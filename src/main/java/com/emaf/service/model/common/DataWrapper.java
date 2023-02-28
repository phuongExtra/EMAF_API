package com.emaf.service.model.common;

import lombok.*;

/**
 * DataWrapper
 *
 * @author khal
 * @since 2022/01/11
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataWrapper {

    private Object data;
    private String status;
    private String message;

}
