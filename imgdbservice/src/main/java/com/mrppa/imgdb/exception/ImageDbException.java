package com.mrppa.imgdb.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class ImageDbException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public ImageDbException(String errorMessage) {
        super(errorMessage);
    }

    public ImageDbException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
