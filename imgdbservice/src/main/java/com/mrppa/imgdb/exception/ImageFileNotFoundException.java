package com.mrppa.imgdb.exception;

import java.io.Serial;

public class ImageFileNotFoundException extends ImageDbException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ImageFileNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public ImageFileNotFoundException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
