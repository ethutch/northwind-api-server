package com.jetsys.northwindapiserver.controller;

import com.jetsys.northwindapiserver.mapper.customer.CustomerRequest;
import com.jetsys.northwindapiserver.mapper.customer.CustomerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
		name = "Customer Endpoint",
		description = "These endpoints provide direct manipulation of the Customer"
)
public interface CustomerController {

	@GetMapping
	@Operation(summary = "Get a page of Customers",
			description = "Return the next page of Customers.  Default to page 1 size 20"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Page of Customer Value Objects"),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content())
	})
	Page<CustomerVO> getAll(
			@PageableDefault(page = 0, size = 20) Pageable pageable);

	@GetMapping("/{id}")
	@Operation(summary = "Retrieve a single customer by Id",
			description = "Return all attributes for the requested customer"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Customer attributes"),
			@ApiResponse(responseCode = "404", description = "Customer Not found", content = @Content()),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content())
	})
	ResponseEntity<CustomerVO> getCustomer(@PathVariable String id);

	@PostMapping
	@Operation(summary = "Create a new customer",
			description = "Create a brand new customer"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Existing Customer - NOT Updated"),
			@ApiResponse(responseCode = "201", description = "New Customer attributes"),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content())
	})
	ResponseEntity<CustomerVO> create(@Valid @RequestBody CustomerRequest request);

	@PutMapping("/{id}")
	ResponseEntity<CustomerVO> update(
			@PathVariable String id,
			@Valid @RequestBody CustomerRequest request);

	@DeleteMapping("/{id}")
	ResponseEntity<Void> delete(@PathVariable String id);

}
