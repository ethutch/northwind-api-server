package com.jetsys.northwindapiserver.service;

import com.jetsys.northwindapiserver.domain.OrderManager;
import com.jetsys.northwindapiserver.mapper.order.*;
import com.jetsys.northwindapiserver.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrderServiceTest {

	@Mock
	private OrderManager orderManager;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OutboxService outboxService;

	@InjectMocks
	private OrderServiceImpl orderService;

	private final LocalDate today = LocalDate.now();

	private OrderVO sampleOrderVO;
	private OrderRequest sampleOrderRequest;
	private OrderDetailVO sampleDetailVO;
	private OrderDetailRequest sampleDetailRequest;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		sampleDetailVO = OrderDetailVO.builder()
				.productId((short) 1)
				.unitPrice(12.5f)
				.quantity((short) 2)
				.discount(0f)
				.build();

		sampleDetailRequest = OrderDetailRequest.builder()
				.productId((short) 1)
				.unitPrice(12.5f)
				.quantity((short) 2)
				.discount(0f)
				.build();

		sampleOrderVO = OrderVO.builder()
				.orderId(1)
				.customerId("ALFKI")
				.employeeId((short) 5)
				.orderDate(today)
				.requiredDate(today.plusDays(7))
				.shippedDate(null)
				.freight(10.5f)
				.shipName("Test Ship")
				.shipAddress("123 Street")
				.shipCity("Seattle")
				.shipRegion("WA")
				.shipPostalCode("98101")
				.shipCountry("USA")
				.orderDetails(Set.of(sampleDetailVO))
				.build();

		sampleOrderRequest = new OrderRequest(
				null,
				"ALFKI",
				(short) 5,
				today,
				today.plusDays(7),
				null,
				10.5f,
				"Test Ship",
				"123 Street",
				"Seattle",
				"WA",
				"98101",
				"USA",
				Set.of(sampleDetailRequest)
		);
	}

	// -------------------------------------------------------------------------
	// getAllOrders
	// -------------------------------------------------------------------------

	@Test
	void getAllOrders_returnsPaginatedResults() {
		Page<OrderVO> page = new PageImpl<>(List.of(sampleOrderVO));
		when(orderManager.findAll(any(Pageable.class), anyBoolean())).thenReturn(page);

		Page<OrderVO> result = orderService.getAllOrders(Pageable.unpaged(), false);

		assertEquals(1, result.getTotalElements());
		assertEquals("ALFKI", result.getContent().get(0).customerId());
		verify(orderManager).findAll(any(Pageable.class), anyBoolean());
	}

	@Test
	void getAllOrders_emptyPage_returnsEmptyResult() {
		when(orderManager.findAll(any(Pageable.class),anyBoolean()))
				.thenReturn(new PageImpl<>(List.of()));

		Page<OrderVO> result = orderService.getAllOrders(Pageable.unpaged(), false);

		assertTrue(result.isEmpty());
	}

	// -------------------------------------------------------------------------
	// getOrder
	// -------------------------------------------------------------------------

	@Test
	void getOrder_found_returnsVO() {
		when(orderManager.findById(1, false)).thenReturn(Optional.of(sampleOrderVO));

		OrderVO result = orderService.getOrder(1, false);

		assertEquals("ALFKI", result.customerId());
		verify(orderManager).findById(1, false);
	}

	@Test
	void getOrder_notFound_throwsNoSuchElementException() {
		when(orderManager.findById(99, false)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> orderService.getOrder(99, false));
		verify(orderManager).findById(99, false);
	}

	// -------------------------------------------------------------------------
	// getOrdersByCustomer
	// -------------------------------------------------------------------------

	@Test
	void getOrdersByCustomer_returnsPaginatedResults() {
		Page<OrderVO> page = new PageImpl<>(List.of(sampleOrderVO));
		when(orderManager.findByCustomerId(eq("ALFKI"), any(Pageable.class), anyBoolean()))
				.thenReturn(page);

		Page<OrderVO> result = orderService.getOrdersByCustomer("ALFKI", Pageable.unpaged(), false);

		assertEquals(1, result.getTotalElements());
		verify(orderManager).findByCustomerId(eq("ALFKI"), any(Pageable.class), anyBoolean());
	}

	@Test
	void getOrdersByCustomer_unknownCustomer_returnsEmptyPage() {
		when(orderManager.findByCustomerId(eq("XXXXX"), any(Pageable.class), anyBoolean()))
				.thenReturn(new PageImpl<>(List.of()));

		Page<OrderVO> result = orderService.getOrdersByCustomer("XXXXX", Pageable.unpaged(), false);

		assertTrue(result.isEmpty());
	}

	// -------------------------------------------------------------------------
	// createOrder
	// -------------------------------------------------------------------------

	@Test
	void createOrder_success_returnsCreatedVO() {
		when(orderManager.create(any(OrderVO.class))).thenReturn(sampleOrderVO);

		OrderVO result = orderService.createOrder(sampleOrderRequest);

		assertEquals("ALFKI", result.customerId());
		assertEquals(1, result.orderId());
		verify(orderManager).create(any(OrderVO.class));
	}

	@Test
	void createOrder_managerThrows_propagatesException() {
		when(orderManager.create(any(OrderVO.class)))
				.thenThrow(new RuntimeException("DB failure"));

		assertThrows(RuntimeException.class,
				() -> orderService.createOrder(sampleOrderRequest));
	}

	// -------------------------------------------------------------------------
	// updateOrder
	// -------------------------------------------------------------------------

	@Test
	void updateOrder_exists_returnsUpdatedVO() {
		when(orderRepository.existsById(1)).thenReturn(true);
		when(orderManager.update(eq(1), any(OrderVO.class))).thenReturn(sampleOrderVO);

		OrderVO result = orderService.updateOrder(1, sampleOrderRequest);

		assertEquals("ALFKI", result.customerId());
		verify(orderRepository).existsById(1);
		verify(orderManager).update(eq(1), any(OrderVO.class));
	}

	@Test
	void updateOrder_notFound_throwsNoSuchElementException() {
		when(orderRepository.existsById(99)).thenReturn(false);

		assertThrows(NoSuchElementException.class,
				() -> orderService.updateOrder(99, sampleOrderRequest));

		verify(orderManager, never()).update(any(), any());
	}

	@Test
	void updateOrder_managerThrows_propagatesException() {
		when(orderRepository.existsById(1)).thenReturn(true);
		when(orderManager.update(eq(1), any(OrderVO.class)))
				.thenThrow(new RuntimeException("DB failure"));

		assertThrows(RuntimeException.class,
				() -> orderService.updateOrder(1, sampleOrderRequest));
	}

	// -------------------------------------------------------------------------
	// deleteOrder
	// -------------------------------------------------------------------------

	@Test
	void deleteOrder_success_delegatesToManager() {
		doNothing().when(orderManager).delete(1);

		orderService.deleteOrder(1);

		verify(orderManager).delete(1);
	}

	@Test
	void deleteOrder_managerThrows_propagatesException() {
		doThrow(new RuntimeException("Delete failed"))
				.when(orderManager).delete(99);

		assertThrows(RuntimeException.class, () -> orderService.deleteOrder(99));
	}

	// -------------------------------------------------------------------------
	// addDetail
	// -------------------------------------------------------------------------

	@Test
	void addDetail_success_returnsDetailVO() {
		when(orderManager.addDetail(eq(1), any(OrderDetailVO.class)))
				.thenReturn(sampleDetailVO);

		OrderDetailVO result = orderService.addDetail(1, sampleDetailRequest);

		assertEquals((short) 1, result.productId());
		verify(orderManager).addDetail(eq(1), any(OrderDetailVO.class));
	}

	@Test
	void addDetail_orderNotFound_propagatesException() {
		when(orderManager.addDetail(eq(99), any(OrderDetailVO.class)))
				.thenThrow(new NoSuchElementException("Order not found"));

		assertThrows(NoSuchElementException.class,
				() -> orderService.addDetail(99, sampleDetailRequest));
	}

	// -------------------------------------------------------------------------
	// updateDetail
	// -------------------------------------------------------------------------

	@Test
	void updateDetail_success_returnsUpdatedVO() {
		when(orderManager.updateDetail(eq(1), any(OrderDetailVO.class)))
				.thenReturn(sampleDetailVO);

		OrderDetailVO result = orderService.updateDetail(1, sampleDetailRequest);

		assertEquals((short) 1, result.productId());
		verify(orderManager).updateDetail(eq(1), any(OrderDetailVO.class));
	}

	@Test
	void updateDetail_detailNotFound_propagatesException() {
		when(orderManager.updateDetail(eq(1), any(OrderDetailVO.class)))
				.thenThrow(new NoSuchElementException("Detail not found"));

		assertThrows(NoSuchElementException.class,
				() -> orderService.updateDetail(1, sampleDetailRequest));
	}

	// -------------------------------------------------------------------------
	// deleteDetail
	// -------------------------------------------------------------------------

	@Test
	void deleteDetail_success_delegatesToManager() {
		doNothing().when(orderManager).deleteDetail(1, (short) 1);

		orderService.deleteDetail(1, (short) 1);

		verify(orderManager).deleteDetail(1, (short) 1);
	}

	@Test
	void deleteDetail_notFound_propagatesException() {
		doThrow(new NoSuchElementException("Detail not found"))
				.when(orderManager).deleteDetail(eq(1), eq((short) 99));

		assertThrows(NoSuchElementException.class,
				() -> orderService.deleteDetail(1, (short) 99));
	}
}