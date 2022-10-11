package com.mrppa.imgdb.exception;

public class ImageFileStoreException extends ImageDbException {

	private static final long serialVersionUID = 1L;

	public ImageFileStoreException(String errorMessage) {
		super(errorMessage);
	}

	public ImageFileStoreException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}

}
