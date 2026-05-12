package com.jetsys.northwindapiserver.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * This allows us to prevent the callout to Apicurio when we are doing JUnits
 */
@RequiredArgsConstructor
@Component
@Profile("!openapi-gen")
public class ApicurioInitializer  implements ApplicationRunner {

	private final ApicurioSchemaRegistry apicurioSchemaRegistry;


	@Override
	public void run(ApplicationArguments args) {
		apicurioSchemaRegistry.init();
	}
}
