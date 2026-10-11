package com.muslim.hajjrules.util;

import android.content.Context;

import com.muslim.hajjrules.R;

/** Persisted appearance choices and their corresponding Android theme overlays. */
public final class ThemeManager {
    public static final String PREFERENCES = "appearance";
    public static final String SELECTED_THEME = "selected_theme";
    public static final String DEFAULT_THEME = "hijaz_golden_dawn";

    private ThemeManager() {}

    public static String[] ids() {
        return new String[]{"sacred_rawdah", DEFAULT_THEME, "sacred_purity",
                "sanctuary_guidance", "kiswah_royale", "nocturnal_sanctuary"};
    }

    public static int styleFor(Context context) {
        String selected = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getString(SELECTED_THEME, DEFAULT_THEME);
        if ("sacred_rawdah".equals(selected)) return R.style.Theme_HajjApp_SacredRawdah;
        if ("sacred_purity".equals(selected)) return R.style.Theme_HajjApp_SacredPurity;
        if ("sanctuary_guidance".equals(selected)) return R.style.Theme_HajjApp_SanctuaryGuidance;
        if ("kiswah_royale".equals(selected)) return R.style.Theme_HajjApp_KiswahRoyale;
        if ("nocturnal_sanctuary".equals(selected)) return R.style.Theme_HajjApp_NocturnalSanctuary;
        return R.style.Theme_HajjApp;
    }
}
