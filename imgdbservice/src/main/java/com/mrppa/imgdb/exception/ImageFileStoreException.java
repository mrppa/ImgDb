package com.mrppa.imgdb.exception;

import java.io.Serial;

public class ImageFileStoreException extends ImageDbException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ImageFileStoreException(String errorMessage) {
        super(errorMessage);
    }

    public ImageFileStoreException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
