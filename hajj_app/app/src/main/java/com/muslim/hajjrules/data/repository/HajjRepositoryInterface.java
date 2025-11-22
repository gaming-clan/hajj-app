package com.muslim.hajjrules.data.repository;

import androidx.lifecycle.LiveData;
import com.muslim.hajjrules.model.HajjRule;
import com.muslim.hajjrules.model.Category;

import java.util.List;

/**
 * Interface for Hajj Repository following the Repository pattern
 * This defines the contract for Hajj data operations
 */
public interface HajjRepositoryInterface {

    // Rule operations
    LiveData<List<HajjRule>> getAllRules();
    LiveData<List<HajjRule>> getRulesByCategory(String category);
    LiveData<List<HajjRule>> getFavoriteRules();
    LiveData<List<HajjRule>> searchRules(String query);
    LiveData<HajjRule> getRuleById(int id);

    // Category operations
    LiveData<List<Category>> getAllCategories();
    LiveData<Category> getCategoryById(int id);
    LiveData<List<Category>> getFeaturedCategories();

    // CRUD operations for rules
    void insert(HajjRule rule);
    void update(HajjRule rule);
    void delete(HajjRule rule);
    void deleteAllRules();

    // CRUD operations for categories
    void insert(Category category);
    void update(Category category);
    void delete(Category category);

    // Bulk operations
    void insertAllRules(List<HajjRule> rules);
    void insertAllCategories(List<Category> categories);

    // Search and filtering
    LiveData<List<HajjRule>> searchRulesByTitle(String query);
    LiveData<List<HajjRule>> searchRulesByContent(String query);
    LiveData<List<HajjRule>> getRulesWithEvidence();

    // Favorites management
    void toggleFavorite(int ruleId);
    LiveData<Integer> getFavoriteCount();

    // Cache and sync operations
    void refreshData();
    LiveData<Boolean> isDataFresh();
    void clearCache();

    // Statistics
    LiveData<Integer> getTotalRulesCount();
    LiveData<Integer> getTotalCategoriesCount();
    LiveData<Integer> getRulesWithEvidenceCount();
}