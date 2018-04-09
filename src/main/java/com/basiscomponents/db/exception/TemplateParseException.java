package com.basiscomponents.db.exception;

public class TemplateParseException extends RuntimeException {
	public TemplateParseException(String message) {
		super(message);
	}

	public TemplateParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
