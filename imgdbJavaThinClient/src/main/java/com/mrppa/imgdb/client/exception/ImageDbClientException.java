package com.mrppa.imgdb.client.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class ImageDbClientException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public ImageDbClientException(String errorMessage) {
        super(errorMessage);
    }

    public ImageDbClientException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
