package com.ydo4ki.datalayouts;

public class UnpureClassException extends RuntimeException {
	public UnpureClassException(Class<?> clazz, String details) {
		super(clazz + " cannot be present as layout: " + details);
	}
	public UnpureClassException(Class<?> clazz) {
		super(clazz + " cannot be present as layout");
	}
}
