package com.jetsys.northwindapiserver.service;

import com.jetsys.northwindapiserver.mapper.customer.CustomerVO;

@Deprecated(since = "2.0")
/**
 * @deprecated
 */
public interface CustomerMessageProducer {

	void sendCustomerEvent(CustomerVO customerVO, String action);
}
