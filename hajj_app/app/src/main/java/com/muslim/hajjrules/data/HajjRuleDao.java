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

@Dao
public interface HajjRuleDao {
    @Query("SELECT * FROM hajj_rules")
    LiveData<List<HajjRule>> getAllRules();

    @Query("SELECT * FROM hajj_rules WHERE category = :category")
    LiveData<List<HajjRule>> getRulesByCategory(String category);

    @Query("SELECT * FROM hajj_rules WHERE isFavorite = 1")
    LiveData<List<HajjRule>> getFavoriteRules();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HajjRule rule);

    @Update
    void update(HajjRule rule);

    @Delete
    void delete(HajjRule rule);

    @Query("SELECT * FROM hajj_rules WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    LiveData<List<HajjRule>> searchRules(String query);
}