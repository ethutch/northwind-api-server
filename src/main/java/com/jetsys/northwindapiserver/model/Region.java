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
@Table(name = "region")
public class Region {
	@Id
	@Column(name = "region_id", nullable = false)
	private Short id;

	@Column(name = "region_description", nullable = false, length = 60)
	private String regionDescription;

}