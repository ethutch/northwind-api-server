package com.jetsys.northwindapiserver.repository;

import com.jetsys.northwindapiserver.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Short> {
}
