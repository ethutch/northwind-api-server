package com.jetsys.northwindapiserver.config;

import com.jetsys.northwindapiserver.controller.CorrelationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final CorrelationInterceptor interceptor;

	public WebConfig(CorrelationInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(interceptor)
				.addPathPatterns("/**");
	}
}
