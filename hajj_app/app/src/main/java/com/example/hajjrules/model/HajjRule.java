package com.example.hajjrules.model;

import java.util.List;

public class HajjRule {
    private String title;
    private String description;
    private int categoryId;
    private int ruleId;

    public HajjRule(String title, String description, int categoryId, int ruleId) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.ruleId = ruleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }
}
