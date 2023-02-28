package com.emaf.service.common.constant;

import com.emaf.service.model.ExcelColumnConfig;

import java.util.Arrays;
import java.util.List;

/**
 * ExcelConstant
 *
 * @author: VuongVT2
 * @since: 2022/12/17
 */
public class ExcelConstant {
    public enum EXCEL_CONFIG {
        SUMMARY(Arrays.asList(
                new ExcelColumnConfig("Student Code", "studentCode"),
                new ExcelColumnConfig("Fullname", "fullName"),
                new ExcelColumnConfig("Email", "email"),
                new ExcelColumnConfig("Status", "status")
        ));
        public final List<ExcelColumnConfig> value;

        EXCEL_CONFIG(List<ExcelColumnConfig> value) {
            this.value = value;
        }

        public static EXCEL_CONFIG getValueOf(List<ExcelColumnConfig> _value) {
            return Arrays.stream(EXCEL_CONFIG.values())
                    .filter(id -> id.value.equals(_value))
                    .findAny()
                    .orElse(null);
        }
    }
}
