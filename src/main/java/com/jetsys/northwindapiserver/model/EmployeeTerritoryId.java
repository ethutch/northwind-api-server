package com.jetsys.northwindapiserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class EmployeeTerritoryId implements Serializable {
	private static final long serialVersionUID = 1019365697376100215L;
	@Column(name = "employee_id", nullable = false)
	private Short employeeId;

	@Column(name = "territory_id", nullable = false, length = 20)
	private String territoryId;
}