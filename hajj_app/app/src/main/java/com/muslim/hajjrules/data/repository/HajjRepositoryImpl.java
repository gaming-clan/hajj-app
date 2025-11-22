package com.muslim.hajjrules.data.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.muslim.hajjrules.data.HajjDatabase;
import com.muslim.hajjrules.data.HajjRuleDao;
import com.muslim.hajjrules.data.CategoryDao;
import com.muslim.hajjrules.model.HajjRule;
import com.muslim.hajjrules.model.Category;
import com.muslim.hajjrules.util.JsonDataLoader;
import com.muslim.hajjrules.util.NetworkUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Implementation of HajjRepository with proper dependency injection
 * Follows clean architecture principles with separation of concerns
 */
@Singleton
public class HajjRepositoryImpl implements HajjRepositoryInterface {

    private final HajjDatabase database;
    private final HajjRuleDao ruleDao;
    private final CategoryDao categoryDao;
    private final Context context;
    private final ExecutorService executorService;
    private final NetworkUtils networkUtils;
    private final JsonDataLoader jsonDataLoader;

    // Cache for freshness tracking
    private final MutableLiveData<Boolean> isDataFresh = new MutableLiveData<>(false);
    private long lastRefreshTime = 0;
    private static final long CACHE_DURATION_MS = TimeUnit.HOURS.toMillis(1); // 1 hour cache

    @Inject
    public HajjRepositoryImpl(@ApplicationContext Context context) {
        this.context = context;
        this.database = HajjDatabase.getDatabase(context);
        this.ruleDao = database.hajjRuleDao();
        this.categoryDao = database.categoryDao();
        this.executorService = Executors.newFixedThreadPool(4); // Optimize thread pool size
        this.networkUtils = new NetworkUtils(context);
        this.jsonDataLoader = new JsonDataLoader(context);

        // Initialize data if needed
        initializeData();
    }

    /**
     * Initialize data from JSON assets if database is empty
     */
    private void initializeData() {
        executorService.execute(() -> {
            try {
                // Check if data exists
                int ruleCount = ruleDao.getRulesCountSync();
                int categoryCount = categoryDao.getCategoriesCountSync();

                if (ruleCount == 0 || categoryCount == 0) {
                    // Load initial data from JSON
                    loadInitialData();
                }

                // Update freshness status
                updateDataFreshness();
            } catch (Exception e) {
                // Log error but don't crash the app
                android.util.Log.e("HajjRepository", "Error initializing data", e);
            }
        });
    }

    /**
     * Load initial data from JSON assets
     */
    private void loadInitialData() {
        try {
            // Load categories
            List<Category> categories = jsonDataLoader.loadCategories();
            if (categories != null && !categories.isEmpty()) {
                categoryDao.insertAll(categories);
            }

            // Load rules
            List<HajjRule> rules = jsonDataLoader.loadRules();
            if (rules != null && !rules.isEmpty()) {
                ruleDao.insertAll(rules);
            }

            lastRefreshTime = System.currentTimeMillis();
            isDataFresh.postValue(true);

        } catch (Exception e) {
            android.util.Log.e("HajjRepository", "Error loading initial data", e);
        }
    }

    /**
     * Update data freshness status
     */
    private void updateDataFreshness() {
        long currentTime = System.currentTimeMillis();
        boolean fresh = (currentTime - lastRefreshTime) < CACHE_DURATION_MS;
        isDataFresh.postValue(fresh);
    }

    // Rule operations
    @Override
    public LiveData<List<HajjRule>> getAllRules() {
        return ruleDao.getAllRules();
    }

    @Override
    public LiveData<List<HajjRule>> getRulesByCategory(String category) {
        return ruleDao.getRulesByCategory(category);
    }

    @Override
    public LiveData<List<HajjRule>> getFavoriteRules() {
        return ruleDao.getFavoriteRules();
    }

    @Override
    public LiveData<List<HajjRule>> searchRules(String query) {
        return ruleDao.searchRules(query);
    }

    @Override
    public LiveData<HajjRule> getRuleById(int id) {
        return ruleDao.getRuleById(id);
    }

    // Category operations
    @Override
    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    @Override
    public LiveData<Category> getCategoryById(int id) {
        return categoryDao.getCategoryById(id);
    }

    @Override
    public LiveData<List<Category>> getFeaturedCategories() {
        return categoryDao.getFeaturedCategories();
    }

    // CRUD operations for rules
    @Override
    public void insert(HajjRule rule) {
        executorService.execute(() -> {
            try {
                ruleDao.insert(rule);
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error inserting rule", e);
            }
        });
    }

    @Override
    public void update(HajjRule rule) {
        executorService.execute(() -> {
            try {
                ruleDao.update(rule);
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error updating rule", e);
            }
        });
    }

    @Override
    public void delete(HajjRule rule) {
        executorService.execute(() -> {
            try {
                ruleDao.delete(rule);
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error deleting rule", e);
            }
        });
    }

    @Override
    public void deleteAllRules() {
        executorService.execute(() -> {
            try {
                ruleDao.deleteAllRules();
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error deleting all rules", e);
            }
        });
    }

    // CRUD operations for categories
    @Override
    public void insert(Category category) {
        executorService.execute(() -> {
            try {
                categoryDao.insert(category);
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error inserting category", e);
            }
        });
    }

    @Override
    public void update(Category category) {
        executorService.execute(() -> {
            try {
                categoryDao.update(category);
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error updating category", e);
            }
        });
    }

    @Override
    public void delete(Category category) {
        executorService.execute(() -> {
            try {
                categoryDao.delete(category);
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error deleting category", e);
            }
        });
    }

    // Bulk operations
    @Override
    public void insertAllRules(List<HajjRule> rules) {
        executorService.execute(() -> {
            try {
                ruleDao.insertAll(rules);
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error inserting rules", e);
            }
        });
    }

    @Override
    public void insertAllCategories(List<Category> categories) {
        executorService.execute(() -> {
            try {
                categoryDao.insertAll(categories);
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error inserting categories", e);
            }
        });
    }

    // Search and filtering
    @Override
    public LiveData<List<HajjRule>> searchRulesByTitle(String query) {
        return ruleDao.searchRulesByTitle(query);
    }

    @Override
    public LiveData<List<HajjRule>> searchRulesByContent(String query) {
        return ruleDao.searchRulesByContent(query);
    }

    @Override
    public LiveData<List<HajjRule>> getRulesWithEvidence() {
        return ruleDao.getRulesWithEvidence();
    }

    // Favorites management
    @Override
    public void toggleFavorite(int ruleId) {
        executorService.execute(() -> {
            try {
                ruleDao.toggleFavorite(ruleId);
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error toggling favorite", e);
            }
        });
    }

    @Override
    public LiveData<Integer> getFavoriteCount() {
        return ruleDao.getFavoriteCount();
    }

    // Cache and sync operations
    @Override
    public void refreshData() {
        executorService.execute(() -> {
            try {
                if (networkUtils.isNetworkAvailable()) {
                    // Clear existing data
                    ruleDao.deleteAllRules();
                    categoryDao.deleteAllCategories();

                    // Reload from JSON
                    loadInitialData();
                }
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error refreshing data", e);
            }
        });
    }

    @Override
    public LiveData<Boolean> isDataFresh() {
        return isDataFresh;
    }

    @Override
    public void clearCache() {
        executorService.execute(() -> {
            try {
                // Clear all data
                ruleDao.deleteAllRules();
                categoryDao.deleteAllCategories();

                // Reset freshness
                lastRefreshTime = 0;
                isDataFresh.postValue(false);
            } catch (Exception e) {
                android.util.Log.e("HajjRepository", "Error clearing cache", e);
            }
        });
    }

    // Statistics
    @Override
    public LiveData<Integer> getTotalRulesCount() {
        return ruleDao.getRulesCount();
    }

    @Override
    public LiveData<Integer> getTotalCategoriesCount() {
        return categoryDao.getCategoriesCount();
    }

    @Override
    public LiveData<Integer> getRulesWithEvidenceCount() {
        return ruleDao.getRulesWithEvidenceCount();
    }

    /**
     * Cleanup resources when repository is no longer needed
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}