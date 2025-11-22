package com.muslim.hajjrules.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.muslim.hajjrules.model.HajjRule;
import com.muslim.hajjrules.model.Category;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Utility class for loading data from JSON assets
 * Uses Gson for JSON parsing with proper error handling
 */
@Singleton
public class JsonDataLoader {

    private static final String TAG = "JsonDataLoader";
    private static final String HAJJ_RULES_FILE = "hajj_rules_extended.json";
    private static final String HAJJ_RULES_BACKUP_FILE = "hajj_rules.json";

    private final Context context;
    private final Gson gson;

    @Inject
    public JsonDataLoader(Context context) {
        this.context = context;
        this.gson = new GsonBuilder()
                .setLenient() // Allows lenient parsing for malformed JSON
                .create();
    }

    /**
     * Load categories from JSON assets
     */
    public List<Category> loadCategories() {
        try {
            // Try primary file first
            List<Category> categories = loadCategoriesFromAsset(HAJJ_RULES_FILE);
            if (categories != null && !categories.isEmpty()) {
                return categories;
            }

            // Fallback to backup file
            return loadCategoriesFromAsset(HAJJ_RULES_BACKUP_FILE);
        } catch (Exception e) {
            Log.e(TAG, "Error loading categories from JSON", e);
            return createDefaultCategories();
        }
    }

    /**
     * Load rules from JSON assets
     */
    public List<HajjRule> loadRules() {
        try {
            // Try primary file first
            List<HajjRule> rules = loadRulesFromAsset(HAJJ_RULES_FILE);
            if (rules != null && !rules.isEmpty()) {
                return rules;
            }

            // Fallback to backup file
            return loadRulesFromAsset(HAJJ_RULES_BACKUP_FILE);
        } catch (Exception e) {
            Log.e(TAG, "Error loading rules from JSON", e);
            return createDefaultRules();
        }
    }

    /**
     * Load categories from a specific asset file
     */
    private List<Category> loadCategoriesFromAsset(String fileName) {
        try {
            String jsonString = readAssetFile(fileName);
            if (jsonString == null || jsonString.trim().isEmpty()) {
                return null;
            }

            // Parse JSON
            Map<String, Object> jsonData = gson.fromJson(jsonString, new TypeToken<Map<String, Object>>(){}.getType());
            if (jsonData == null) {
                return null;
            }

            List<Category> categories = new ArrayList<>();
            int categoryId = 1;

            // Add categories based on JSON structure
            if (jsonData.containsKey("shtyllat_e_islamit")) {
                categories.add(new Category(
                        categoryId++,
                        "shtyllat_e_islamit",
                        "Shtyllat e Islamit",
                        "Pesë shtyllat themelore të fesë islame",
                        "pillars_of_islam.png",
                        "account_balance",
                        "#2E7D32",
                        true
                ));
            }

            if (jsonData.containsKey("edukata_e_udhetimit")) {
                categories.add(new Category(
                        categoryId++,
                        "edukata_e_udhetimit",
                        "Edukata e Udhëtimit",
                        "Rregullat dhe edukativa para dhe gjatë udhëtimit për Haxh",
                        "travel_etiquette.png",
                        "luggage",
                        "#1976D2",
                        true
                ));
            }

            if (jsonData.containsKey("ihrami")) {
                categories.add(new Category(
                        categoryId++,
                        "ihrami",
                        "Ihrami",
                        "Rregullat dhe kërkesat për ihramin",
                        "ihram.png",
                        "verified",
                        "#F57C00",
                        true
                ));
            }

            if (jsonData.containsKey("ndalesat_gjate_ihramit")) {
                categories.add(new Category(
                        categoryId++,
                        "ndalesat_gjate_ihramit",
                        "Ndalesat gjatë Ihramit",
                        "Gjërat që janë të ndaluara gjatë gjendjes së ihramit",
                        "prohibitions.png",
                        "block",
                        "#D32F2F",
                        true
                ));
            }

            if (jsonData.containsKey("vendcaktimet")) {
                categories.add(new Category(
                        categoryId++,
                        "vendcaktimet",
                        "Vendcaktimet (Miqat)",
                        "Vendcaktimet ku bëhet ihram-i",
                        "miqats.jpg",
                        "place",
                        "#7B1FA2",
                        true
                ));
            }

            return categories;

        } catch (Exception e) {
            Log.e(TAG, "Error parsing categories from " + fileName, e);
            return null;
        }
    }

    /**
     * Load rules from a specific asset file
     */
    private List<HajjRule> loadRulesFromAsset(String fileName) {
        try {
            String jsonString = readAssetFile(fileName);
            if (jsonString == null || jsonString.trim().isEmpty()) {
                return null;
            }

            // Parse JSON
            Map<String, Object> jsonData = gson.fromJson(jsonString, new TypeToken<Map<String, Object>>(){}.getType());
            if (jsonData == null) {
                return null;
            }

            List<HajjRule> rules = new ArrayList<>();
            int ruleId = 1;

            // Extract rules from different sections
            if (jsonData.containsKey("shtyllat_e_islamit")) {
                List<Map<String, Object>> pillars = (List<Map<String, Object>>) jsonData.get("shtyllat_e_islamit");
                for (Map<String, Object> pillar : pillars) {
                    rules.add(new HajjRule(
                            ruleId++,
                            pillar.get("name") != null ? pillar.get("name").toString() : "",
                            pillar.get("description") != null ? pillar.get("description").toString() : "",
                            "shtyllat_e_islamit",
                            false,
                            0,
                            null,
                            null
                    ));
                }
            }

            if (jsonData.containsKey("edukata_e_udhetimit")) {
                List<Map<String, Object>> etiquetteItems = (List<Map<String, Object>>) jsonData.get("edukata_e_udhetimit");
                for (Map<String, Object> item : etiquetteItems) {
                    rules.add(new HajjRule(
                            ruleId++,
                            item.get("rule") != null ? item.get("rule").toString() : "",
                            item.get("description") != null ? item.get("description").toString() : "",
                            "edukata_e_udhetimit",
                            false,
                            0,
                            null,
                            null
                    ));
                }
            }

            if (jsonData.containsKey("ndalesat_gjate_ihramit")) {
                List<Map<String, Object>> prohibitions = (List<Map<String, Object>>) jsonData.get("ndalesat_gjate_ihramit");
                for (Map<String, Object> prohibition : prohibitions) {
                    rules.add(new HajjRule(
                            ruleId++,
                            prohibition.get("ndalesa") != null ? prohibition.get("ndalesa").toString() : "",
                            prohibition.get("description") != null ? prohibition.get("description").toString() : "",
                            "ndalesat_gjate_ihramit",
                            false,
                            0,
                            null,
                            null
                    ));
                }
            }

            if (jsonData.containsKey("vendcaktimet")) {
                List<Map<String, Object>> miqats = (List<Map<String, Object>>) jsonData.get("vendcaktimet");
                for (Map<String, Object> miqat : miqats) {
                    rules.add(new HajjRule(
                            ruleId++,
                            miqat.get("emri") != null ? miqat.get("emri").toString() : "",
                            miqat.get("per_ke") != null ? miqat.get("per_ke").toString() : "",
                            "vendcaktimet",
                            false,
                            0,
                            miqat.get("image") != null ? miqat.get("image").toString() : null,
                            null
                    ));
                }
            }

            return rules;

        } catch (Exception e) {
            Log.e(TAG, "Error parsing rules from " + fileName, e);
            return null;
        }
    }

    /**
     * Read a file from assets
     */
    private String readAssetFile(String fileName) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();

            char[] buffer = new char[1024];
            int length;
            while ((length = reader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, length);
            }

            reader.close();
            inputStream.close();

            return stringBuilder.toString();

        } catch (IOException e) {
            Log.e(TAG, "Error reading asset file: " + fileName, e);
            return null;
        }
    }

    /**
     * Create default categories if JSON loading fails
     */
    private List<Category> createDefaultCategories() {
        List<Category> categories = new ArrayList<>();

        categories.add(new Category(
                1,
                "shtyllat_e_islamit",
                "Shtyllat e Islamit",
                "Pesë shtyllat themelore të fesë islame",
                "pillars_of_islam.png",
                "account_balance",
                "#2E7D32",
                true
        ));

        categories.add(new Category(
                2,
                "edukata_e_udhetimit",
                "Edukata e Udhëtimit",
                "Rregullat dhe edukativa para dhe gjatë udhëtimit për Haxh",
                "travel_etiquette.png",
                "luggage",
                "#1976D2",
                true
        ));

        categories.add(new Category(
                3,
                "ihrami",
                "Ihrami",
                "Rregullat dhe kërkesat për ihramin",
                "ihram.png",
                "verified",
                "#F57C00",
                true
        ));

        categories.add(new Category(
                4,
                "ndalesat_gjate_ihramit",
                "Ndalesat gjatë Ihramit",
                "Gjërat që janë të ndaluara gjatë gjendjes së ihramit",
                "prohibitions.png",
                "block",
                "#D32F2F",
                true
        ));

        categories.add(new Category(
                5,
                "vendcaktimet",
                "Vendcaktimet (Miqat)",
                "Vendcaktimet ku bëhet ihram-i",
                "miqats.jpg",
                "place",
                "#7B1FA2",
                true
        ));

        return categories;
    }

    /**
     * Create default rules if JSON loading fails
     */
    private List<HajjRule> createDefaultRules() {
        List<HajjRule> rules = new ArrayList<>();

        // Add a few default rules for each category
        rules.add(new HajjRule(
                1,
                "Dëshmia",
                "Nuk ka zot përveç Allahut dhe Muhammedi është i Dërguari i Tij.",
                "shtyllat_e_islamit",
                false,
                0,
                null,
                null
        ));

        rules.add(new HajjRule(
                2,
                "Larja e borxheve",
                "Udhëtari duhet të lajë borxhet dhe të shkruajë testamentin para nisjes.",
                "edukata_e_udhetimit",
                false,
                0,
                null,
                null
        ));

        rules.add(new HajjRule(
                3,
                "Mbulimi i kokës",
                "Nuk lejohet të mbulohet koka për burrat.",
                "ndalesat_gjate_ihramit",
                false,
                0,
                null,
                null
        ));

        return rules;
    }
}