package com.jetsys.northwindapiserver.mapper.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jetsys.northwindapiserver.model.Customer;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomerVO (
	String customerId,
	String companyName,
	String contactName,
	String contactTitle,
	String address,
	String city,
	String region,
	String postalCode,
	String country,
	String phone,
	String fax,
	Instant createdAt,
	String   createdBy,
	Instant  updatedAt,
	String   updatedBy
)
{
	public static CustomerVO fromEntity(Customer entity) {
		return CustomerVO.builder()
				.customerId(entity.getCustomerId())
				.companyName(entity.getCompanyName())
				.contactName(entity.getContactName())
				.contactTitle(entity.getContactTitle())
				.address(entity.getAddress())
				.city(entity.getCity())
				.region(entity.getRegion())
				.postalCode(entity.getPostalCode())
				.country(entity.getCountry())
				.phone(entity.getPhone())
				.fax(entity.getFax())
				.createdAt(entity.getCreatedAt())
				.createdBy(entity.getCreatedBy())
				.updatedAt(entity.getUpdatedAt())
				.updatedBy(entity.getUpdatedBy())
				.build();
	}

	public static CustomerVO fromRequest(CustomerRequest request) {
		return CustomerVO.builder()
				.customerId(request.customerId())
				.companyName(request.companyName())
				.contactName(request.contactName())
				.contactTitle(request.contactTitle())
				.address(request.address())
				.city(request.city())
				.region(request.region())
				.postalCode(request.postalCode())
				.phone(request.phone())
				.fax(request.fax())
				.build();
	}
}

