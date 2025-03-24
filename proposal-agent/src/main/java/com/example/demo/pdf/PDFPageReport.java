package com.example.demo.pdf;

import java.util.List;

public class PDFPageReport {
    private List<TextElement> textElements;
    private List<ImageElement> imageElements;
    private List<TableElement> tableElements;
    private String header;
    private String footer;

    // Getters and Setters
    public List<TextElement> getTextElements() {
        return textElements;
    }

    public void setTextElements(List<TextElement> textElements) {
        this.textElements = textElements;
    }

    public List<ImageElement> getImageElements() {
        return imageElements;
    }

    public void setImageElements(List<ImageElement> imageElements) {
        this.imageElements = imageElements;
    }

    public List<TableElement> getTableElements() {
        return tableElements;
    }

    public void setTableElements(List<TableElement> tableElements) {
        this.tableElements = tableElements;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }
}