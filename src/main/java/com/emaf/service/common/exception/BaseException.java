package com.emaf.service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BaseException
 *
 * @author khale
 * @since 2021/01/11
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseException extends RuntimeException {

    private String status;
    private String message;
    private Object[] args;

    public BaseException(final String messageCode) {
        this(messageCode, messageCode, null);
    }

    public BaseException(final String messageCode, final Object[] args) {
        this(messageCode, messageCode, args);
    }

    public BaseException(final String errorCode, final String messageCode) {
        this(errorCode, messageCode, null);
    }

}
