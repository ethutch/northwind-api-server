package com.jetsys.northwindapiserver.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.ByteBuffer;

@Component
public class ApicurioSchemaRegistry {

	private final RestClient restClient;

	public ApicurioSchemaRegistry(@Value("${app.apicurio.url}") String url) {
		this.restClient = RestClient.builder()
				.baseUrl(url)
				.build();
	}

	public byte[] framePayload(long globalId, byte[] protoBytes) {
		ByteBuffer buffer = ByteBuffer.allocate(5 + protoBytes.length);
		buffer.put((byte) 0x0);
		buffer.putInt((int) globalId);
		buffer.put(protoBytes);
		return buffer.array();
	}
}
