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
@Table(name = "shippers")
public class Shipper {
	@Id
	@Column(name = "shipper_id", nullable = false)
	private Short id;

	@Column(name = "company_name", nullable = false, length = 40)
	private String companyName;

	@Column(name = "phone", length = 24)
	private String phone;

}