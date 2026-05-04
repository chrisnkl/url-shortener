package com.chrisnkl.shortenurl.infrastructure.web.dto.create_url;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;

public record CreateUrlRequest(

        @NotBlank(message = "Original URL cannot be empty")
        @URL(message = "Malformed URL provided")
        @Pattern(regexp = "^(https?|ftp)://.*$", message = "Only HTTP, HTTPS, and FTP protocols are allowed")
        String originalUrl,

        Long ttlInSeconds

) {
}
