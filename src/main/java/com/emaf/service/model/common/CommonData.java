package com.emaf.service.model.common;

import lombok.*;

import java.io.Serializable;

/**
 * FieldData
 *
 * @author khal
 * @since 2022/01/11
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonData implements Serializable {

    private String id;
    private String name;

}
