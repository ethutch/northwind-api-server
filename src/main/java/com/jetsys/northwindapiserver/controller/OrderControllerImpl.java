package com.jetsys.northwindapiserver.controller;

import com.jetsys.northwindapiserver.mapper.order.OrderDetailRequest;
import com.jetsys.northwindapiserver.mapper.order.OrderDetailVO;
import com.jetsys.northwindapiserver.mapper.order.OrderRequest;
import com.jetsys.northwindapiserver.mapper.order.OrderVO;
import com.jetsys.northwindapiserver.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/orders")
public class OrderControllerImpl implements OrderController {

		private final OrderService orderService;


	/**
	 * Get all orders. Not the most practical of all endpoints
	 *
	 * @param pageable  Pageable restrictions.
	 * @param includeDetails    Control loading of detail lines
	 * @return          Page of all orders.
	 */
		@Override
		@PreAuthorize("hasRole('USER')")
		@GetMapping
		public ResponseEntity<Page<OrderVO>> getAllOrders(Pageable pageable,
				@RequestParam(name = "include_details", defaultValue = "false") boolean includeDetails) {

			// Make this an info line to help monitor if anyone ever uses it
			log.info("getAllOrders called");
			Page<OrderVO> orders = orderService.getAllOrders(pageable, includeDetails);
			return ResponseEntity.ok(orders);
		}

	/**
	 * Get a specific order by ID
	 *
	 * @param id    The Order ID
	 * @param includeDetails   Control loading of detail lines
	 * @return      OrderVO Requested Order
	 */
		@Override
		@PreAuthorize("hasRole('USER')")
		@GetMapping("/{id}")
		public ResponseEntity<OrderVO> getOrder(@PathVariable Integer id,
				@RequestParam(name = "include_details", defaultValue = "false") boolean includeDetails)  {

			log.debug("Get order by id {}", id);
			OrderVO order = orderService.getOrder(id, includeDetails);
			return ResponseEntity.ok(order);
		}

	/**
	 * Get the orders for a given customer
	 * @param customerId        The customer
	 * @param pageable          Paging control
	 * @param includeDetails    Control loading of detail lines
	 * @return                  A page of results
	 */
		@Override
		@PreAuthorize("hasRole('USER')")
		@GetMapping("/customer/{customerId}")
		public ResponseEntity<Page<OrderVO>> getOrdersByCustomer(@PathVariable String customerId,
				Pageable pageable, @RequestParam(name = "include_details", defaultValue = "false") boolean includeDetails)  {

			log.debug("Get orders for customer id {}", customerId);
			Page<OrderVO> orders = orderService.getOrdersByCustomer(customerId, pageable, includeDetails);
			return ResponseEntity.ok(orders);
		}

	/**
	 * Creat a new order.
	 *
	 * @param orderRequest  The full order complete with customer shipping info and detail lines
	 * @return  OrderVO The persisted version including the OneToMany orderLines
	 */
		@Override
		@PreAuthorize("hasRole('USER')")
		@PostMapping
		public ResponseEntity<OrderVO> createOrder(@Valid @RequestBody OrderRequest orderRequest) {

			log.debug("Create order for Customer:{}", orderRequest.customerId());
			OrderVO created = orderService.createOrder(orderRequest);

			log.debug("Created new order {}", created.orderId());
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		}

	/**
	 * Completely replace an existing order, including all detail lines
	 * @param id        The order Id
	 * @param orderRequest  The new order to replace existing
	 * @return  The new persisted order
	 */
		@Override
		@PreAuthorize("hasRole('USER')")
		@PutMapping("/{id}")
		public ResponseEntity<OrderVO> updateOrder(@PathVariable Integer id,
				@Valid @RequestBody OrderRequest orderRequest) {

			log.debug("Replace existing order:{} for Customer:{}", orderRequest.orderId(),
					orderRequest.customerId());
			OrderVO updated = orderService.updateOrder(id, orderRequest);
			return ResponseEntity.ok(updated);
		}

	/**
	 * Delete an existing order
	 * @param id        The order to delete
	 * @return          Response showing delete successful
	 */
		@Override
		@PreAuthorize("hasRole('USER')")
		@DeleteMapping("/{id}")
		public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {

			log.debug("Delete order by id {}", id);
			orderService.deleteOrder(id);
			return ResponseEntity.noContent().build();
		}

	/**
	 * Add another line item to an existing order
	 * @param orderId            The Order ID
	 * @param orderDetailRequest The new line to add.
	 * @return                   The persisted line.
	 */
		@Override
		@PreAuthorize("hasRole('USER')")
		@PostMapping("/{orderId}/details")
		public ResponseEntity<OrderDetailVO> addDetail(@PathVariable Integer orderId,
				@Valid @RequestBody OrderDetailRequest orderDetailRequest) {

			log.debug("Add new line to order:{} for product:{}",
					orderId, orderDetailRequest.productId());
			OrderDetailVO createdDetail = orderService.addDetail(orderId, orderDetailRequest);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdDetail);
		}

	/**
	 * Replace an existing line with a new one. This is really only good for changing quantity etc.
	 * @param orderId            The Order ID
	 * @param productId          The product but it is part of compound key for detail.
	 * @param orderDetailRequest The new detail line data
	 * @return                   The new persisted detail line data
	 */
		@Override
		@PreAuthorize("hasRole('USER')")
		@PutMapping("/{orderId}/details/{productId}")
		public ResponseEntity<OrderDetailVO> updateDetail(@PathVariable Integer orderId,
				@PathVariable Short productId,
				@Valid @RequestBody OrderDetailRequest orderDetailRequest) {
			OrderDetailVO updatedDetail = orderService.updateDetail(orderId, orderDetailRequest);
			return ResponseEntity.ok(updatedDetail);
		}

	/**
	 * Delete an order line
	 * @param orderId       The order Id
	 * @param productId     The productID makes up the detail line item PK
	 * @return              Successful deletion notification
	 */
		@Override
		@PreAuthorize("hasRole('USER')")
		@DeleteMapping("/{orderId}/details/{productId}")
		public ResponseEntity<Void> deleteDetail(@PathVariable Integer orderId,
				@PathVariable Short productId) {
			orderService.deleteDetail(orderId, productId);
			return ResponseEntity.noContent().build();
		}
	}
