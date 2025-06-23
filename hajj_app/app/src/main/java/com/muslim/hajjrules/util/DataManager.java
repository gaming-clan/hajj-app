package com.muslim.hajjrules.util;

import android.content.Context;
import android.util.Log;

import com.muslim.hajjrules.model.Category;
import com.muslim.hajjrules.model.HajjRule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static final String TAG = "DataManager";
    private static DataManager instance;
    private final Context context;
    private JSONObject hajjRulesJson;
    private List<Category> categories;
    private Map<Integer, List<HajjRule>> rulesByCategory;

    private DataManager(Context context) {
        this.context = context.getApplicationContext();
        this.categories = new ArrayList<>();
        this.rulesByCategory = new HashMap<>();
        loadData();
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    private void loadData() {
        try {
            // Load JSON from assets
            String jsonString = loadJSONFromAsset("hajj_rules.json");
            if (jsonString != null) {
                hajjRulesJson = new JSONObject(jsonString);
                initializeCategories();
                loadRules();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
        }
    }

    private String loadJSONFromAsset(String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Log.e(TAG, "Error reading JSON file: " + ex.getMessage());
            return null;
        }
        return json;
    }

    private void initializeCategories() {
        categories.add(new Category(1, "Hyrje dhe Rëndësia", "Hyrje dhe rëndësia e Haxhit në Islam", android.R.drawable.ic_dialog_info));
        categories.add(new Category(2, "Shtyllat e Islamit", "Pesë shtyllat themelore të Islamit", com.muslim.hajjrules.R.drawable.pillars_of_islam));
        categories.add(new Category(3, "Detyrimi i Haxhit", "Detyrimi dhe kushtet e kryerjes së Haxhit", com.muslim.hajjrules.R.drawable.hajj_obligation));
        categories.add(new Category(4, "Edukata e Udhëtimit", "Rregullat dhe etika gjatë udhëtimit për Haxh", com.muslim.hajjrules.R.drawable.travel_etiquette));
        categories.add(new Category(5, "Ihrami", "Rregullat dhe procedurat e Ihramit", com.muslim.hajjrules.R.drawable.ihram));
        categories.add(new Category(6, "Ndalesat gjatë Ihramit", "Veprimet e ndaluara gjatë Ihramit", com.muslim.hajjrules.R.drawable.prohibitions));
        categories.add(new Category(7, "Vendcaktimet", "Vendcaktimet (Mikat) për Haxhin", android.R.drawable.ic_menu_mylocation));
        categories.add(new Category(8, "Qabja", "Informacion për Qaben e shenjtë", com.muslim.hajjrules.R.drawable.hajj_kaaba_1));
    }

    private void loadRules() {
        try {
            // Load introduction rules
            List<HajjRule> introRules = new ArrayList<>();
            JSONObject intro = hajjRulesJson.getJSONObject("introduction");
            HajjRule introRule = new HajjRule(
                "Hyrje", 
                intro.getString("description"), 
                1, // categoryId
                com.muslim.hajjrules.R.drawable.image_1, // Introduction image
                1
            );
            introRules.add(introRule);
            
            HajjRule qabjaRule = new HajjRule(
                "Qabja", 
                intro.getString("qabja"), 
                1, // categoryId
                com.muslim.hajjrules.R.drawable.hajj_kaaba_1, // Kaaba image
                2
            );
            introRules.add(qabjaRule);
            rulesByCategory.put(1, introRules);

            // Load pillars of Islam
            List<HajjRule> pillarRules = new ArrayList<>();
            JSONArray pillars = hajjRulesJson.getJSONArray("shtyllat_e_islamit");
            for (int i = 0; i < pillars.length(); i++) {
                JSONObject pillar = pillars.getJSONObject(i);
                int imageResId = com.muslim.hajjrules.R.drawable.pillars_of_islam;
                // Use specific images for each pillar
                switch (i) {
                    case 0: imageResId = com.muslim.hajjrules.R.drawable.image_2; break; // Shahada
                    case 1: imageResId = com.muslim.hajjrules.R.drawable.image_3; break; // Prayer
                    case 2: imageResId = com.muslim.hajjrules.R.drawable.image_4; break; // Zakat
                    case 3: imageResId = com.muslim.hajjrules.R.drawable.image_5; break; // Fasting
                    case 4: imageResId = com.muslim.hajjrules.R.drawable.pillars_of_islam; break; // Hajj
                }
                HajjRule pillarRule = new HajjRule(
                        pillar.getString("name"),
                        pillar.getString("description"),
                        2, // categoryId
                        imageResId,
                        pillar.getInt("id")
                );
                pillarRules.add(pillarRule);
            }
            rulesByCategory.put(2, pillarRules);

            // Load hajj obligation
            List<HajjRule> obligationRules = new ArrayList<>();
            JSONObject obligation = hajjRulesJson.getJSONObject("detyrimi_i_haxhit");
            HajjRule obligationRule1 = new HajjRule(
                "Detyrimi", 
                obligation.getString("description"), 
                3, // categoryId
                com.muslim.hajjrules.R.drawable.hajj_obligation, 
                1
            );
            obligationRules.add(obligationRule1);
            
            HajjRule obligationRule2 = new HajjRule(
                "Hadith", 
                obligation.getString("hadith"), 
                3, // categoryId
                com.muslim.hajjrules.R.drawable.image_6, 
                2
            );
            obligationRules.add(obligationRule2);
            
            HajjRule obligationRule3 = new HajjRule(
                "Kushtet", 
                obligation.getString("kushtet"), 
                3, // categoryId
                com.muslim.hajjrules.R.drawable.image_7, 
                3
            );
            obligationRules.add(obligationRule3);
            rulesByCategory.put(3, obligationRules);

            // Load travel etiquette
            List<HajjRule> etiquetteRules = new ArrayList<>();
            JSONArray etiquettes = hajjRulesJson.getJSONArray("edukata_e_udhetimit");
            for (int i = 0; i < etiquettes.length(); i++) {
                JSONObject etiquette = etiquettes.getJSONObject(i);
                int imageResId = com.muslim.hajjrules.R.drawable.travel_etiquette;
                // Use specific images for different etiquette rules
                if (i < 6) {
                    imageResId = com.muslim.hajjrules.R.drawable.travel_etiquette;
                } else if (i < 12) {
                    switch (i) {
                        case 6: imageResId = com.muslim.hajjrules.R.drawable.image_8; break;
                        case 7: imageResId = com.muslim.hajjrules.R.drawable.image_9; break;
                        case 8: imageResId = com.muslim.hajjrules.R.drawable.image_10; break;
                        case 9: imageResId = com.muslim.hajjrules.R.drawable.image_11; break;
                        case 10: imageResId = com.muslim.hajjrules.R.drawable.image_12; break;
                        case 11: imageResId = com.muslim.hajjrules.R.drawable.image_13; break;
                    }
                }
                HajjRule etiquetteRule = new HajjRule(
                        etiquette.getString("rule"),
                        etiquette.getString("description"),
                        4, // categoryId
                        imageResId,
                        etiquette.getInt("id")
                );
                etiquetteRules.add(etiquetteRule);
            }
            rulesByCategory.put(4, etiquetteRules);

            // Load ihram rules
            List<HajjRule> ihramRules = new ArrayList<>();
            JSONObject ihram = hajjRulesJson.getJSONObject("ihrami");
            HajjRule ihramRule1 = new HajjRule(
                "Çfarë është Ihrami", 
                ihram.getString("description"), 
                5, // categoryId
                com.muslim.hajjrules.R.drawable.ihram, 
                1
            );
            ihramRules.add(ihramRule1);
            
            HajjRule ihramRule2 = new HajjRule(
                "Koha e Ihramit", 
                ihram.getString("koha"), 
                5, // categoryId
                com.muslim.hajjrules.R.drawable.ihram, 
                2
            );
            ihramRules.add(ihramRule2);

            JSONArray nijetTypes = ihram.getJSONArray("llojet_e_nijetit");
            for (int i = 0; i < nijetTypes.length(); i++) {
                JSONObject nijet = nijetTypes.getJSONObject(i);
                HajjRule nijetRule = new HajjRule(
                        "Nijeti për " + nijet.getString("lloji"),
                        nijet.getString("nijeti"),
                        5, // categoryId
                        com.muslim.hajjrules.R.drawable.ihram,
                        3 + i
                );
                ihramRules.add(nijetRule);
            }

            JSONArray beforeIhram = ihram.getJSONArray("para_veshjes");
            for (int i = 0; i < beforeIhram.length(); i++) {
                JSONObject prep = beforeIhram.getJSONObject(i);
                HajjRule prepRule = new HajjRule(
                        prep.getString("veprim"),
                        prep.getString("description"),
                        5, // categoryId
                        com.muslim.hajjrules.R.drawable.ihram,
                        7 + i
                );
                ihramRules.add(prepRule);
            }
            rulesByCategory.put(5, ihramRules);

            // Load prohibitions
            List<HajjRule> prohibitionRules = new ArrayList<>();
            JSONArray prohibitions = hajjRulesJson.getJSONArray("ndalesat_gjate_ihramit");
            for (int i = 0; i < prohibitions.length(); i++) {
                JSONObject prohibition = prohibitions.getJSONObject(i);
                HajjRule prohibitionRule = new HajjRule(
                        prohibition.getString("ndalesa"),
                        prohibition.getString("description"),
                        6, // categoryId
                        com.muslim.hajjrules.R.drawable.prohibitions,
                        prohibition.getInt("id")
                );
                prohibitionRules.add(prohibitionRule);
            }
            rulesByCategory.put(6, prohibitionRules);

            // Load miqat (vendcaktimet)
            List<HajjRule> miqatRules = new ArrayList<>();
            JSONArray miqats = hajjRulesJson.getJSONArray("vendcaktimet");
            for (int i = 0; i < miqats.length(); i++) {
                JSONObject miqat = miqats.getJSONObject(i);
                HajjRule miqatRule = new HajjRule(
                        miqat.getString("emri"),
                        miqat.getString("per_ke") + " - " + miqat.getString("largesia"),
                        7, // categoryId
                        com.muslim.hajjrules.R.drawable.miqats,
                        miqat.getInt("id")
                );
                miqatRules.add(miqatRule);
            }
            rulesByCategory.put(7, miqatRules);

            // Load Kaaba info
            List<HajjRule> kaabaRules = new ArrayList<>();
            JSONObject kaaba = hajjRulesJson.getJSONObject("qabja");
            HajjRule kaabaRule1 = new HajjRule(
                "Qabja", 
                kaaba.getString("description"), 
                8, // categoryId
                com.muslim.hajjrules.R.drawable.hajj_kaaba_1, 
                1
            );
            kaabaRules.add(kaabaRule1);
            
            HajjRule kaabaRule2 = new HajjRule(
                "Rëndësia e Qabes", 
                kaaba.getString("rendesia"), 
                8, // categoryId
                com.muslim.hajjrules.R.drawable.hajj_kaaba_1, 
                2
            );
            kaabaRules.add(kaabaRule2);
            rulesByCategory.put(8, kaabaRules);

        } catch (JSONException e) {
            Log.e(TAG, "Error loading rules: " + e.getMessage());
        }
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<HajjRule> getRulesByCategory(int categoryId) {
        List<HajjRule> rules = rulesByCategory.get(categoryId);
        return rules != null ? rules : new ArrayList<>();
    }

    public List<HajjRule> getRulesForCategory(int categoryId) {
        List<HajjRule> rules = rulesByCategory.get(categoryId);
        return rules != null ? rules : new ArrayList<>();
    }

    public List<HajjRule> searchRules(String query) {
        List<HajjRule> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (List<HajjRule> rules : rulesByCategory.values()) {
            for (HajjRule rule : rules) {
                if (rule.getTitle().toLowerCase().contains(lowerQuery) ||
                    rule.getDescription().toLowerCase().contains(lowerQuery)) {
                    results.add(rule);
                }
            }
        }
        return results;
    }
}

