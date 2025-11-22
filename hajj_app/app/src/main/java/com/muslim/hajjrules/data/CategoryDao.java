package com.muslim.hajjrules.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.muslim.hajjrules.model.Category;

import java.util.List;

/**
 * Data Access Object for Category entities
 * Provides database access methods for Category operations
 */
@Dao
public interface CategoryDao {

    /**
     * Insert a single category
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    /**
     * Insert multiple categories
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Category> categories);

    /**
     * Update a category
     */
    @Update
    void update(Category category);

    /**
     * Delete a category
     */
    @Delete
    void delete(Category category);

    /**
     * Delete all categories
     */
    @Query("DELETE FROM categories")
    void deleteAllCategories();

    /**
     * Get all categories ordered by order
     */
    @Query("SELECT * FROM categories ORDER BY `order` ASC")
    LiveData<List<Category>> getAllCategories();

    /**
     * Get category by ID
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(int id);

    /**
     * Get category by name
     */
    @Query("SELECT * FROM categories WHERE name = :name")
    LiveData<Category> getCategoryByName(String name);

    /**
     * Get featured categories
     */
    @Query("SELECT * FROM categories WHERE featured = 1 ORDER BY `order` ASC")
    LiveData<List<Category>> getFeaturedCategories();

    /**
     * Get categories count (synchronous)
     */
    @Query("SELECT COUNT(*) FROM categories")
    int getCategoriesCountSync();

    /**
     * Get total count of categories
     */
    @Query("SELECT COUNT(*) FROM categories")
    LiveData<Integer> getCategoriesCount();

    /**
     * Update category order
     */
    @Query("UPDATE categories SET `order` = :order WHERE id = :id")
    void updateCategoryOrder(int id, int order);

    /**
     * Toggle featured status
     */
    @Query("UPDATE categories SET featured = CASE WHEN featured = 1 THEN 0 ELSE 1 END WHERE id = :id")
    void toggleFeatured(int id);

    /**
     * Search categories by title or description
     */
    @Query("SELECT * FROM categories WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY `order` ASC")
    LiveData<List<Category>> searchCategories(String query);

    /**
     * Get categories with specific color
     */
    @Query("SELECT * FROM categories WHERE color = :color ORDER BY `order` ASC")
    LiveData<List<Category>> getCategoriesByColor(String color);

    /**
     * Update category image
     */
    @Query("UPDATE categories SET image = :image WHERE id = :id")
    void updateCategoryImage(int id, String image);

    /**
     * Update category icon
     */
    @Query("UPDATE categories SET icon = :icon WHERE id = :id")
    void updateCategoryIcon(int id, String icon);

    /**
     * Get max order value
     */
    @Query("SELECT MAX(`order`) FROM categories")
    int getMaxOrder();

    /**
     * Get categories by order range (for pagination)
     */
    @Query("SELECT * FROM categories ORDER BY `order` ASC LIMIT :limit OFFSET :offset")
    LiveData<List<Category>> getCategoriesByRange(int limit, int offset);
}