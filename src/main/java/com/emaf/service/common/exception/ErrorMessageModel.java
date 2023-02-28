package com.emaf.service.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ErrorMessageModel
 *
 * @author khal
 * @since 2020/06/13
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageModel {

    private String type;
    private String code;
    private String message;
    private String status;

}
