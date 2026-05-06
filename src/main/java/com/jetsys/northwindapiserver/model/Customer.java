package com.jetsys.northwindapiserver.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.domain.Persistable;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "customers")
public class Customer extends AuditableEntity implements Persistable <String> {

	@Transient
	@Builder.Default
	private boolean isNew = true;

	@PostLoad
	@PostPersist
	void markNotNew() {
		this.isNew = false;
	}

	@Override
	public String getId() {
		return getCustomerId();
	}

	@Override
	public boolean isNew() {
		return isNew;
	}


	@Id
	@EqualsAndHashCode.Include
	@Column(name = "customer_id", nullable = false, length = 5)
	@Size(max = 5)
	private String customerId;

	@Column(name = "company_name", nullable = false, length = 40)
	@Size(max = 40)
	private String companyName;

	@Column(name = "contact_name", length = 30)
	@Size(max = 30)
	private String contactName;

	@Column(name = "contact_title", length = 30)
	@Size(max = 30)
	private String contactTitle;

	@Column(name = "address", length = 60)
	@Size(max = 60)
	private String address;

	@Column(name = "city", length = 15)
	@Size(max = 15)
	private String city;

	@Column(name = "region", length = 15)
	@Size(max = 15)
	private String region;

	@Column(name = "postal_code", length = 10)
	@Size(max = 10)
	private String postalCode;

	@Column(name = "country", length = 15)
	@Size(max = 15)
	private String country;

	@Column(name = "phone", length = 24)
	@Size(max = 24)
	private String phone;

	@Column(name = "fax", length = 24)
	@Size(max = 24)
	private String fax;

}
