package com.jetsys.northwindapiserver.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ValidPhoneValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ValidPhone {

	String message() default "{com.jetsys.northwindapiserver.validation.ValidPhone.message}";

	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

	/**
	 * Optional: default region when no country code is provided.
	 * Examples: "US", "GB", "FR". Empty string = use library default (usually lenient).
	 */
	String defaultRegion() default "US";
}