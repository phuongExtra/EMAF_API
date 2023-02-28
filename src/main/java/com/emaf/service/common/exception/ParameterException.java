package com.emaf.service.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ParameterException
 *
 * @author khal
 * @since 2021/01/11
 */
@Getter
@Setter
@NoArgsConstructor
public class ParameterException extends BaseException {

    public ParameterException(final String messageCode) {
        super(messageCode, messageCode, null);
    }

    public ParameterException(final String messageCode, final Object[] args) {
        super(messageCode, messageCode, args);
    }

    public ParameterException(final String errorCode, final String messageCode) {
        super(errorCode, messageCode, null);
    }

}
