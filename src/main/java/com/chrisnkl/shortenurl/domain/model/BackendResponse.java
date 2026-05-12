package com.chrisnkl.shortenurl.domain.model;

public record BackendResponse<T>(
        int status,
        String message,
        T data
) {}
