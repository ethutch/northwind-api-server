package com.jetsys.northwindapiserver.repository;

import com.jetsys.northwindapiserver.model.OrderDetail;
import com.jetsys.northwindapiserver.model.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {


	void deleteByIdOrderId(Integer id);
}
