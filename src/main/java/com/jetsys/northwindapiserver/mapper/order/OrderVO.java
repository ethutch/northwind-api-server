package com.jetsys.northwindapiserver.mapper.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jetsys.northwindapiserver.model.Order;
import com.jetsys.northwindapiserver.model.OrderDetail;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OrderVO(
		Integer orderId, // null for create; present for PUT
		@NotNull @Size(min = 1, max = 5) String customerId,
		Short employeeId,
		@NotNull LocalDate orderDate,
		LocalDate requiredDate,
		LocalDate shippedDate,
		Float freight,
		String shipName,
		String shipAddress,
		String shipCity,
		String shipRegion,
		String shipPostalCode,
		String shipCountry,
		Instant createdAt,
		String   createdBy,
		Instant  updatedAt,
		String   updatedBy,
		@NotNull @Valid Set<OrderDetailVO> orderDetails
) {
	public static OrderVO fromEntity(Order entity,boolean includeDetails) {
		OrderVO orderVO = OrderVO.builder()
				.orderId(entity.getId())
				.customerId(entity.getCustomerId())
				.employeeId(entity.getEmployeeId())
				.orderDate(entity.getOrderDate())
				.requiredDate(entity.getRequiredDate())
				.shippedDate(entity.getShippedDate())
				.freight(entity.getFreight())
				.shipName(entity.getShipName())
				.shipAddress(entity.getShipAddress())
				.shipCity(entity.getShipCity())
				.shipRegion(entity.getShipRegion())
				.shipPostalCode(entity.getShipPostalCode())
				.shipCountry(entity.getShipCountry())
				.createdAt(entity.getCreatedAt())
				.createdBy(entity.getCreatedBy())
				.updatedAt(entity.getUpdatedAt())
				.updatedBy(entity.getUpdatedBy())
				.orderDetails(HashSet.newHashSet(10))
				.build();

		if (includeDetails) {
		for(OrderDetail orderDetail : entity.getOrderDetails()) {
			var detail = OrderDetailVO.fromEntity(orderDetail);
			orderVO.orderDetails.add(detail);
		}
		}
		return orderVO;
	}

	public static OrderVO fromRequest(OrderRequest orderRequest) {
		OrderVO orderVO = OrderVO.builder()
				.orderId(orderRequest.orderId())
				.customerId(orderRequest.customerId())
				.employeeId(orderRequest.employeeId())
				.orderDate(orderRequest.orderDate())
				.requiredDate(orderRequest.requiredDate())
				.shippedDate(orderRequest.shippedDate())
				.freight(orderRequest.freight())
				.shipName(orderRequest.shipName())
				.shipAddress(orderRequest.shipAddress())
				.shipCity(orderRequest.shipCity())
				.shipRegion(orderRequest.shipRegion())
				.shipPostalCode(orderRequest.shipPostalCode())
				.shipCountry(orderRequest.shipCountry())
				.orderDetails(HashSet.newHashSet(10))
				.build();

		for(OrderDetailRequest orderDetailRequest : orderRequest.orderDetails()) {
			var detail = OrderDetailVO.fromRequest(orderDetailRequest);
			orderVO.orderDetails.add(detail);
		}
		return orderVO;
	}
}
