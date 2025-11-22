package com.muslim.hajjrules.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "hajj_rules")
public class HajjRule {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String category;
    private int imageResourceId;
    private boolean isFavorite;

    public HajjRule(String title, String description, String category, int imageResourceId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.imageResourceId = imageResourceId;
        this.isFavorite = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}

