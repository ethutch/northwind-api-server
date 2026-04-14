package com.jetsys.northwindapiserver.service;

import com.jetsys.northwindapiserver.mapper.customer.CustomerVO;
import com.jetsys.northwindapiserver.config.CustomerEventPublisher;
import com.jetsys.northwindapiserver.controller.CorrelationContext;
import com.jetsys.northwindapiserver.customer.CustomerEventMessage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @deprecated
 * This shows direct publishing to Kafka.  It remains as an example of publishing.
 * But outbox is the proper design pattern.
 */
@Deprecated(since = "2.0")
@Service
@AllArgsConstructor
public class CustomerMessageProducerImpl implements CustomerMessageProducer {

	private final CustomerEventPublisher customerRequestEventPublisher;
	private final CorrelationContext correlationContext;

	@Override
	public void sendCustomerEvent(CustomerVO customerVO, String action) {
		CustomerEventMessage customerEventMessage = CustomerEventMessage.newBuilder()
				.setCorrelationId(correlationContext.getCorrelationId())
				.setCustomerId(customerVO.customerId())
				.setAction(action)
				.setCompanyName(customerVO.companyName())
				.setContactName(customerVO.contactName())
				.setContactTitle(customerVO.contactTitle())
				.setAddress(customerVO.address())
				.setCity(customerVO.city())
				.setRegion(customerVO.region())
				.setPostalCode(customerVO.postalCode())
				.setCountry(customerVO.country())
				.setPhone(customerVO.phone())
				.setFax(customerVO.fax())
				.build();

		customerRequestEventPublisher.publish(customerEventMessage);
	}
}