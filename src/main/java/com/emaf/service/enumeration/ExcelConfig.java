package com.emaf.service.enumeration;

import com.emaf.service.model.ExcelColumnConfig;

import java.util.Arrays;
import java.util.List;

/**
 * ExcelConfig
 *
 * @author: VuongVT2
 * @since: 2022/12/17
 */
public enum ExcelConfig {
    SUMMARY(Arrays.asList(
            new ExcelColumnConfig("Student Code", "studentCode"),
            new ExcelColumnConfig("Fullname", "fullName"),
            new ExcelColumnConfig("Email", "email"),
            new ExcelColumnConfig("Status", "status")
    ));
    public final List<ExcelColumnConfig> value;

    ExcelConfig(List<ExcelColumnConfig> value) {
        this.value = value;
    }

    public static ExcelConfig getValueOf(List<ExcelColumnConfig> _value) {
        return Arrays.stream(ExcelConfig.values())
                .filter(id -> id.value.equals(_value))
                .findAny()
                .orElse(null);
    }

}
