package com.jetsys.northwindapiserver.controller;

import com.jetsys.northwindapiserver.mapper.order.OrderDetailRequest;
import com.jetsys.northwindapiserver.mapper.order.OrderRequest;
import com.jetsys.northwindapiserver.model.Customer;
import com.jetsys.northwindapiserver.model.Order;
import com.jetsys.northwindapiserver.service.OrderService;
import com.jetsys.northwindapiserver.service.OrderServiceImpl;
import com.jetsys.northwindapiserver.mapper.order.OrderDetailVO;
import com.jetsys.northwindapiserver.mapper.order.OrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderControllerTest {

	private OrderService orderService;
	private OrderController orderController;

	OrderDetailVO detailVO = new OrderDetailVO((short)1, 9.99f, (short)10,
			0f, null, null, null, null);
	OrderDetailRequest orderDetailRequest = new OrderDetailRequest((short)1, 9.99f, (short)10, 0f);


	OrderVO orderVO = new OrderVO(1,"ALFKI", (short) 1,LocalDate.now(),
			LocalDate.now(),LocalDate.now(), 2F,"shipname","shipAddress","shipCity", "US", "90210", "US", null, null,
			null, null, Set.of(detailVO));


	OrderRequest orderRequest = new OrderRequest(
			null,
			"ALFKI",
			(short) 1,
			LocalDate.now(),
			LocalDate.now().plusDays(7),
			LocalDate.now().plusDays(2),
			15.0f,
			"Test Ship",
			"123 Street",
			"City",
			"Region",
			"12345",
			"USA",
			Set.of(orderDetailRequest)
	);

	@BeforeEach
	void setUp() {
		orderService = mock(OrderServiceImpl.class);
		orderController = new OrderControllerImpl(orderService);
	}

	@Test
	void testGetByCustomerReturnsPagedOrders() {
		Customer customer = new Customer();
		customer.setCustomerId("ALFKI");



		Page<OrderVO> page = new PageImpl<>(List.of(orderVO), PageRequest.of(0, 20), 1);
		when(orderService.getOrdersByCustomer(anyString(), any(Pageable.class))).thenReturn(page);

		ResponseEntity<?> response = orderController.getOrdersByCustomer("ALFKI", Pageable.ofSize(1));

		assertNotNull(response);
		assertInstanceOf(Page.class, response.getBody());
	}

	@Test
	void testGetOneNotFound() {

		when(orderService.getOrder(1)).thenThrow( new NoSuchElementException("Order not found"));

		assertThrowsExactly(NoSuchElementException.class,
				() -> orderController.getOrder(1));
	}

	@Test
	void testGetOneFound() {
		when(orderService.getOrder(1)).thenReturn(orderVO);

		ResponseEntity<OrderVO> response = orderController.getOrder(1);

		assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
	}

	@Test
	void testCreateOrder() {

		when(orderService.createOrder(orderRequest)).thenReturn(orderVO);

		ResponseEntity<?> response = orderController.createOrder(orderRequest);

		assertNotNull(response);
		assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode());
	}

	@Test
	void testUpdateOrder() {

		Order updated = new Order();
		updated.setId(1);
		updated.setCustomer(new Customer());
		updated.setOrderDate(LocalDate.now());

		when(orderService.updateOrder(1, orderRequest)).thenReturn(orderVO);

		ResponseEntity<?> response = orderController.updateOrder(1, orderRequest);

		assertNotNull(response);
		assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
	}

	@Test
	void testDeleteOrder() {
		doNothing().when(orderService).deleteOrder(1);

		ResponseEntity<Void> response = orderController.deleteOrder(1);

		assertNotNull(response);
		assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
		verify(orderService, times(1)).deleteOrder(1);
	}
}
