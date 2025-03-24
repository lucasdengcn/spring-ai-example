package com.example.demo.pdf;

public class PageHeaderFooter {
    private String header;
    private String footer;
    private int pageNumber;

    public PageHeaderFooter(String header, String footer, int pageNumber) {
        this.header = header;
        this.footer = footer;
        this.pageNumber = pageNumber;
    }

    // Getters
    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}