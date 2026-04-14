package com.jetsys.northwindapiserver.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee {
	@Id
	@Column(name = "employee_id", nullable = false)
	private Short id;

	@Column(name = "last_name", nullable = false, length = 20)
	private String lastName;

	@Column(name = "first_name", nullable = false, length = 10)
	private String firstName;

	@Column(name = "title", length = 30)
	private String title;

	@Column(name = "title_of_courtesy", length = 25)
	private String titleOfCourtesy;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Column(name = "hire_date")
	private LocalDate hireDate;

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

	@Column(name = "home_phone", length = 24)
	private String homePhone;

	@Column(name = "extension", length = 4)
	private String extension;

	@Column(name = "photo")
	private byte[] photo;

	@Column(name = "notes", length = Integer.MAX_VALUE)
	private String notes;

	@Column(name = "reports_to" )
	private Short reportsTo;

	@Column(name = "photo_path")
	private String photoPath;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reports_to", insertable = false, updatable = false)
	private Employee supervisor;

}