package com.jetsys.northwindapiserver.repository;


import com.jetsys.northwindapiserver.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, Short> {
}
