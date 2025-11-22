package com.muslim.hajjrules.di;

import android.content.Context;

import androidx.room.Room;

import com.muslim.hajjrules.data.HajjDatabase;
import com.muslim.hajjrules.data.HajjRuleDao;
import com.muslim.hajjrules.data.CategoryDao;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * Dagger Hilt module for database dependencies
 * Provides Room database and DAO instances
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class DatabaseModule {

    @Provides
    @Singleton
    public static HajjDatabase provideHajjDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                HajjDatabase.class,
                "hajj_database"
            )
            .fallbackToDestructiveMigration() // For development - consider proper migrations in production
            .allowMainThreadQueries() // Only for development - remove in production
            .build();
    }

    @Binds
    public abstract HajjRuleDao bindHajjRuleDao(HajjDatabase database);

    @Binds
    public abstract CategoryDao bindCategoryDao(HajjDatabase database);
}