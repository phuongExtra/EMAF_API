package com.emaf.service.model;

public class ExcelColumnConfig {
    private String header;
    private String value;

    public ExcelColumnConfig() {
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ExcelColumnConfig(String header, String value) {
        this.header = header;
        this.value = value;
    }
}
