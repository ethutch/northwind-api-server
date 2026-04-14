package com.jetsys.northwindapiserver.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;

public class ValidPhoneValidator implements ConstraintValidator<ValidPhone, String> {  // ← Generic: Annotation type, Field type

	// Singleton instance — libphonenumber is thread-safe, so reuse it
	private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

	// We'll store the annotation's defaultRegion here
	private String defaultRegion;

	// Called once when the validator is initialized (e.g., app startup or per-bean)
	@Override
	public void initialize(ValidPhone constraintAnnotation) {
		this.defaultRegion = constraintAnnotation.defaultRegion();  // ← Grab the per-annotation config
	}

	// Called *every time* validation runs (e.g., on each @Valid customer)
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// Null/blank? Let other annotations (@NotBlank) handle it — don't fail here
		if (value == null || value.isBlank()) {
			return true;  // ← Early return: valid (or ignorable)
		}

		try {
			// Parse the raw string into a PhoneNumber object
			// Uses defaultRegion if no +country code (e.g., "555-1234" → assumes US)
			PhoneNumber number = PHONE_UTIL.parse(value, defaultRegion.isEmpty() ? null : defaultRegion);

			// Check if it's a real, valid number (uses metadata for 200+ countries)
			return PHONE_UTIL.isValidNumber(number);  // ← True if valid (e.g., +1-555-123-4567), false otherwise
		} catch (NumberParseException numberParseException) {
			// Parsing failed (e.g., "abc-def" or malformed). Customize the error:
			context.disableDefaultConstraintViolation();  // ← Skip the boring default msg
			context.buildConstraintViolationWithTemplate(  // ← Build a custom one
					"Invalid phone number format: '" + value + "'. Use international format or valid local format."
			).addConstraintViolation();  // ← Attach it to the field
			return false;  // ← Fail validation
		}
	}
}