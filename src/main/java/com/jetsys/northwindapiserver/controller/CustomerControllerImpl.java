package com.jetsys.northwindapiserver.controller;

import com.jetsys.northwindapiserver.mapper.customer.CustomerRequest;
import com.jetsys.northwindapiserver.mapper.customer.CustomerVO;
import com.jetsys.northwindapiserver.service.CustomerService;
import com.jetsys.northwindapiserver.util.ServiceResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.jetsys.northwindapiserver.NorthwindApiServerApplication.SECURITY;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customer")
public class CustomerControllerImpl implements CustomerController {

	private final CustomerService customerService;


	/**
	 * Get paged list of customers
	 *
	 * @param pageable where to start
	 * @return Page of CustomerVOs
	 */
	@Override
	@PreAuthorize("hasRole('USER')")
	@GetMapping
	public Page<CustomerVO> getAll(
			@PageableDefault(page = 0, size = 20) Pageable pageable) {

		log.debug("Get all customers entry");
		Page<CustomerVO> customerPage = customerService.findAll(pageable);

		return customerPage;
	}


	/**
	 * Get a single Customer by Id
	 * @param id    PK of Customer
	 * @return CustomerVO
	 */
	@Override
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{id}")
	public ResponseEntity<CustomerVO> getCustomer(@PathVariable String id) {

		log.debug("Get single customer:{} entry", id);
		return customerService.findById(id)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound()
						.build());
	}


	/**
	 * Create a new customer
	 * @param                   request CustomerRequest payload
	 * @return CustomerVO       201 if created 200 if already exists
	 */
	@Override
	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<CustomerVO> create(@Valid @RequestBody CustomerRequest request) {

		log.debug("Attempt to create new customer:{}", request.companyName());
		ServiceResult<CustomerVO> persistedCustomer = customerService.create(request);
		HttpStatus httpStatus = persistedCustomer.wasCreated() ? HttpStatus.CREATED : HttpStatus.OK;
		if (persistedCustomer.wasCreated()) {
			log.debug("New Customer created successfully:{}", persistedCustomer.entity().companyName());
		} else {
			log.debug("Customer already exists:{}", persistedCustomer.entity().companyName());
		}

		return ResponseEntity.status(httpStatus).body(persistedCustomer.entity());

	}


	/**
	 * Put method is full record replacement
	 *
	 * This method also demonstrates a minimal security check.
	 * The URL path variable id is compared to the request payload id.  While not meant to be exhaustive it does
	 * show a pattern that can be followed for non-database level edits on input data.
	 * <p>
	 * Note also the use of the log4j feature MARKER (SECURITY) that can greatly assist in log monitoring applications.
	 *
	 * @param id PK of customer to replace (From URL)
	 * @param customerRequest Full replacement for all attributes
	 * @return updated CustomerVO
	 */
	@Override
	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{id}")
			public ResponseEntity<CustomerVO> update(
			@PathVariable String id,
			@Valid @RequestBody CustomerRequest customerRequest) {

		log.debug("Attempt to replace existing customer:{}", id);
		if (!id.equals(customerRequest.customerId())) {
			log.error(SECURITY, "URL Customer ID:{} does not match PUT Payload:{}",
					id, customerRequest.customerId());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		CustomerVO updated = customerService.update(id, customerRequest);
		log.debug("Customer:{} updated successfully", updated.customerId());

		return ResponseEntity.ok(updated);
	}

	/**
	 * Delete a customer by PK
	 * @param id PK to delete
	 * @return Http Resp with no body is proper return
	 */
	@Override
	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {

		log.debug("Attempt to delete existing customer:{}", id);
		customerService.deleteById(id);
		log.debug("Customer:{} deleted successfully", id);
		return ResponseEntity.noContent().build();
	}

}
