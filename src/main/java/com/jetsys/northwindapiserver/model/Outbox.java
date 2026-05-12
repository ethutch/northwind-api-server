package com.jetsys.northwindapiserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.Instant;

@Entity
@Table(name = "outbox")
@Getter
@Setter
@NoArgsConstructor
public class Outbox {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "aggregate_type", nullable = false, length = 50)
	private String aggregateType;

	@Column(name = "aggregate_id", nullable = false, length = 50)
	private String aggregateId;

	@Column(name = "topic", nullable = false, length = 100)
	private String topic;

	@Column(name = "partition_key", nullable = false, length = 50)
	private String partitionKey;

	@Column(name = "global_id", nullable = false)
	private Long globalId;

	@Column(name = "payload", nullable = false)
	private byte[] payload;

	@Column(name = "status", nullable = false, length = 20)
	private String status;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	@Column(name = "error_message", length = 500)
	private String errorMessage;

	@Column(name = "retry_count", nullable = false)
	private Short retryCount;

	@Column(name = "next_retry_at")
	private Instant nextRetryAt;

	@Column(name = "published_at")
	private Instant publishedAt;
}