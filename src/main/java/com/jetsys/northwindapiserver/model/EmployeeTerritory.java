package com.jetsys.northwindapiserver.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employee_territories")
public class EmployeeTerritory {
	@EmbeddedId
	private EmployeeTerritoryId id;

	//TODO [Reverse Engineering] generate columns from DB
}