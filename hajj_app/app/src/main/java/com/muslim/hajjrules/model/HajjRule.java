package com.muslim.hajjrules.model;

public class HajjRule {
    private String title;
    private String description;
    private int categoryId;
    private int id;
    private int imageResourceId;

    public HajjRule(String title, String description, int categoryId, int imageResourceId, int id) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.imageResourceId = imageResourceId;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getId() {
        return id;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}

