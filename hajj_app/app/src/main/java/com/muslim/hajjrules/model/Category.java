package com.muslim.hajjrules.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/** Category row used by the native app's Room database and UI. */
@Entity(tableName = "categories")
public class Category {
    @PrimaryKey
    private int id;
    private String name;
    private String title;
    private String description;
    private String image;
    private String icon;
    private String color;
    private boolean featured;
    @ColumnInfo(name = "order")
    private int order;
    @Ignore
    private int iconResId;

    public Category() {}

    @Ignore
    public Category(int id, String title, String description, int iconResId) {
        this.id = id;
        this.name = title;
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.order = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
}
