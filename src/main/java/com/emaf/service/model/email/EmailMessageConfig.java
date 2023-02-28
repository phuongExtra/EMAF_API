package com.emaf.service.model.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * EmailMessageConfig
 *
 * @author: VuongVT2
 * @since: 2022/05/13
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageConfig {
    private String to;
    private String subject;
    private String template;
    private Map<String, Object> templateModel;
    private List<String> cc;
    private String[] toList;
}
