package com.jetsys.northwindapiserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "suppliers")
public class Supplier {
	@Id
	@Column(name = "supplier_id", nullable = false)
	private Short id;

	@Column(name = "company_name", nullable = false, length = 40)
	private String companyName;

	@Column(name = "contact_name", length = 30)
	private String contactName;

	@Column(name = "contact_title", length = 30)
	private String contactTitle;

	@Column(name = "address", length = 60)
	private String address;

	@Column(name = "city", length = 15)
	private String city;

	@Column(name = "region", length = 15)
	private String region;

	@Column(name = "postal_code", length = 10)
	private String postalCode;

	@Column(name = "country", length = 15)
	private String country;

	@Column(name = "phone", length = 24)
	private String phone;

	@Column(name = "fax", length = 24)
	private String fax;

	@Column(name = "homepage", length = Integer.MAX_VALUE)
	private String homepage;

}