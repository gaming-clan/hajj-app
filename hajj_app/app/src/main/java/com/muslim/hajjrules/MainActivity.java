package com.muslim.hajjrules;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.muslim.hajjrules.fragments.AboutFragment;
import com.muslim.hajjrules.fragments.CategoryFragment;
import com.muslim.hajjrules.fragments.HomeFragment;
import com.muslim.hajjrules.fragments.SearchFragment;
import com.muslim.hajjrules.fragments.MapFragment;
import com.muslim.hajjrules.fragments.SavedFragment;
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
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemActiveIndicatorEnabled(true);
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
