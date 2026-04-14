package com.jetsys.northwindapiserver.mapper.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record OrderDetailRequest(
		@NotNull Short productId,
		@NotNull @PositiveOrZero Float unitPrice,
		@NotNull @Min(1) Short quantity,
		@NotNull @PositiveOrZero Float discount
) { }

