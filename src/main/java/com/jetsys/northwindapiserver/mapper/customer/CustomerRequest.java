package com.jetsys.northwindapiserver.mapper.customer;

import com.jetsys.northwindapiserver.validation.ValidPhone;
import jakarta.validation.constraints.Pattern;


public record CustomerRequest (
		String customerId,
		String companyName,
		String contactName,
		String contactTitle,
		String address,
		String city,
		String region,
		@Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid ZIP Code")
		String postalCode,
		String country,
		@ValidPhone
		String phone,
		@ValidPhone
		String fax
)
{}


