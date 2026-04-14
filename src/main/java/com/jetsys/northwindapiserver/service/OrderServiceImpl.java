package com.jetsys.northwindapiserver.service;

import com.jetsys.northwindapiserver.mapper.order.OrderDetailRequest;
import com.jetsys.northwindapiserver.mapper.order.OrderDetailVO;
import com.jetsys.northwindapiserver.mapper.order.OrderRequest;
import com.jetsys.northwindapiserver.mapper.order.OrderVO;
import com.jetsys.northwindapiserver.domain.OrderManager;
import com.jetsys.northwindapiserver.model.OrderDetailId;
import com.jetsys.northwindapiserver.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final OrderManager orderManager;


	/**
	 * Get all orders
	 * @param pageable  Pageable query control
	 * @return          Result of all orders
	 */
	@Override
	public Page<OrderVO> getAllOrders(Pageable pageable) {
		return orderManager.findAll(pageable);
	}

	/**
	 * Get an order by ID
	 * @param id        The order Id
	 * @return          The order complete with detail lines
	 */
	@Override
	public OrderVO getOrder(Integer id) {
		return orderManager.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Order not found"));
	}

	/**
	 * Create a new order complete with all details
	 * @param orderRequest  The external input of order and details
	 * @return              The persisted view of new order
	 */
	@Override
	public OrderVO createOrder(OrderRequest orderRequest) {

		return orderManager.create(OrderVO.fromRequest(orderRequest));
	}

	/**
	 * Full replacement of an existing order.
	 * @param id            The order ID
	 * @param orderRequest  The external representation of the order
	 * @return              The normalized representation of the order
	 */
	@Override
	public OrderVO updateOrder(Integer id, OrderRequest orderRequest) {
		if (!orderRepository.existsById(id)) {
			throw new NoSuchElementException("Order not found");
		}
		return orderManager.update(id, OrderVO.fromRequest(orderRequest));
	}

	/**
	 * Delete an order by ID
	 * @param id        The ID to delete
	 */
	@Override
	public void deleteOrder(Integer id) {
		orderManager.delete(id);
	}

	/**
	 * Get all the orders for a given customer
	 * @param customerId        The customer to retrieve
	 * @param pageable          Sort and paging control
	 * @return                  A page of orders
	 */
	@Override
	public Page<OrderVO> getOrdersByCustomer(String customerId, Pageable pageable) {
		return orderManager.findByCustomerId(customerId, pageable);
	}

	/**
	 * Add a detail line to an order
	 * @param orderId               The existing order to update
	 * @param orderDetailRequest    The new line to add
	 * @return                      Normalized view of the new detail line
	 */
	@Override
	public OrderDetailVO addDetail(Integer orderId, OrderDetailRequest orderDetailRequest) {

		var orderDetailVO = OrderDetailVO.fromRequest(orderDetailRequest);
		return orderManager.addDetail(orderId, orderDetailVO);
	}

	/**
	 * Update an existing detail line
	 * @param orderId               The order to update
	 * @param orderDetailRequest    The new data.  NOTE: Product ID part of compound key for details
	 * @return                      The new detail line
	 */
	@Override
	public OrderDetailVO updateDetail(Integer orderId, OrderDetailRequest orderDetailRequest) {
		var orderDetailVO = OrderDetailVO.fromRequest(orderDetailRequest);
		return orderManager.updateDetail(orderId, orderDetailVO);
	}

	/**
	 * Delete a detail line from an order
	 * @param orderId       The order Id
	 * @param productId     The line to delete
	 */
	@Override
	public void deleteDetail(Integer orderId, Short productId) {
		OrderDetailId id = new OrderDetailId();
		id.setOrderId(orderId);
		id.setProductId(productId);
		orderManager.deleteDetail(orderId, productId);
	}
}
