package com.emaf.service.model.event;

import lombok.*;

import java.io.Serializable;

/**
 * DocumentData
 *
 * @author: VuongVT2
 * @since: 2022/11/07
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentData implements Serializable {
    private Long id;
    private String name;
    private String targetUrl;
}