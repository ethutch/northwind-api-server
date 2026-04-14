package com.jetsys.northwindapiserver.domain;

import com.jetsys.northwindapiserver.mapper.order.OrderDetailVO;
import com.jetsys.northwindapiserver.mapper.order.OrderOutboxMapper;
import com.jetsys.northwindapiserver.model.Order;
import com.jetsys.northwindapiserver.model.OrderDetail;
import com.jetsys.northwindapiserver.model.OrderDetailId;
import com.jetsys.northwindapiserver.model.OutboxAction;
import com.jetsys.northwindapiserver.repository.OrderDetailRepository;
import com.jetsys.northwindapiserver.repository.OrderRepository;
import com.jetsys.northwindapiserver.mapper.order.OrderVO;
import com.jetsys.northwindapiserver.repository.ProductRepository;
import com.jetsys.northwindapiserver.service.OutboxService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Log4j2
public class OrderManagerImpl implements OrderManager {

	private final OrderRepository orderRepository;
	private final OrderDetailRepository orderDetailRepository;
	private final ProductRepository productRepository;
	private final CustomerManager customerManager;
	private final OrderOutboxMapper orderOutboxMaper;
	private final OutboxService outboxService;


	/**
	 * Get all orders.  Really dubiously useful function
	 * @param pageable  the page control
	 * @return          the requested page
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<OrderVO> findAll(Pageable pageable) {
		return orderRepository.findAll(pageable).map(OrderVO::fromEntity);
	}

	/**
	 * Get a specific order by ID
	 * @param id    The order PK
	 * @return      The order
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<OrderVO> findById(Integer id) {

		return orderRepository.findById(id).map(OrderVO::fromEntity);

	}

	/**
	 * Get orderf for a given customer
	 *
	 * @param customerId    The customer Id
	 * @param pageable      Page control info
	 * @return              The page of results
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<OrderVO> findByCustomerId(String customerId, Pageable pageable) {
		log.debug("Finding orders by customer id {}", customerId);
		return orderRepository.findByCustomerId(customerId, pageable).map(OrderVO::fromEntity);
	}

	/**
	 * Insert a new order into the database.
	 * <p>
	 * Note how the customer ID is validated using the customerManager object rather than a direct read against the database.
	 * Never cross domain data ownership.  Even for reads.
	 * <p>
	 * Also note that when a find normally fails an empty Optional is returned.  This gives the caller maximum flexibility to
	 * handle the condition without forcing it directly into an exception path.  Conversely if the Customer is not found when
	 * attempting to insert an order this means the data store is in, or attempting to be put in, an inconsistent state.  Thus
	 * throwing an exception becomes the correct response.  Unless the customer was deleted by one process simultaneously while
	 * another process was adding the order this can only happen due to a external caller error.  That makes it one of the
	 * infamous this can never happen sorts of errors.
	 * 
	 * @param orderVO       The new order to add
	 * @return              The persisted VO version of the order that was created
	 * @throws              EntityNotFoundException if the embedded Customer Id is invalid
	 */
	// Create: persist header, then details once ID exists
	@Override
	public OrderVO create(OrderVO orderVO) {

		var customer = customerManager.findById(orderVO.customerId());
		if (customer.isEmpty()) {
			log.debug("Customer:{} not found attempting to create new order", orderVO.customerId());
			throw new EntityNotFoundException("Customer with id:" + orderVO.customerId() + "not found.");
		}

		Order order = new Order();
		populateOrderHeader(order, orderVO);
		// save order first to get generated ID
		Order persistedOrder = orderRepository.save(order);

		// attach details with the generated order ID
		populateOrderDetails(persistedOrder, orderVO);

		// cascade will save details
		Order saved = orderRepository.save(persistedOrder);

		// Save updated order to outbox for Kafka publishing
		outboxService.publish(saved, OutboxAction.CREATE, orderOutboxMaper);
		return OrderVO.fromEntity(saved);
	}


	/**
	 * Update true to its PUT nature is a full replacement of the logical object.  Specifically that means the
	 * Order and all its details.
	 * <p>
	 * Once again if the read of the base order fails then it was likely deleted in a race condition.  We are
	 * making the conscious choice to give up and throw an exception.
	 *
	 * @param id        The orderId
	 * @param orderVO   Request data in VO rationalized format
	 * @return          New OrderVO for replaced order
	 */
	@Override
	public OrderVO update(Integer id, OrderVO orderVO) {
		Order existingOrder = orderRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Order not found:" + id));

		// Changing the customer would be extreme but is supported
		if (!existingOrder.getCustomerId().equals(orderVO.customerId())) {
			// Log something really unusual
			log.info("Change customer for existing order:{} from:{} to {}",
					existingOrder.getId(), existingOrder.getCustomerId(), orderVO.customerId());
			var customer = customerManager.findById(orderVO.customerId());
			if (customer.isEmpty()) {
				throw new EntityNotFoundException("Customer with id:" + orderVO.customerId() + "not found.");
			}
		}

		populateOrderHeader(existingOrder, orderVO);
		// Replace details with provided set
		existingOrder.getOrderDetails().clear();
		// Delete existing detail lines
		orderDetailRepository.deleteByIdOrderId(existingOrder.getId());
		populateOrderDetails(existingOrder, orderVO);

		var newPersistedOrder = orderRepository.save(existingOrder);
		outboxService.publish(newPersistedOrder, OutboxAction.UPDATE, orderOutboxMaper);
		return OrderVO.fromEntity(newPersistedOrder);
	}

	/**
	 * Delte an order
	 * @param id        The PK of the order to delete
	 */
	@Override
	public void delete(Integer id) {

		var order = orderRepository.findById(id);
		if (order.isEmpty()) {
			throw new EntityNotFoundException("Order with id:" + id + "not found to delete");
		} else {
			orderRepository.deleteById(id);
			// Save deleted order to outbox for Kafka publishing
			outboxService.publish(order.get(), OutboxAction.DELETE, orderOutboxMaper);
		}
	}

	/*
	 *   Detail line operations
	 */

	/**
	 * Add a new line item to an order
	 *
	 * @param orderId           The order Id
	 * @param orderDetailVO     The new line to add
	 * @return                  Persisted version of the new line
	 */
	@Override
	public OrderDetailVO addDetail(Integer orderId, OrderDetailVO orderDetailVO) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found"));

		OrderDetail detail = new OrderDetail();
		OrderDetailId id = new OrderDetailId();
		id.setOrderId(order.getId());
		id.setProductId(orderDetailVO.productId());
		if (orderDetailRepository.existsById(id)) {
			throw new IllegalStateException("OrderDetail already exists for orderId="
					+ orderId + ", productId=" + orderDetailVO.productId());
		}

		detail.setId(id);
		detail.setOrder(order);
		detail.setUnitPrice(orderDetailVO.unitPrice());
		detail.setQuantity(orderDetailVO.quantity());
		detail.setDiscount(orderDetailVO.discount());
		order.getOrderDetails().add(detail);

		OrderDetail saved = orderDetailRepository.save(detail);
		// Save updated order to outbox for Kafka publishing
		outboxService.publish(order, OutboxAction.UPDATE, orderOutboxMaper);
		return OrderDetailVO.fromEntity(saved);
	}


	/**
	 * Allow to update attributes of an existing detail line.  Do not support changing the item itself
	 *
	 * @param orderId           The order key
	 * @param orderDetailVO     This one line
	 * @return                  return just the one line that was changed
	 */
	@Override
	public OrderDetailVO updateDetail(Integer orderId, OrderDetailVO orderDetailVO) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new EntityNotFoundException("Order with id:" + orderId + "not found to update"));

		OrderDetailId id = new OrderDetailId();
		id.setOrderId(orderId);
		id.setProductId(orderDetailVO.productId());

		OrderDetail detail = orderDetailRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Order detail with id:" + id + "not found"));
		order.getOrderDetails().remove(detail);

		detail.setUnitPrice(orderDetailVO.unitPrice());
		detail.setQuantity(orderDetailVO.quantity());
		detail.setDiscount(orderDetailVO.discount());
		order.getOrderDetails().add(detail);

		OrderDetail saved = orderDetailRepository.save(detail);
		// Save updated order to outbox for Kafka publishing
		outboxService.publish(order, OutboxAction.UPDATE, orderOutboxMaper);

		return OrderDetailVO.fromEntity(saved);
	}


	/**
	 * Allow to delete a single line from an order
	 *
	 * @param orderId       The order key
	 * @param productId     The product to remove.
	 */
	@Override
	public void deleteDetail(Integer orderId, Short productId) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new EntityNotFoundException("Order with id:" + orderId + "not found"));

		OrderDetailId id = new OrderDetailId();
		id.setOrderId(orderId);
		id.setProductId(productId);

		boolean removed = order.getOrderDetails().removeIf(detail -> detail.getId().equals(id));
		if (!removed) {
			throw new EntityNotFoundException("Order detail not found: order="
					+ orderId + " product=" + productId);
		}

		orderDetailRepository.deleteById(id);

		outboxService.publish(order, OutboxAction.DELETE, orderOutboxMaper);
	}


	/**
	 * Create a new orderDetail entity from VO input and save it to the datastore
	 * @param orderId           The order Id
	 * @param orderDetailVO     The line to save
	 * @return                  The new saved line
	 */
	@Override
	public OrderDetailVO createDetail(Integer orderId, OrderDetailVO orderDetailVO) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new EntityNotFoundException("Order with id:" + orderId + "not found"));

		OrderDetailId id = new OrderDetailId();
		id.setOrderId(orderId);
		id.setProductId(orderDetailVO.productId());

		boolean exists = order.getOrderDetails().stream()
				.anyMatch(detail -> detail.getId().equals(id));

		if (exists) {
			throw new IllegalStateException("Order detail already exists: order="
					+ orderId + " product=" + orderDetailVO.productId());
		}

		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setId(id);
		orderDetail.setUnitPrice(orderDetailVO.unitPrice());
		orderDetail.setQuantity(orderDetailVO.quantity());
		orderDetail.setDiscount(orderDetailVO.discount());
		order.getOrderDetails().add(orderDetail);

		OrderDetail saved = orderDetailRepository.save(orderDetail);
		outboxService.publish(order, OutboxAction.UPDATE, orderOutboxMaper);

		return OrderDetailVO.fromEntity(saved);
	}


	// helpers
	private void populateOrderHeader(Order order, OrderVO orderVO) {
		order.setCustomerId(orderVO.customerId());
		order.setEmployeeId(orderVO.employeeId());
		order.setOrderDate(orderVO.orderDate());
		order.setRequiredDate(orderVO.requiredDate());
		order.setShippedDate(orderVO.shippedDate());
		order.setFreight(orderVO.freight());
		order.setShipName(orderVO.shipName());
		order.setShipAddress(orderVO.shipAddress());
		order.setShipCity(orderVO.shipCity());
		order.setShipRegion(orderVO.shipRegion());
		order.setShipPostalCode(orderVO.shipPostalCode());
		order.setShipCountry(orderVO.shipCountry());
	}


	private void populateOrderDetails(Order order, OrderVO orderVO) {
		for (OrderDetailVO orderDetailVO : orderVO.orderDetails()) {
			OrderDetailId orderDetailId = OrderDetailId.builder()
					.orderId(order.getId())
					.productId(orderDetailVO.productId())
					.build();

			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setId(orderDetailId);
			orderDetail.setOrder(order);
			orderDetail.setProduct(productRepository.getReferenceById(orderDetailVO.productId()));
			orderDetail.setUnitPrice(orderDetailVO.unitPrice());
			orderDetail.setQuantity(orderDetailVO.quantity());
			orderDetail.setDiscount(orderDetailVO.discount());
			order.getOrderDetails().add(orderDetail);
		}
	}

}
