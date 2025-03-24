package com.example.demo.pdf;

import java.util.List;

public class TableElement {
    private List<List<String>> cells;
    private float x;
    private float y;
    private float width;
    private float height;
    private int pageNumber;

    public TableElement(List<List<String>> cells, float x, float y, float width, float height, int pageNumber) {
        this.cells = cells;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pageNumber = pageNumber;
    }

    // Getters
    public List<List<String>> getCells() {
        return cells;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}