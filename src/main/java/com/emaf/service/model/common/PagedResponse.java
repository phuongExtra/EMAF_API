package com.emaf.service.model.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PagedResponse
 *
 * @author: VuongVT2
 * @since: 2022/03/28
 */
@Getter
@Setter
@NoArgsConstructor
public class PagedResponse<T> {
    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private long totalPages;

    public PagedResponse(List<T> data, int page, int size, long totalElements, long totalPages) {
        setData(data);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getData() {
        return data == null ? null : new ArrayList<>(data);
    }

    public final void setData(List<T> data) {
        if (data == null) {
            this.data = null;
        } else {
            this.data = Collections.unmodifiableList(data);
        }
    }
}
