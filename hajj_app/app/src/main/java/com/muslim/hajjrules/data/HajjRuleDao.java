package com.muslim.hajjrules.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.muslim.hajjrules.model.HajjRule;

import java.util.List;

/**
 * Data Access Object for HajjRule entities
 * Provides database access methods for HajjRule operations
 */
@Dao
public interface HajjRuleDao {

    // Basic CRUD operations
    @Query("SELECT * FROM hajj_rules ORDER BY category, `order` ASC")
    LiveData<List<HajjRule>> getAllRules();

    @Query("SELECT * FROM hajj_rules WHERE category = :category ORDER BY `order` ASC")
    LiveData<List<HajjRule>> getRulesByCategory(String category);

    @Query("SELECT * FROM hajj_rules WHERE isFavorite = 1 ORDER BY category, `order` ASC")
    LiveData<List<HajjRule>> getFavoriteRules();

    @Query("SELECT * FROM hajj_rules WHERE id = :id")
    LiveData<HajjRule> getRuleById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HajjRule rule);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<HajjRule> rules);

    @Update
    void update(HajjRule rule);

    @Delete
    void delete(HajjRule rule);

    @Query("DELETE FROM hajj_rules")
    void deleteAllRules();

    // Search operations
    @Query("SELECT * FROM hajj_rules WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY category, `order` ASC")
    LiveData<List<HajjRule>> searchRules(String query);

    @Query("SELECT * FROM hajj_rules WHERE title LIKE '%' || :query || '%' ORDER BY category, `order` ASC")
    LiveData<List<HajjRule>> searchRulesByTitle(String query);

    @Query("SELECT * FROM hajj_rules WHERE description LIKE '%' || :query || '%' ORDER BY category, `order` ASC")
    LiveData<List<HajjRule>> searchRulesByContent(String query);

    // Evidence and verification operations
    @Query("SELECT * FROM hajj_rules WHERE (quranicReference IS NOT NULL OR hadithReference IS NOT NULL) ORDER BY category, `order` ASC")
    LiveData<List<HajjRule>> getRulesWithEvidence();

    // Favorites management
    @Query("UPDATE hajj_rules SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    void toggleFavorite(int id);

    @Query("SELECT COUNT(*) FROM hajj_rules WHERE isFavorite = 1")
    LiveData<Integer> getFavoriteCount();

    // Statistics operations
    @Query("SELECT COUNT(*) FROM hajj_rules")
    LiveData<Integer> getRulesCount();

    @Query("SELECT COUNT(*) FROM hajj_rules")
    int getRulesCountSync();

    @Query("SELECT COUNT(*) FROM hajj_rules WHERE (quranicReference IS NOT NULL OR hadithReference IS NOT NULL)")
    LiveData<Integer> getRulesWithEvidenceCount();

    // Category-specific operations
    @Query("SELECT COUNT(*) FROM hajj_rules WHERE category = :category")
    LiveData<Integer> getRulesCountByCategory(String category);

    // Order management
    @Query("UPDATE hajj_rules SET `order` = :order WHERE id = :id")
    void updateRuleOrder(int id, int order);

    @Query("UPDATE hajj_rules SET `order` = `order` + 1 WHERE `order` >= :order AND category = :category")
    void shiftRulesDown(int order, String category);

    // Content operations
    @Query("UPDATE hajj_rules SET title = :title WHERE id = :id")
    void updateRuleTitle(int id, String title);

    @Query("UPDATE hajj_rules SET description = :description WHERE id = :id")
    void updateRuleDescription(int id, String description);

    @Query("UPDATE hajj_rules SET image = :image WHERE id = :id")
    void updateRuleImage(int id, String image);

    // Pagination support
    @Query("SELECT * FROM hajj_rules ORDER BY category, `order` ASC LIMIT :limit OFFSET :offset")
    LiveData<List<HajjRule>> getRulesByRange(int limit, int offset);

    // Advanced search with multiple filters
    @Query("SELECT * FROM hajj_rules WHERE " +
           "(:category IS NULL OR category = :category) AND " +
           "(:isFavorite IS NULL OR isFavorite = :isFavorite) AND " +
           "(title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') " +
           "ORDER BY category, `order` ASC")
    LiveData<List<HajjRule>> searchRulesWithFilters(String query, String category, Boolean isFavorite);

    // Content integrity checks
    @Query("SELECT * FROM hajj_rules WHERE title IS NULL OR title = '' OR description IS NULL OR description = ''")
    LiveData<List<HajjRule>> getRulesWithMissingContent();

    // Recent access tracking (if implemented)
    @Query("UPDATE hajj_rules SET lastAccessed = :timestamp WHERE id = :id")
    void updateLastAccessed(int id, long timestamp);

    @Query("SELECT * FROM hajj_rules WHERE lastAccessed > :timestamp ORDER BY lastAccessed DESC LIMIT :limit")
    LiveData<List<HajjRule>> getRecentlyAccessedRules(long timestamp, int limit);
}