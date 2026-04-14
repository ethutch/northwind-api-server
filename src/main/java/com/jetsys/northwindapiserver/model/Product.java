package com.jetsys.northwindapiserver.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
	@Id
	@Column(name = "product_id", nullable = false)
	private Short id;

	@Column(name = "product_name", nullable = false, length = 40)
	private String productName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "quantity_per_unit", length = 20)
	private String quantityPerUnit;

	@Column(name = "unit_price")
	private Float unitPrice;

	@Column(name = "units_in_stock")
	private Short unitsInStock;

	@Column(name = "units_on_order")
	private Short unitsOnOrder;

	@Column(name = "reorder_level")
	private Short reorderLevel;

	@Column(name = "discontinued", nullable = false)
	private Integer discontinued;

}