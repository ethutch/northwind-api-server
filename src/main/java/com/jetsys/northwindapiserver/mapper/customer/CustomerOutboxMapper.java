package com.jetsys.northwindapiserver.mapper.customer;

import com.jetsys.northwindapiserver.customer.CustomerEventMessage;
import com.jetsys.northwindapiserver.model.Customer;
import com.jetsys.northwindapiserver.model.Outbox;
import com.jetsys.northwindapiserver.model.OutboxAction;
import com.jetsys.northwindapiserver.util.ApicurioSchemaRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
public class CustomerOutboxMapper
		implements BiFunction<Customer, OutboxAction, Outbox> {

	@Value("${app.apicurio.customer-event-global-id}")
	private int globalId;

	private final ApicurioSchemaRegistry registry;

	public CustomerOutboxMapper(ApicurioSchemaRegistry registry) {
		this.registry = registry;
	}

	@Override
	public Outbox apply(Customer customer, OutboxAction action) {
		byte[] protoBytes = CustomerEventMessage.newBuilder()
				.setCorrelationId(UUID.randomUUID().toString())
				.setAction(action.name())
				.setCustomerId(customer.getCustomerId())
				.setCompanyName(nullToEmpty(customer.getCompanyName()))
				.setContactName(nullToEmpty(customer.getContactName()))
				.setContactTitle(nullToEmpty(customer.getContactTitle()))
				.setAddress(nullToEmpty(customer.getAddress()))
				.setCity(nullToEmpty(customer.getCity()))
				.setRegion(nullToEmpty(customer.getRegion()))
				.setPostalCode(nullToEmpty(customer.getPostalCode()))
				.setCountry(nullToEmpty(customer.getCountry()))
				.setPhone(nullToEmpty(customer.getPhone()))
				.setFax(nullToEmpty(customer.getFax()))
				.setCreatedBy(nullToEmpty(customer.getCreatedBy()))
				.setUpdatedBy(nullToEmpty(customer.getUpdatedBy()))
				.setCreatedAt(customer.getCreatedAt().toEpochMilli())
				.setUpdatedAt(customer.getUpdatedAt() != null
						? customer.getUpdatedAt().toEpochMilli() : 0L)
				.build()
				.toByteArray();

		Outbox outbox = new Outbox();
		outbox.setAggregateType("CUSTOMER");
		outbox.setAggregateId(customer.getCustomerId());
		outbox.setTopic("customer-events");
		outbox.setPartitionKey(customer.getCustomerId());
		outbox.setGlobalId(globalId);
		outbox.setPayload(registry.framePayload(globalId, protoBytes));
		return outbox;
	}

	private String nullToEmpty(String value) {
		return Objects.toString(value, "");
	}
}