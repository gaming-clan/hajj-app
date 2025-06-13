package com.muslim.hajjrules.util;

import android.content.Context;
import android.util.Log;

import com.muslim.hajjrules.model.Category;
import com.muslim.hajjrules.model.HajjRule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        categories.add(new Category(2, "Shtyllat e Islamit", "Pesë shtyllat themelore të Islamit", android.R.drawable.ic_menu_sort_by_size));
        categories.add(new Category(3, "Detyrimi i Haxhit", "Detyrimi dhe kushtet e kryerjes së Haxhit", android.R.drawable.ic_menu_agenda));
        categories.add(new Category(4, "Edukata e Udhëtimit", "Rregullat dhe etika gjatë udhëtimit për Haxh", android.R.drawable.ic_menu_directions));
        categories.add(new Category(5, "Ihrami", "Rregullat dhe procedurat e Ihramit", android.R.drawable.ic_menu_gallery));
        categories.add(new Category(6, "Ndalesat gjatë Ihramit", "Veprimet e ndaluara gjatë Ihramit", android.R.drawable.ic_menu_close_clear_cancel));
        categories.add(new Category(7, "Vendcaktimet", "Vendcaktimet (Mikat) për Haxhin", android.R.drawable.ic_menu_mylocation));
        categories.add(new Category(8, "Qabja", "Informacion për Qaben e shenjtë", android.R.drawable.ic_menu_compass));
    }

    private void loadRules() {
        try {
            // Load introduction rules
            List<HajjRule> introRules = new ArrayList<>();
            JSONObject intro = hajjRulesJson.getJSONObject("introduction");
            introRules.add(new HajjRule("Hyrje", intro.getString("description"), 1, 1));
            introRules.add(new HajjRule("Qabja", intro.getString("qabja"), 1, 2));
            rulesByCategory.put(1, introRules);

            // Load pillars of Islam
            List<HajjRule> pillarRules = new ArrayList<>();
            JSONArray pillars = hajjRulesJson.getJSONArray("shtyllat_e_islamit");
            for (int i = 0; i < pillars.length(); i++) {
                JSONObject pillar = pillars.getJSONObject(i);
                pillarRules.add(new HajjRule(
                        pillar.getString("name"),
                        pillar.getString("description"),
                        2,
                        pillar.getInt("id")
                ));
            }
            rulesByCategory.put(2, pillarRules);

            // Load hajj obligation
            List<HajjRule> obligationRules = new ArrayList<>();
            JSONObject obligation = hajjRulesJson.getJSONObject("detyrimi_i_haxhit");
            obligationRules.add(new HajjRule("Detyrimi", obligation.getString("description"), 3, 1));
            obligationRules.add(new HajjRule("Hadith", obligation.getString("hadith"), 3, 2));
            obligationRules.add(new HajjRule("Kushtet", obligation.getString("kushtet"), 3, 3));
            rulesByCategory.put(3, obligationRules);

            // Load travel etiquette
            List<HajjRule> etiquetteRules = new ArrayList<>();
            JSONArray etiquettes = hajjRulesJson.getJSONArray("edukata_e_udhetimit");
            for (int i = 0; i < etiquettes.length(); i++) {
                JSONObject etiquette = etiquettes.getJSONObject(i);
                etiquetteRules.add(new HajjRule(
                        etiquette.getString("rule"),
                        etiquette.getString("description"),
                        4,
                        etiquette.getInt("id")
                ));
            }
            rulesByCategory.put(4, etiquetteRules);

            // Load ihram rules
            List<HajjRule> ihramRules = new ArrayList<>();
            JSONObject ihram = hajjRulesJson.getJSONObject("ihrami");
            ihramRules.add(new HajjRule("Çfarë është Ihrami", ihram.getString("description"), 5, 1));
            ihramRules.add(new HajjRule("Koha e Ihramit", ihram.getString("koha"), 5, 2));
            
            JSONArray nijetTypes = ihram.getJSONArray("llojet_e_nijetit");
            for (int i = 0; i < nijetTypes.length(); i++) {
                JSONObject nijet = nijetTypes.getJSONObject(i);
                ihramRules.add(new HajjRule(
                        "Nijeti për " + nijet.getString("lloji"),
                        nijet.getString("nijeti"),
                        5,
                        3 + i
                ));
            }
            
            JSONArray beforeIhram = ihram.getJSONArray("para_veshjes");
            for (int i = 0; i < beforeIhram.length(); i++) {
                JSONObject prep = beforeIhram.getJSONObject(i);
                ihramRules.add(new HajjRule(
                        prep.getString("veprim"),
                        prep.getString("description"),
                        5,
                        7 + i
                ));
            }
            rulesByCategory.put(5, ihramRules);

            // Load prohibitions
            List<HajjRule> prohibitionRules = new ArrayList<>();
            JSONArray prohibitions = hajjRulesJson.getJSONArray("ndalesat_gjate_ihramit");
            for (int i = 0; i < prohibitions.length(); i++) {
                JSONObject prohibition = prohibitions.getJSONObject(i);
                prohibitionRules.add(new HajjRule(
                        prohibition.getString("ndalesa"),
                        prohibition.getString("description"),
                        6,
                        prohibition.getInt("id")
                ));
            }
            rulesByCategory.put(6, prohibitionRules);

            // Load miqat (vendcaktimet)
            List<HajjRule> miqatRules = new ArrayList<>();
            JSONArray miqats = hajjRulesJson.getJSONArray("vendcaktimet");
            for (int i = 0; i < miqats.length(); i++) {
                JSONObject miqat = miqats.getJSONObject(i);
                miqatRules.add(new HajjRule(
                        miqat.getString("emri"),
                        miqat.getString("per_ke") + " - " + miqat.getString("largesia"),
                        7,
                        miqat.getInt("id")
                ));
            }
            rulesByCategory.put(7, miqatRules);

            // Load Kaaba info
            List<HajjRule> kaabaRules = new ArrayList<>();
            JSONObject kaaba = hajjRulesJson.getJSONObject("qabja");
            kaabaRules.add(new HajjRule("Qabja", kaaba.getString("description"), 8, 1));
            kaabaRules.add(new HajjRule("Rëndësia e Qabes", kaaba.getString("rendesia"), 8, 2));
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
