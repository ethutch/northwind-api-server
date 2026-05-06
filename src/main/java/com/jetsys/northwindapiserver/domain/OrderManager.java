package com.jetsys.northwindapiserver.domain;

import com.jetsys.northwindapiserver.mapper.order.OrderDetailVO;
import com.jetsys.northwindapiserver.mapper.order.OrderVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OrderManager {
	// Query: paged lists
	@Transactional(readOnly = true)
	Page<OrderVO> findAll(Pageable pageable, boolean includeDetails);

	@Transactional(readOnly = true)
	Optional<OrderVO> findById(Integer id, boolean includeDetails);

	@Transactional(readOnly = true)
	Page<OrderVO> findByCustomerId(String customerId, Pageable pageable, boolean includeDetails);

	// Create: persist header, then details once ID exists
	OrderVO create(OrderVO orderVO);

	// Update: replace header fields and all details
	OrderVO update(Integer id, OrderVO orderVO);

	void delete(Integer id);

	// Detail line operations
	OrderDetailVO addDetail(Integer orderId, OrderDetailVO orderDetailVO);

	OrderDetailVO updateDetail(Integer orderId, OrderDetailVO orderDetailVO);

	void deleteDetail(Integer orderId, Short productId);

	OrderDetailVO createDetail(Integer orderId, OrderDetailVO orderDetailVO);
}
