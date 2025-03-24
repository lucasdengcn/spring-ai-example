package com.example.demo.pdf;

public class TextElement {
    private String text;
    private float x;
    private float y;
    private float width;
    private float height;
    private int pageNumber;

    public TextElement(String text, float x, float y, float width, float height, int pageNumber) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pageNumber = pageNumber;
    }

    // Getters
    public String getText() {
        return text;
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

    @Override
    public String toString() {
        return String.format("[%fx%f]%s", this.x, this.y, this.text);
    }
}