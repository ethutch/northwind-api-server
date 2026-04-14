package com.jetsys.northwindapiserver.controller;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Request scoped Value Object to hold the Correlation Id of the request
 * This will be sourced from well known headers or if none are present a default will
 * be assigned in the HandlerInterceptor
 */
@Component
@Getter
@Setter
@RequestScope
public class CorrelationContext {

	private String correlationId;

}
