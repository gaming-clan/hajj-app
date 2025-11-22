package com.muslim.hajjrules.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.muslim.hajjrules.model.HajjRule;

@Database(entities = {HajjRule.class}, version = 1, exportSchema = false)
public abstract class HajjDatabase extends RoomDatabase {
    private static volatile HajjDatabase INSTANCE;
    public abstract HajjRuleDao hajjRuleDao();

    public static HajjDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HajjDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            HajjDatabase.class,
                            "hajj_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}