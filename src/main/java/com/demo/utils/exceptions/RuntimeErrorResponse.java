package com.demo.utils.exceptions;

import java.time.LocalDateTime;

public record RuntimeErrorResponse (String path,
                                    String msg,
                                    int statusCode,
                                    LocalDateTime localDateTime) {
}
