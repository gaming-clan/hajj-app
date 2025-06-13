package com.example.hajjrules;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.hajjrules.fragments.AboutFragment;
import com.example.hajjrules.fragments.CategoryFragment;
import com.example.hajjrules.fragments.HomeFragment;
import com.example.hajjrules.fragments.SearchFragment;
import com.example.hajjrules.fragments.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Load default fragment
        loadFragment(new HomeFragment());
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
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.navigation_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.navigation_categories) {
            fragment = new HomeFragment();
        } else if (id == R.id.navigation_search) {
            fragment = new SearchFragment();
        } else if (id == R.id.navigation_about) {
            fragment = new AboutFragment();
        } else if (id == R.id.navigation_map) {
            fragment = new MapFragment();
        }

        return loadFragment(fragment);
    }
}
