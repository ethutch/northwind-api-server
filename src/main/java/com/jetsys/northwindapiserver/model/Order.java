package com.jetsys.northwindapiserver.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_order_id_seq")
	@SequenceGenerator(name = "orders_order_id_seq", sequenceName = "orders_order_id_seq", allocationSize = 1)
	@Column(name = "order_id", nullable = false)
	private Integer id;

	@Column(name = "customer_id", nullable = false)
	private String customerId;

	@Column(name = "employee_id", nullable = false)
	private Short employeeId;

	@Column(name = "order_date", nullable = false)
	private LocalDate orderDate;

	@Column(name = "required_date")
	private LocalDate requiredDate;

	@Column(name = "shipped_date")
	private LocalDate shippedDate;

	@Column(name = "freight")
	private Float freight;

	@Column(name = "ship_name", length = 40)
	private String shipName;

	@Column(name = "ship_address", length = 60)
	private String shipAddress;

	@Column(name = "ship_city", length = 15)
	private String shipCity;

	@Column(name = "ship_region", length = 15)
	private String shipRegion;

	@Column(name = "ship_postal_code", length = 10)
	private String shipPostalCode;

	@Column(name = "ship_country", length = 15)
	private String shipCountry;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", insertable = false, updatable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", insertable = false, updatable = false)
	private Employee employee;

	// EAGER because you explicitly requested it earlier
	@OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<OrderDetail> orderDetails = new LinkedHashSet<>();
}
