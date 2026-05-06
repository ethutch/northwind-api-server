package com.jetsys.northwindapiserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class OrderDetailId implements Serializable {

	@Column(name = "order_id", nullable = false)
	private Integer orderId;

	@Column(name = "product_id", nullable = false)
	private Short productId;

}
