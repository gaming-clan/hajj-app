package com.muslim.hajjrules;

import android.os.Bundle;
import android.view.MenuItem;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.muslim.hajjrules.fragments.AboutFragment;
import com.muslim.hajjrules.fragments.CategoryFragment;
import com.muslim.hajjrules.fragments.HomeFragment;
import com.muslim.hajjrules.fragments.SearchFragment;
import com.muslim.hajjrules.fragments.MapFragment;
import com.muslim.hajjrules.fragments.SavedFragment;
import com.muslim.hajjrules.util.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Main Activity with Hilt support for dependency injection
 * Manages bottom navigation and fragment transactions
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private int selectedDestination = R.id.navigation_home;
    private static final String STATE_SELECTED_DESTINATION = "selectedDestination";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeManager.styleFor(this));
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemActiveIndicatorEnabled(true);
        applyNavigationTheme();
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                setTitle(titleForDestination(selectedDestination));
            }
        });

        selectedDestination = savedInstanceState == null
                ? R.id.navigation_home
                : savedInstanceState.getInt(STATE_SELECTED_DESTINATION, R.id.navigation_home);
        bottomNavigationView.setSelectedItemId(selectedDestination);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        setTitle(titleForDestination(selectedDestination));
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            loadFragment(fragmentForDestination(selectedDestination));
        }
    }

    private int themeColor(int attribute) {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(attribute, value, true);
        return value.data;
    }

    private void applyNavigationTheme() {
        int primary = themeColor(com.google.android.material.R.attr.colorPrimaryVariant);
        int muted = themeColor(R.attr.colorNavigationUnselected);
        int surface = themeColor(R.attr.colorNavigationSurface);
        int outline = themeColor(R.attr.colorOutline);
        int active = themeColor(R.attr.colorNavigationActive);
        ColorStateList tint = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{primary, muted});
        bottomNavigationView.setItemIconTintList(tint);
        bottomNavigationView.setItemTextColor(tint);
        bottomNavigationView.setItemRippleColor(ColorStateList.valueOf(active));
        GradientDrawable pill = new GradientDrawable();
        pill.setColor(surface);
        pill.setCornerRadius(getResources().getDisplayMetrics().density * 28);
        pill.setStroke(Math.round(getResources().getDisplayMetrics().density), outline);
        bottomNavigationView.setBackground(pill);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(STATE_SELECTED_DESTINATION, selectedDestination);
        super.onSaveInstanceState(outState);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int destination = item.getItemId();
        Fragment fragment = fragmentForDestination(destination);
        if (fragment == null) return false;
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate(null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        selectedDestination = destination;
        setTitle(titleForDestination(destination));
        return loadFragment(fragment);
    }

    private Fragment fragmentForDestination(int destination) {
        if (destination == R.id.navigation_home) return new HomeFragment();
        if (destination == R.id.navigation_search) return new SearchFragment();
        if (destination == R.id.navigation_saved) return new SavedFragment();
        if (destination == R.id.navigation_map) return new MapFragment();
        if (destination == R.id.navigation_about) return new AboutFragment();
        return null;
    }

    private int titleForDestination(int destination) {
        if (destination == R.id.navigation_home) return R.string.guide;
        if (destination == R.id.navigation_search) return R.string.search;
        if (destination == R.id.navigation_saved) return R.string.saved;
        if (destination == R.id.navigation_map) return R.string.map;
        return R.string.about;
    }
}
