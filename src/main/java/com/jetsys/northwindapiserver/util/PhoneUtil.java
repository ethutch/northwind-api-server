package com.jetsys.northwindapiserver.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public final class PhoneUtil {
	private static final PhoneNumberUtil UTIL = PhoneNumberUtil.getInstance();

	private PhoneUtil() {}  // Utility class: no instances

	public static String normalise(String raw, String defaultRegion) {
		if (raw == null || raw.isBlank()) return null;
		try {
			var number = UTIL.parse(raw, defaultRegion);  // Same parse as validator
			if (!UTIL.isValidNumber(number)) return null;  // Same validity check
			return UTIL.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);  // ← Extra: normalize to standard format
		} catch (NumberParseException numberParseException) {
			return null;
		}
	}

	public static boolean isValid(String raw, String defaultRegion) {  // ← Mirror for simple checks
		return normalise(raw, defaultRegion) != null;
	}
}