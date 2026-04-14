package com.jetsys.northwindapiserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * This Critical class captures the INBOUND correlation ID based on well known headers depending on
 * environment.  This value then becomes part of EVERY log statement and will even be supplied to
 * published broker messages.  This is THE critical component for end-to-end tracing.
 */
@Component
public class CorrelationInterceptor implements HandlerInterceptor {

	private final CorrelationContext correlationContext;

	public CorrelationInterceptor(CorrelationContext correlationContext) {
		this.correlationContext = correlationContext;
	}

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) {

		String id = resolveCorrelationId(request);

		correlationContext.setCorrelationId(id);
		ThreadContext.put("correlationId", id);

		response.setHeader("X-Correlation-ID", id);

		return true;
	}

	@Override
	public void afterCompletion(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			Object handler,
			Exception ex) {

		ThreadContext.remove("correlationId");

	}

	private String resolveCorrelationId(HttpServletRequest request) {

		String id;

		id = request.getHeader("X-Correlation-ID");

		if (id == null)
			id = request.getHeader("X-Amzn-Trace-Id");

		if (id == null)
			id = request.getHeader("X-Amz-Cf-Id");

		if (id == null)
			id = request.getHeader("X-Request-ID");

		if (id == null)
			id = UUID.randomUUID().toString();

		return id;
	}
}