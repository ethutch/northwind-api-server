package com.jetsys.northwindapiserver.service;

import com.jetsys.northwindapiserver.mapper.order.OrderDetailRequest;
import com.jetsys.northwindapiserver.mapper.order.OrderDetailVO;
import com.jetsys.northwindapiserver.mapper.order.OrderRequest;
import com.jetsys.northwindapiserver.mapper.order.OrderVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
	Page<OrderVO> getAllOrders(Pageable pageable);

	OrderVO getOrder(Integer id);

	OrderVO createOrder(OrderRequest orderRequest);

	OrderVO updateOrder(Integer id, OrderRequest orderRequest);

	void deleteOrder(Integer id);

	Page<OrderVO> getOrdersByCustomer(String customerId, Pageable pageable);

	OrderDetailVO addDetail(Integer orderId, OrderDetailRequest orderDetailRequest);

	OrderDetailVO updateDetail(Integer orderId, OrderDetailRequest orderDetailRequest);

	void deleteDetail(Integer orderId, Short productId);
}
