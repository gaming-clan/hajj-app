package com.muslim.hajjrules.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.muslim.hajjrules.R;
import com.muslim.hajjrules.util.ThemeManager;

public class AboutFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) getActivity().setTitle(R.string.about);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        String[] themeIds = ThemeManager.ids();
        String selected = requireContext().getSharedPreferences(
                ThemeManager.PREFERENCES, android.content.Context.MODE_PRIVATE)
                .getString(ThemeManager.SELECTED_THEME, ThemeManager.DEFAULT_THEME);
        String[] themeNames = getResources().getStringArray(R.array.app_theme_names);
        LinearLayout gallery = view.findViewById(R.id.theme_gallery);
        for (int index = 0; index < themeIds.length; index++) {
            gallery.addView(createThemeCard(index, themeIds, themeNames, selected));
        }
        int selectedIndex = 1;
        for (int i = 0; i < themeIds.length; i++) if (themeIds[i].equals(selected)) selectedIndex = i;
        TextView note = view.findViewById(R.id.selected_theme_note);
        note.setText(ThemeManager.description(selectedIndex));
        android.graphics.drawable.Drawable[] icons = note.getCompoundDrawablesRelative();
        if (icons[0] != null) {
            android.graphics.drawable.Drawable icon = androidx.core.graphics.drawable.DrawableCompat.wrap(icons[0].mutate());
            androidx.core.graphics.drawable.DrawableCompat.setTint(icon,
                    ThemeManager.previewAccent(selectedIndex));
            note.setCompoundDrawablesRelative(icon, icons[1], icons[2], icons[3]);
        }
        return view;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private View createThemeCard(int index, String[] ids, String[] names, String selected) {
        int canvas = ThemeManager.previewCanvas(index);
        int surface = ThemeManager.previewSurface(index);
        int accent = ThemeManager.previewAccent(index);
        int textColor = ThemeManager.previewText(index);
        boolean active = ids[index].equals(selected);

        MaterialCardView card = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(dp(196), dp(184));
        cardParams.setMarginEnd(dp(12));
        card.setLayoutParams(cardParams);
        card.setRadius(dp(17));
        card.setCardBackgroundColor(canvas);
        card.setCardElevation(active ? dp(3) : dp(1));
        card.setStrokeWidth(dp(active ? 2 : 1));
        card.setStrokeColor(active ? accent : ThemeManager.previewSurface(index));
        card.setCheckable(true);
        card.setChecked(active);
        card.setClickable(true);
        card.setFocusable(true);
        card.setContentDescription(names[index] + (active ? ", selected theme" : ", select theme"));

        LinearLayout content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12), dp(12), dp(12), dp(10));

        LinearLayout swatches = new LinearLayout(requireContext());
        swatches.setGravity(android.view.Gravity.CENTER_VERTICAL);
        swatches.setPadding(dp(12), 0, dp(12), 0);
        swatches.setBackground(roundDrawable(surface, dp(11)));
        LinearLayout.LayoutParams swatchParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(42));
        content.addView(swatches, swatchParams);
        int[] colors = {canvas, surface, accent};
        for (int color : colors) {
            View dot = new View(requireContext());
            dot.setBackground(roundDrawable(color, dp(50)));
            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dp(18), dp(18));
            dotParams.setMarginEnd(dp(9));
            swatches.addView(dot, dotParams);
        }
        View accentLine = new View(requireContext());
        accentLine.setBackground(roundDrawable(accent, dp(6)));
        swatches.addView(accentLine, new LinearLayout.LayoutParams(0, dp(5), 1));

        TextView title = new TextView(requireContext());
        title.setText(names[index]);
        title.setTextColor(textColor);
        title.setTextSize(14);
        title.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        title.setMaxLines(1);
        title.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.topMargin = dp(12);
        content.addView(title, titleParams);

        TextView description = new TextView(requireContext());
        description.setText(ThemeManager.description(index));
        description.setTextColor(textColor);
        description.setAlpha(0.78f);
        description.setTextSize(11);
        description.setMaxLines(2);
        description.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        descriptionParams.topMargin = dp(4);
        content.addView(description, descriptionParams);

        TextView state = new TextView(requireContext());
        state.setText(active ? "✓  STILI AKTUAL" : "PREKNI PËR TA ZGJEDHUR");
        state.setTextColor(active ? accent : textColor);
        state.setTextSize(9);
        state.setLetterSpacing(0.06f);
        state.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD);
        content.addView(state, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        card.addView(content);
        card.setOnClickListener(v -> {
            if (ids[index].equals(selected)) return;
            requireContext().getSharedPreferences(ThemeManager.PREFERENCES,
                    android.content.Context.MODE_PRIVATE).edit()
                    .putString(ThemeManager.SELECTED_THEME, ids[index]).apply();
            requireActivity().recreate();
        });
        return card;
    }

    private GradientDrawable roundDrawable(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }
}
