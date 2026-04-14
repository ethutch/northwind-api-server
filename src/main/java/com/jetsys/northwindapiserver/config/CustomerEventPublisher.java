package com.jetsys.northwindapiserver.config;

import com.jetsys.northwindapiserver.customer.CustomerEventMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

/**
 * @deprecated
 * <p>
 * This class is deprecated as we are now using the outbox pattern.
 * It remains here to show how simple it is to publish to Kafka from
 * Spring Boot.
 */
@Log4j2
@Configuration
@Deprecated(since = "2.0")
public class CustomerEventPublisher {

	// NOTE: no Spring Message wrapper anymore
	private final Sinks.Many<CustomerEventMessage> sink =
			Sinks.many().multicast().onBackpressureBuffer();

	@Bean
	public Supplier<Flux<CustomerEventMessage>> customerRequest() {
		return sink::asFlux;
	}

	public void publish(CustomerEventMessage message) {
		sink.tryEmitNext(message);
	}
}