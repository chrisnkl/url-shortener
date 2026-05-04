package com.chrisnkl.shortenurl.domain.exception;

import lombok.Getter;

@Getter
public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException() {
        super();
    }

    public UrlNotFoundException(String message) {
        super(message);
    }

    protected UrlNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UrlNotFoundException(Throwable cause) {
        super(cause);
    }

    public UrlNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
