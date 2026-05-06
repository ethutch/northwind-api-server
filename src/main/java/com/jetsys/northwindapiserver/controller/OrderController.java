package com.jetsys.northwindapiserver.controller;

import com.jetsys.northwindapiserver.mapper.order.OrderDetailRequest;
import com.jetsys.northwindapiserver.mapper.order.OrderDetailVO;
import com.jetsys.northwindapiserver.mapper.order.OrderRequest;
import com.jetsys.northwindapiserver.mapper.order.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
		name = "Order Endpoint",
		description = "These endpoints provide direct manipulation of Orders"
)
public interface OrderController {
	@GetMapping
	@Operation(summary = "Get a page of all Orders",
			description = "Return the next page of Orders.  This is likely useful only in the context of sorting by date.  Default to page 1 size 20"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Page of Order Value Objects"),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content())
	})
	ResponseEntity<Page<OrderVO>> getAllOrders(Pageable pageable,
			@RequestParam(name = "include_details", defaultValue = "false") boolean includeDetails);

	@GetMapping("/{id}")
	@Operation(summary = "Retrieve a single order by Id",
			description = "Return all attributes for the requested order"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order attributes with included detail lines"),
			@ApiResponse(responseCode = "404", description = "Order Not found", content = @Content()),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content())
	})
	ResponseEntity<OrderVO> getOrder(@PathVariable Integer id,
			@RequestParam(name = "include_details", defaultValue = "false") boolean includeDetails);

	@GetMapping("/customer/{customerId}")
	@Operation(summary = "Get a page of all Orders for a given customer",
			description = "Return the next page of Orders.  Default to page 1 size 20"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Page of Order Value Objects"),
			@ApiResponse(responseCode = "404", description = "Customer Not found", content = @Content()),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content())
	})
	ResponseEntity<Page<OrderVO>> getOrdersByCustomer(@PathVariable String customerId,
			Pageable pageable, @RequestParam(name = "include_details", defaultValue = "false") boolean includeDetails);

	@PostMapping
	@Operation(summary = "Create a new order",
			description = "Create a new order and return the persisted data."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order attributes with included detail lines"),
			@ApiResponse(responseCode = "404", description = "Customer Not found", content = @Content()),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content())
	})
	ResponseEntity<OrderVO> createOrder(@Valid @RequestBody OrderRequest orderRequest);

	@DeleteMapping("/{id}")
	ResponseEntity<Void> deleteOrder(@PathVariable Integer id);

	@Operation(summary = "Update an existing order",
			description = "This is a full replacement of the order and all its detail lines."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "New order attributes with included detail lines"),
			@ApiResponse(responseCode = "404", description = "Customer Not found", content = @Content()),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content())
	})
	@PutMapping("/{id}")
	ResponseEntity<OrderVO> updateOrder(@PathVariable Integer id,
			@Valid @RequestBody OrderRequest orderRequest);

	/**
	 *  Detail line endpoints below here.
 	 */

	@Operation(summary = "Add a new detail line to an existing order",
			description = """
					Add a new detail line without changing any attributes of the original order. \
					This includes even its Changed audit data.
					Change audit data will be reflected properly on the new detail line.
					"""
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "New order attributes with included detail lines"),
			@ApiResponse(responseCode = "404", description = "Customer Not found", content = @Content()),
			@ApiResponse(responseCode = "409", description = "Order Detail already exists", content = @Content()),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content())
	})
	@PostMapping("/{orderId}/details")
	ResponseEntity<OrderDetailVO> addDetail(@PathVariable Integer orderId,
			@Valid @RequestBody OrderDetailRequest orderDetailRequest);

	@Operation(summary = "Update an existing detail line to an existing order",
			description = """
					Change an existing detail line without changing any attributes of the original order. \
					This includes even its Changed audit data.
					Change audit data will be reflected properly on the new detail line.
					"""
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "New order attributes with included detail lines"),
			@ApiResponse(responseCode = "404", description = "Customer Not found", content = @Content()),
			@ApiResponse(responseCode = "404", description = "Order Detail Not found", content = @Content()),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content())
	})
	@PutMapping("/{orderId}/details/{productId}")
	ResponseEntity<OrderDetailVO> updateDetail(@PathVariable Integer orderId,
			@PathVariable Short productId,
			@Valid @RequestBody OrderDetailRequest orderDetailRequest);

	@Operation(summary = "Delete an existing detail line to an existing order",
			description = """
					Remove a detail line from an order without changing any attributes of the original order. \
					This includes even its Changed audit data.
					"""
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Detail line deleted"),
			@ApiResponse(responseCode = "404", description = "Order Detail Not found", content = @Content()),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content())
	})
	@DeleteMapping("/{orderId}/details/{productId}")
	ResponseEntity<Void> deleteDetail(@PathVariable Integer orderId,
			@PathVariable Short productId);
}
