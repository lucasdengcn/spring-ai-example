package com.example.demo.pdf;

public class ImageElement {
    private float x;
    private float y;
    private float width;
    private float height;
    private int pageNumber;
    private byte[] imageData;

    public ImageElement(float x, float y, float width, float height, int pageNumber, byte[] imageData) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pageNumber = pageNumber;
        this.imageData = imageData;
    }

    // Getters
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

    public byte[] getImageData() {
        return imageData;
    }
}