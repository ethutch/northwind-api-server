package com.jetsys.northwindapiserver.util;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@Setter
@ConfigurationProperties(prefix = "app.apicurio")
public class ApicurioSchemaRegistry {

	private String url;
	private String groupId;
	private List<String> artifacts;
	private final Map<String, Long> globalIds = new HashMap<>();

	/**
	 * This makes the call and cannot run during spring context initialization
	 * Instead we will use the new ApplicationRunner to call it after context load time but before execution begins
	 */
	public void init() {
		RestClient restClient = RestClient.builder()
				.baseUrl(url)
				.build();
		for (String artifactId : artifacts) {
			log.info("Loading artifact:{} ", artifactId);
			ArtifactMetaData meta = restClient.get()
					.uri("/apis/registry/v2/groups/{groupId}/artifacts/{artifactId}/meta",
							groupId, artifactId)
					.retrieve()
					.body(ArtifactMetaData.class);
			if (meta == null || meta.globalId() == null) {
				throw new IllegalStateException(
						"Failed to retrieve globalId for artifact: " + artifactId);
			}
			globalIds.put(artifactId, meta.globalId());
		}
	}

	public byte[] framePayload(String artifactId, byte[] protoBytes) {
		Long globalId = globalIds.get(artifactId);
		if (globalId == null) {
			throw new IllegalStateException("No globalId found for artifact: " + artifactId);
		}
		ByteBuffer buffer = ByteBuffer.allocate(9 + protoBytes.length);
		buffer.put((byte) 0x0);
		buffer.putLong(globalId);
		buffer.put(protoBytes);
		return buffer.array();
	}

	public long getGlobalId(String artifactId) {
		Long globalId = globalIds.get(artifactId);
		if (globalId == null) {
			throw new IllegalStateException("No globalId found for artifact: " + artifactId);
		}
		return globalId;
	}

	/**
	 * RestClient will parse the returned metadata against this record and only populate the 1 attribute we care about.
	 * @param globalId
	 */
	public record ArtifactMetaData(Long globalId) {}
}