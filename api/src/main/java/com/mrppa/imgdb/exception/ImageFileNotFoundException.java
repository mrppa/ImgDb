package com.mrppa.imgdb.exception;

public class ImageFileNotFoundException extends ImageDbException {

	private static final long serialVersionUID = 1L;

	public ImageFileNotFoundException(String errorMessage) {
		super(errorMessage);
	}

	public ImageFileNotFoundException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}

}
