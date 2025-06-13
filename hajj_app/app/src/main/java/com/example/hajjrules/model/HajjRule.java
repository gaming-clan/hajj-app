package com.example.hajjrules.model;

public class HajjRule {
    private String title;
    private String description;
    private int categoryId;
    private int id;

    public HajjRule(String title, String description, int categoryId, int id) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
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
}
