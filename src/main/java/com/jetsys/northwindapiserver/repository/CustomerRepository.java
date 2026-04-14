package com.jetsys.northwindapiserver.repository;

import com.jetsys.northwindapiserver.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
}
