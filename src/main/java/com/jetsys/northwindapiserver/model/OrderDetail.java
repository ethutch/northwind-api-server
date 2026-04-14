package com.jetsys.northwindapiserver.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "order_details")
public class OrderDetail extends AuditableEntity {

	@EmbeddedId
	private OrderDetailId id;

	@MapsId("orderId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@MapsId("productId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "unit_price", nullable = false)
	private Float unitPrice;

	@Column(name = "quantity", nullable = false)
	private Short quantity;

	@Column(name = "discount", nullable = false)
	private Float discount;
}
