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
@Table(name = "categories")
public class Category {
	@Id
	@Column(name = "category_id", nullable = false)
	private Short id;

	@Column(name = "category_name", nullable = false, length = 15)
	private String categoryName;

	@Column(name = "description", length = Integer.MAX_VALUE)
	private String description;

	@Column(name = "picture")
	private byte[] picture;

}