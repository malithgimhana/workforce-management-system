package com.flexiwork.dto;

import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageResponse() {}

    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public static <T> Builder<T> builder() { return new Builder<>(); }

    public static class Builder<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public Builder<T> content(List<T> val) { this.content = val; return this; }
        public Builder<T> page(int val) { this.page = val; return this; }
        public Builder<T> size(int val) { this.size = val; return this; }
        public Builder<T> totalElements(long val) { this.totalElements = val; return this; }
        public Builder<T> totalPages(int val) { this.totalPages = val; return this; }

        public PageResponse<T> build() {
            PageResponse<T> obj = new PageResponse<>();
            obj.content = this.content;
            obj.page = this.page;
            obj.size = this.size;
            obj.totalElements = this.totalElements;
            obj.totalPages = this.totalPages;
            return obj;
        }
    }
}
