package com.jetsys.northwindapiserver.service;

import com.jetsys.northwindapiserver.mapper.customer.CustomerRequest;
import com.jetsys.northwindapiserver.mapper.customer.CustomerVO;
import com.jetsys.northwindapiserver.domain.CustomerManager;
import com.jetsys.northwindapiserver.repository.CustomerRepository;
import com.jetsys.northwindapiserver.util.ServiceResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


/**
 * This provides all Customer service features.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerManager customerManager;


	/**
	 * Find all page.
	 *
	 * @param pageable the pageable
	 * @return the page
	 */
	@Override
	public Page<CustomerVO> findAll(Pageable pageable) {

		return customerRepository.findAll(pageable)
				.map(CustomerVO::fromEntity);
	}

	/**
	 * Find by id optional.
	 *
	 * @param id the id
	 * @return the optional
	 */
	@Override
	public Optional<CustomerVO> findById(String id) {
		return customerRepository.findById(id)
				.map(CustomerVO::fromEntity);
	}

	/**
	 * Save customer vo.
	 *
	 * @param customerRequest the customer Request
	 * @return the customer vo
	 */
	@Override
	@Transactional
	public ServiceResult<CustomerVO> create(CustomerRequest customerRequest) {

		var customerVO = CustomerVO.fromRequest(customerRequest);
		return customerManager.create(customerVO);

	}


	/**
	 * Update an existing customer
	 *
	 * @param id PK
	 * @param customerRequest the full customer request object
	 * @return The updated VO
	 */
	@Override
	@Transactional
	public CustomerVO update(String id, CustomerRequest customerRequest) {

		var customerVO = CustomerVO.fromRequest(customerRequest);
		return customerManager.update(customerVO);

	}


	/**
	 * Delete by id.
	 *
	 * @param id the id
	 */
	@Override
	@Transactional
	public void deleteById(String id) {
		customerRepository.deleteById(id);
	}

}
