package com.jetsys.northwindapiserver.mapper.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record OrderRequest(

		Integer orderId, // null for create; present for PUT
		@NotNull
		@Size(min = 1, max = 5) String customerId,
		Short employeeId,
		@NotNull
		LocalDate orderDate,
		LocalDate requiredDate,
		LocalDate shippedDate,
		Float freight,
		String shipName,
		String shipAddress,
		String shipCity,
		String shipRegion,
		String shipPostalCode,
		String shipCountry,
		@NotNull @Valid
		Set<OrderDetailRequest> orderDetails) {
}
