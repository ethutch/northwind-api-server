package com.jetsys.northwindapiserver.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "territories")
public class Territory {
	@Id
	@Column(name = "territory_id", nullable = false, length = 20)
	private String territoryId;

	@Column(name = "territory_description", nullable = false, length = 60)
	private String territoryDescription;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "region_id", nullable = false)
	private Region region;

}