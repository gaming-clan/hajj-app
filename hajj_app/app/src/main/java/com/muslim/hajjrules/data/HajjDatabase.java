package com.muslim.hajjrules.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;

import com.muslim.hajjrules.model.HajjRule;
import com.muslim.hajjrules.model.Category;

/**
 * Room Database for Hajj Rules application
 * Provides database access for HajjRule and Category entities
 */
@Database(
    entities = {HajjRule.class, Category.class},
    version = 2,
    exportSchema = false
)
public abstract class HajjDatabase extends RoomDatabase {
    private static volatile HajjDatabase INSTANCE;

    public abstract HajjRuleDao hajjRuleDao();
    public abstract CategoryDao categoryDao();

    public static HajjDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HajjDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            HajjDatabase.class,
                            "hajj_database"
                    )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration() // Remove in production
                    .allowMainThreadQueries() // Remove in production
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Migration from version 1 to 2
     * Adds Category table and updates HajjRule table structure
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(androidx.sqlite.db.SupportSQLiteDatabase database) {
            // Create categories table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY NOT NULL, " +
                "name TEXT NOT NULL, " +
                "title TEXT NOT NULL, " +
                "description TEXT NOT NULL, " +
                "image TEXT, " +
                "icon TEXT, " +
                "color TEXT, " +
                "featured INTEGER NOT NULL DEFAULT 0, " +
                "`order` INTEGER NOT NULL DEFAULT 0" +
                ")"
            );

            // Update hajj_rules table to add new columns
            try {
                database.execSQL("ALTER TABLE hajj_rules ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0");
            } catch (Exception e) {
                // Column might already exist
            }

            try {
                database.execSQL("ALTER TABLE hajj_rules ADD COLUMN `order` INTEGER NOT NULL DEFAULT 0");
            } catch (Exception e) {
                // Column might already exist
            }

            try {
                database.execSQL("ALTER TABLE hajj_rules ADD COLUMN quranicReference TEXT");
            } catch (Exception e) {
                // Column might already exist
            }

            try {
                database.execSQL("ALTER TABLE hajj_rules ADD COLUMN hadithReference TEXT");
            } catch (Exception e) {
                // Column might already exist
            }

            try {
                database.execSQL("ALTER TABLE hajj_rules ADD COLUMN lastAccessed INTEGER");
            } catch (Exception e) {
                // Column might already exist
            }

            // Create indexes for better performance
            database.execSQL("CREATE INDEX IF NOT EXISTS index_hajj_rules_category ON hajj_rules(category)");
            database.putExtra("index_hajj_rules_isFavorite ON hajj_rules(isFavorite)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_categories_featured ON categories(featured)");
            database.execSQL("CREATE INDEX IF NOT EXISTS index_categories_order ON categories(`order`)");
        }
    };
}