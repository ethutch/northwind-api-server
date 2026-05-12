package com.jetsys.northwindapiserver.mapper.order;

import com.jetsys.northwindapiserver.model.Order;
import com.jetsys.northwindapiserver.model.OrderDetail;
import com.jetsys.northwindapiserver.model.Outbox;
import com.jetsys.northwindapiserver.model.OutboxAction;
import com.jetsys.northwindapiserver.order.OrderDetailMessage;
import com.jetsys.northwindapiserver.order.OrderEventMessage;
import com.jetsys.northwindapiserver.util.ApicurioSchemaRegistry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
public class OrderOutboxMapper
		implements BiFunction<Order, OutboxAction, Outbox> {

	private final ApicurioSchemaRegistry registry;

	public OrderOutboxMapper(ApicurioSchemaRegistry registry) {
		this.registry = registry;
	}

	@Override
	public Outbox apply(Order order, OutboxAction action) {
		byte[] protoBytes = OrderEventMessage.newBuilder()
				.setCorrelationId(UUID.randomUUID().toString())
				.setAction(action.name())
				.setOrderId(order.getId())
				.setCustomerId(order.getCustomerId())
				.setEmployeeId(order.getEmployeeId())
				.setOrderDate(order.getOrderDate().toString())
				.setRequiredDate(order.getRequiredDate() != null
						? order.getRequiredDate().toString() : "")
				.setShippedDate(order.getShippedDate() != null
						? order.getShippedDate().toString() : "")
				.setFreight(order.getFreight() != null
						? order.getFreight() : 0f)
				.setShipName(nullToEmpty(order.getShipName()))
				.setShipAddress(nullToEmpty(order.getShipAddress()))
				.setShipCity(nullToEmpty(order.getShipCity()))
				.setShipRegion(nullToEmpty(order.getShipRegion()))
				.setShipPostalCode(nullToEmpty(order.getShipPostalCode()))
				.setShipCountry(nullToEmpty(order.getShipCountry()))
				.addAllOrderDetails(mapOrderDetails(order.getOrderDetails()))
				.setCreatedBy(nullToEmpty(order.getCreatedBy()))
				.setUpdatedBy(nullToEmpty(order.getUpdatedBy()))
				.setCreatedAt(order.getCreatedAt().toEpochMilli())
				.setUpdatedAt(order.getUpdatedAt() != null
						? order.getUpdatedAt().toEpochMilli() : 0L)
				.build()
				.toByteArray();

		Outbox outbox = new Outbox();
		outbox.setAggregateType("ORDER");
		outbox.setAggregateId(String.valueOf(order.getId()));
		outbox.setTopic("order-events");
		outbox.setPartitionKey(String.valueOf(order.getId()));
		outbox.setGlobalId(registry.getGlobalId("OrderEvent"));
		outbox.setPayload(registry.framePayload("OrderEvent", protoBytes));
		return outbox;
	}

	private List<OrderDetailMessage> mapOrderDetails(Set<OrderDetail> details) {
		return details.stream()
				.map(d -> OrderDetailMessage.newBuilder()
						.setProductId(d.getId().getProductId().intValue())
						.setUnitPrice(d.getUnitPrice())
						.setQuantity(d.getQuantity().intValue())
						.setDiscount(d.getDiscount())
						.build())
				.toList();
	}

	private String nullToEmpty(String value) {
		return Objects.toString(value, "");
	}
}