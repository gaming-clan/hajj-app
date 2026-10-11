package com.muslim.hajjrules.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.TextView;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
        Spinner selector = view.findViewById(R.id.theme_selector);
        TypedValue selectorTint = new TypedValue();
        requireContext().getTheme().resolveAttribute(
                com.google.android.material.R.attr.colorOnSurface, selectorTint, true);
        selector.setBackgroundTintList(ColorStateList.valueOf(selectorTint.data));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), 0,
                getResources().getStringArray(R.array.app_theme_names)) {
            private TextView optionView(int position, View convertView, boolean dropdown) {
                TextView option = convertView instanceof TextView
                        ? (TextView) convertView : new TextView(requireContext());
                TypedValue textColor = new TypedValue();
                TypedValue surfaceColor = new TypedValue();
                requireContext().getTheme().resolveAttribute(
                        com.google.android.material.R.attr.colorOnSurface, textColor, true);
                requireContext().getTheme().resolveAttribute(
                        com.google.android.material.R.attr.colorSurface, surfaceColor, true);
                option.setText(getItem(position));
                option.setTextColor(textColor.data);
                option.setBackgroundColor(dropdown ? surfaceColor.data : Color.TRANSPARENT);
                int horizontal = Math.round(16 * getResources().getDisplayMetrics().density);
                int vertical = Math.round(12 * getResources().getDisplayMetrics().density);
                option.setPadding(horizontal, vertical, horizontal, vertical);
                option.setTextSize(16);
                return option;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return optionView(position, convertView, false);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return optionView(position, convertView, true);
            }
        };
        selector.setAdapter(adapter);

        String[] themeIds = ThemeManager.ids();
        String selected = requireContext().getSharedPreferences(
                ThemeManager.PREFERENCES, android.content.Context.MODE_PRIVATE)
                .getString(ThemeManager.SELECTED_THEME, ThemeManager.DEFAULT_THEME);
        int selectedIndex = 0;
        for (int i = 0; i < themeIds.length; i++) {
            if (themeIds[i].equals(selected)) selectedIndex = i;
        }
        selector.setSelection(selectedIndex, false);
        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View item, int position, long id) {
                if (position >= themeIds.length || themeIds[position].equals(selected)) return;
                requireContext().getSharedPreferences(ThemeManager.PREFERENCES,
                        android.content.Context.MODE_PRIVATE).edit()
                        .putString(ThemeManager.SELECTED_THEME, themeIds[position]).apply();
                requireActivity().recreate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        return view;
    }
}
