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

    public static String description(int index) {
        String[] descriptions = {
                "Gjelbërimi i Rawdahut, nenexhik i butë dhe ar antik.",
                "Pergamenë e ngrohtë, gjelbërim palme dhe ari i agimit.",
                "E bardhë e qetë guri me nuanca të përmbajtura rëre.",
                "Tonalitete të buta oborri me gjelbërim të shenjtë.",
                "Obsidian i thellë me shkëlqimin e artë të Kiswahut.",
                "Natë e qetë smeraldi, fildishi dhe drite të artë."
        };
        return descriptions[index];
    }

    public static int previewCanvas(int index) {
        int[] colors = {0xff00170f, 0xfffff8f4, 0xfff7f9fb, 0xfff7faf6,
                0xff111317, 0xff081612};
        return colors[index];
    }

    public static int previewSurface(int index) {
        int[] colors = {0xff0d3024, 0xfffff1e4, 0xfff2f4f6, 0xfff1f4f1,
                0xff191c20, 0xff14221e};
        return colors[index];
    }

    public static int previewAccent(int index) {
        int[] colors = {0xffe9c349, 0xff805200, 0xff6b5d3d, 0xff775a19,
                0xfff2ca50, 0xffffd685};
        return colors[index];
    }

    public static int previewText(int index) {
        int[] colors = {0xffc5ebda, 0xff231a0d, 0xff191c1e, 0xff181c1b,
                0xffe2e2e6, 0xffd6e6df};
        return colors[index];
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
