package com.jetsys.northwindapiserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "us_states")
public class UsState {
	@Id
	@Column(name = "state_id", nullable = false)
	private Short id;

	@Column(name = "state_name", length = 100)
	private String stateName;

	@Column(name = "state_abbr", length = 2)
	private String stateAbbr;

	@Column(name = "state_region", length = 50)
	private String stateRegion;

}