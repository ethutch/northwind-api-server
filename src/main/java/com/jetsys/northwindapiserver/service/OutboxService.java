package com.jetsys.northwindapiserver.service;

import com.jetsys.northwindapiserver.model.Outbox;
import com.jetsys.northwindapiserver.model.OutboxAction;
import com.jetsys.northwindapiserver.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
public class OutboxService {

	private final OutboxRepository outboxRepository;

	public <T> void publish(T entity, OutboxAction action, BiFunction<T, OutboxAction, Outbox> mapper) {
		Outbox outbox = mapper.apply(entity, action);
		outbox.setStatus("PENDING");
		outbox.setCreatedAt(Instant.now());
		outbox.setRetryCount((short) 0);
		outboxRepository.save(outbox);
	}
}