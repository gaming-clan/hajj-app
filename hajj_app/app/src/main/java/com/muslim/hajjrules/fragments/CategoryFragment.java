package com.muslim.hajjrules.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.viewModels;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muslim.hajjrules.R;
import com.muslim.hajjrules.adapters.RuleAdapter;
import com.muslim.hajjrules.model.HajjRule;
import com.muslim.hajjrules.viewmodel.CategoryViewModel;
import com.muslim.hajjrules.DetailActivity;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Category Fragment that displays rules for a specific category using MVVM architecture
 * Uses Hilt for dependency injection and ViewModel for data management
 */
@AndroidEntryPoint
public class CategoryFragment extends Fragment {

    private static final String ARG_CATEGORY_ID = "categoryId";
    private static final String ARG_CATEGORY_TITLE = "categoryTitle";

    private int categoryId;
    private String categoryTitle;
    private CategoryViewModel categoryViewModel;
    private RecyclerView recyclerView;
    private RuleAdapter adapter;

    public static CategoryFragment newInstance(int id, String title) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, id);
        args.putString(ARG_CATEGORY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        // Initialize ViewModel with Hilt
        categoryViewModel = viewModels(this).get(CategoryViewModel.class);

        // Get arguments and set category
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
            categoryTitle = getArguments().getString(ARG_CATEGORY_TITLE);
            getActivity().setTitle(categoryTitle);

            // Set category in ViewModel
            categoryViewModel.setCategory(categoryId, categoryTitle);
        }

        setupRecyclerView(view);
        setupObservers();

        return view;
    }

    /**
     * Setup RecyclerView and adapter
     */
    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RuleAdapter(getContext(), null); // Pass null initially, will be updated via observer
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(rule -> {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("ruleTitle", rule.getTitle());
            intent.putExtra("ruleDescription", rule.getDescription());
            intent.putExtra("ruleImageId", rule.getImageResourceId());
            startActivity(intent);
        });

        // Set favorite click listener if supported by adapter
        adapter.setOnFavoriteClickListener(rule -> {
            categoryViewModel.toggleFavorite(rule);
        });
    }

    /**
     * Setup LiveData observers for ViewModel data
     */
    private void setupObservers() {
        // Observe rules data
        categoryViewModel.getRules().observe(getViewLifecycleOwner(), new Observer<List<HajjRule>>() {
            @Override
            public void onChanged(List<HajjRule> ruleList) {
                if (ruleList != null) {
                    updateRules(ruleList);
                }
            }
        });

        // Observe category title (in case it changes)
        categoryViewModel.getCategoryTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String title) {
                if (title != null && getActivity() != null) {
                    getActivity().setTitle(title);
                }
            }
        });

        // Observe loading state
        categoryViewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
        categoryViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
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
     * Update adapter with new rules data
     */
    private void updateRules(List<HajjRule> ruleList) {
        if (adapter != null) {
            adapter.updateRules(ruleList);
        }
    }
}
