package com.jetsys.northwindapiserver.service;

import com.jetsys.northwindapiserver.mapper.customer.CustomerRequest;
import com.jetsys.northwindapiserver.mapper.customer.CustomerVO;
import com.jetsys.northwindapiserver.util.ServiceResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface CustomerService {

	Page<CustomerVO> findAll(Pageable pageable);


	Optional<CustomerVO> findById(String id);


	ServiceResult<CustomerVO> create(CustomerRequest customer);

	CustomerVO update(String id, CustomerRequest customer);


	void deleteById(String id);

}
