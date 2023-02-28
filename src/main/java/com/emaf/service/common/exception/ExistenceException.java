package com.emaf.service.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ServerErrorException
 *
 * @author khal
 * @since 2021/01/11
 */
@Getter
@Setter
@NoArgsConstructor
public class ExistenceException extends BaseException {

    public ExistenceException(final String messageCode) {
        super(messageCode, messageCode, null);
    }

    public ExistenceException(final String messageCode, final Object[] args) {
        super(messageCode, messageCode, args);
    }

    public ExistenceException(final String errorCode, final String messageCode) {
        super(errorCode, messageCode, null);
    }

}
