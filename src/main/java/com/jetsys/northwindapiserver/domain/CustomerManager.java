package com.jetsys.northwindapiserver.domain;

import com.jetsys.northwindapiserver.mapper.customer.CustomerVO;
import com.jetsys.northwindapiserver.util.ServiceResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface CustomerManager {


	Page<CustomerVO> findAll(Pageable pageable);


	Optional<CustomerVO> findById(String id);


	ServiceResult<CustomerVO> create(CustomerVO req);

	CustomerVO update(CustomerVO req);

	void deleteById(String id);
}
