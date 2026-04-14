package com.jetsys.northwindapiserver.mapper.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jetsys.northwindapiserver.model.OrderDetail;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderDetailVO(
		@NotNull Short productId,
		@NotNull @PositiveOrZero Float unitPrice,
		@NotNull @Min(1) Short quantity,
		@NotNull @PositiveOrZero Float discount,
		Instant  createdAt,
		String   createdBy,
		Instant  updatedAt,
		String   updatedBy
) {
	public static OrderDetailVO fromEntity(OrderDetail entity) {
		return OrderDetailVO.builder()
				.productId(entity.getId().getProductId())
				.unitPrice(entity.getUnitPrice())
				.quantity(entity.getQuantity())
				.discount(entity.getDiscount())
				.createdAt(entity.getCreatedAt())
				.createdBy(entity.getCreatedBy())
				.updatedAt(entity.getUpdatedAt())
				.updatedBy(entity.getUpdatedBy())
				.build();
	}

	public static OrderDetailVO fromRequest(OrderDetailRequest request) {
		return OrderDetailVO.builder()
				.productId(request.productId())
				.unitPrice(request.unitPrice())
				.quantity(request.quantity())
				.discount(request.discount())
				.build();
	}
}
