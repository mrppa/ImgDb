package com.mrppa.imgdb.exception;

public class ImageMetaException extends ImageDbException {

	private static final long serialVersionUID = 1L;

	public ImageMetaException(String errorMessage) {
		super(errorMessage);
	}

	public ImageMetaException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}

}
