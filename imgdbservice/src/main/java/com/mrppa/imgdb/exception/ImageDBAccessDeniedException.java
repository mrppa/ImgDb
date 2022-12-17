package com.mrppa.imgdb.exception;

import java.io.Serial;

public class ImageDBAccessDeniedException extends ImageDbException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ImageDBAccessDeniedException(String errorMessage) {
        super(errorMessage);
    }

    public ImageDBAccessDeniedException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
