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
@Table(name = "customer_demographics")
public class CustomerDemographic {
	@Id
	@Column(name = "customer_type_id", nullable = false, length = 5)
	private String customerTypeId;

	@Column(name = "customer_desc", length = Integer.MAX_VALUE)
	private String customerDesc;

}