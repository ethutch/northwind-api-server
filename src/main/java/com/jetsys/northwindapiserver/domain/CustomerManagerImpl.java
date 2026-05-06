package com.jetsys.northwindapiserver.domain;

import com.jetsys.northwindapiserver.mapper.customer.CustomerOutboxMapper;
import com.jetsys.northwindapiserver.mapper.customer.CustomerVO;
import com.jetsys.northwindapiserver.model.Customer;
import com.jetsys.northwindapiserver.model.OutboxAction;
import com.jetsys.northwindapiserver.repository.CustomerRepository;
import com.jetsys.northwindapiserver.repository.OutboxRepository;
import com.jetsys.northwindapiserver.service.OutboxService;
import com.jetsys.northwindapiserver.util.ServiceResult;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The CustomerManager handles all validation and manipulation of Customers
 */

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomerManagerImpl implements CustomerManager {

	private final CustomerRepository customerRepository;
	private final OutboxService outboxService;
	private final CustomerOutboxMapper customerOutboxMapper;


	/**
	 * Find all customers based an a page.
	 *
	 * @param pageable the pageable
	 * @return the page
	 */
	@Override
	public Page<CustomerVO> findAll(Pageable pageable) {

		log.error("HERE IS A MESSAGE"); // Testing for Loki
		return customerRepository.findAll(pageable)
				.map(CustomerVO::fromEntity);
	}


	/**
	 * Find a specific Customer.
	 *
	 * @param id the id
	 * @return the optional
	 */
	@Override
	public Optional<CustomerVO> findById(String id) {
		log.error("HERE IS A MESSAGE"); // Testing for Loki
		return customerRepository.findById(id)
				.map(CustomerVO::fromEntity);
	}


	/**
	 * Insert a new customer.
	 * We will null out the audit fields before return because
	 * Hibernate does not refresh the in-memory entity after the save()
	 * The only way we could get the real data is to do another DB Read.
	 *
	 * @param customerVO the customer request to be inserted
	 * @return the customerVO
	 */
	public ServiceResult<CustomerVO> create(CustomerVO customerVO) {

		return customerRepository.findById(customerVO.customerId())
				.map(existingCustomer -> ServiceResult.alreadyExisted(CustomerVO.fromEntity(existingCustomer)))
				.orElseGet(() ->  {
					Customer customer = Customer.builder()
							.customerId(customerVO.customerId())
							.companyName(customerVO.companyName())
							.contactName(customerVO.contactName())
							.contactTitle(customerVO.contactTitle())
							.address(customerVO.address())
							.city(customerVO.city())
							.region(customerVO.region())
							.postalCode(customerVO.postalCode())
							.country(customerVO.country())
							.phone(customerVO.phone())
							.fax(customerVO.fax())
							.build();
					var persistedCustomer = customerRepository.save(customer);

					// Save updated customer to outbox for Kafka publishing
					outboxService.publish(persistedCustomer, OutboxAction.CREATE, customerOutboxMapper);
					return ServiceResult.created(
							CustomerVO.fromEntity(persistedCustomer));
				});
	}


	/**
	 * Update an existing customer
	 * We will null out the audit fields before return because
	 * Hibernate does not refresh the in-memory entity after the save()
	 * The only way we could get the real data is to do another DB Read.
	 *
	 * @param customerVO       the customer request to update an existing entry
	 * @return          new customerVO State
	 */
	@Override
	public CustomerVO update(CustomerVO customerVO) {
		Customer customer = customerRepository.findById(customerVO.customerId())
				.orElseThrow(() -> new EntityNotFoundException("Customer with id:" + customerVO.customerId() + "not found to update"));
		applyUpdate(customer, customerVO);
		log.info("READ CUSTOMER NEW:{}", customer.isNew());
		var persistedCustomer = customerRepository.save(customer);
		log.info("UPDATED CUSTOMER NEW:{}", persistedCustomer.isNew());

		// Save deleted customer to outbox for Kafka publishing
		outboxService.publish(persistedCustomer, OutboxAction.UPDATE, customerOutboxMapper);
		return CustomerVO.fromEntity(persistedCustomer);

	}

	/**
	 * Delete a single customer.
	 *
	 * @param id the id
	 */
	@Override
	public void deleteById(String id) {

		var customer = customerRepository.findById(id);
		if (customer.isEmpty()) {
			throw new EntityNotFoundException("Customer with id:" + id + "not found to delete");
		} else {
			customerRepository.deleteById(id);
			// Save new customer to outbox for Kafka publishing
			outboxService.publish(customer.get(), OutboxAction.DELETE, customerOutboxMapper);
		}
	}


	private boolean existsById(String id) {
		return customerRepository.existsById(id);
	}

	/**
	 *  UPDATE the formal parameter Customer
	 *  
	 * @param cust  The persisted entity to update
	 * @param customerVO   The input data of new attributes
	 */
	private void applyUpdate(Customer cust, CustomerVO customerVO) {
		
		cust.setCustomerId(customerVO.customerId());
		cust.setContactName(customerVO.contactName());
		cust.setContactTitle(customerVO.contactTitle());
		cust.setAddress(customerVO.address());
		cust.setCity(customerVO.city());
		cust.setRegion(customerVO.region());
		cust.setPostalCode(customerVO.postalCode());
		cust.setCountry(customerVO.country());
		cust.setPhone(customerVO.phone());
		cust.setFax(customerVO.fax());

	}

}
