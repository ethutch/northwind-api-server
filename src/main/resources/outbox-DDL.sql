CREATE TABLE outbox (
                        id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        aggregate_type   VARCHAR(50)  NOT NULL,
                        aggregate_id     VARCHAR(50)  NOT NULL,
                        topic            VARCHAR(100) NOT NULL,
                        partition_key    VARCHAR(50)  NOT NULL,
                        global_id        INTEGER      NOT NULL,
                        payload          BYTEA        NOT NULL,
                        status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                        created_at       TIMESTAMPTZ  NOT NULL,
                        updated_at       TIMESTAMPTZ,
                        error_message    VARCHAR(500),
                        retry_count      SMALLINT     NOT NULL DEFAULT 0,
                        next_retry_at    TIMESTAMPTZ,
                        published_at     TIMESTAMPTZ
);

CREATE INDEX idx_outbox_pending   ON outbox (status, id)             WHERE status = 'PENDING';
CREATE INDEX idx_outbox_failed    ON outbox (status, next_retry_at)  WHERE status = 'FAILED';
CREATE INDEX idx_outbox_published ON outbox (published_at)           WHERE status = 'PUBLISHED';