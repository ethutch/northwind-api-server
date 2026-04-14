package com.jetsys.northwindapiserver.repository;

import com.jetsys.northwindapiserver.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Short> {
}
