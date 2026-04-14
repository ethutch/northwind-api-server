package com.jetsys.northwindapiserver.util;

/**
 * Simple special purpose tuple to return from JPA save calls so controller can set proper HTTPRESP
 *
 * @param entity        The persisted entity
 * @param wasCreated    new / existing boolean
 * @param <T>           Entity Class
 */
public record ServiceResult<T>(T entity, boolean wasCreated) {
	public static <T> ServiceResult<T> created(T e) {
		return new ServiceResult<>(e, true);
	}

	public static <T> ServiceResult<T> alreadyExisted(T e){
		return new ServiceResult<>(e, false);
	}
}