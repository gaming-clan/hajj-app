package com.muslim.hajjrules.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.viewModels;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muslim.hajjrules.R;
import com.muslim.hajjrules.adapters.CategoryAdapter;
import com.muslim.hajjrules.model.Category;
import com.muslim.hajjrules.viewmodel.HomeViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Home Fragment that displays categories using MVVM architecture
 * Uses Hilt for dependency injection and ViewModel for data management
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize ViewModel with Hilt
        homeViewModel = viewModels(this).get(HomeViewModel.class);

        recyclerView = view.findViewById(R.id.recycler_view_categories);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        setupObservers();
        setupRecyclerView();

        return view;
    }

    /**
     * Setup LiveData observers for ViewModel data
     */
    private void setupObservers() {
        // Observe categories data
        homeViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categoryList) {
                if (categoryList != null) {
                    updateCategories(categoryList);
                }
            }
        });

        // Observe loading state
        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                // You can show/hide loading indicator here if needed
                if (isLoading != null && isLoading) {
                    // Show loading state
                } else {
                    // Hide loading state
                }
            }
        });

        // Observe error messages
        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if (errorMessage != null) {
                    // Show error message (e.g., Toast or Snackbar)
                    // Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Setup RecyclerView and adapter
     */
    private void setupRecyclerView() {
        adapter = new CategoryAdapter(getContext(), null); // Pass null initially, will be updated via observer
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(category -> {
            Fragment fragment = CategoryFragment.newInstance(category.getId(), category.getTitle());
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        });
    }

    /**
     * Update adapter with new categories data
     */
    private void updateCategories(List<Category> categoryList) {
        if (adapter != null) {
            adapter.updateCategories(categoryList);
        }
    }
}
