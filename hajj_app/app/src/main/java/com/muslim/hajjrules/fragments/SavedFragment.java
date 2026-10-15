package com.muslim.hajjrules.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.muslim.hajjrules.DetailActivity;
import com.muslim.hajjrules.R;
import com.muslim.hajjrules.adapters.RuleAdapter;
import com.muslim.hajjrules.model.HajjRule;
import com.muslim.hajjrules.viewmodel.SavedViewModel;

import java.util.Collections;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SavedFragment extends Fragment {
    private SavedViewModel viewModel;
    private RuleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(SavedViewModel.class);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_saved);
        TextView emptyState = view.findViewById(R.id.empty_saved);
        android.util.TypedValue iconColor = new android.util.TypedValue();
        requireContext().getTheme().resolveAttribute(
                com.google.android.material.R.attr.colorPrimaryVariant, iconColor, true);
        android.graphics.drawable.Drawable[] emptyIcons = emptyState.getCompoundDrawablesRelative();
        if (emptyIcons[0] != null) {
            android.graphics.drawable.Drawable icon = androidx.core.graphics.drawable.DrawableCompat
                    .wrap(emptyIcons[0].mutate());
            androidx.core.graphics.drawable.DrawableCompat.setTint(icon, iconColor.data);
            emptyState.setCompoundDrawablesRelative(icon, emptyIcons[1], emptyIcons[2], emptyIcons[3]);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RuleAdapter(requireContext(), Collections.emptyList());
        adapter.setOnFavoriteClickListener(viewModel::toggleSaved);
        adapter.setOnItemClickListener(this::openRule);
        recyclerView.setAdapter(adapter);
        viewModel.getSavedRules().observe(getViewLifecycleOwner(), rules -> {
            adapter.updateRules(rules);
            boolean isEmpty = rules == null || rules.isEmpty();
            emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });
    }

    private void openRule(HajjRule rule) {
        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra("ruleTitle", rule.getTitle());
        intent.putExtra("ruleDescription", rule.getDescription());
        intent.putExtra("ruleImageId", rule.getImageResourceId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) getActivity().setTitle(R.string.saved);
    }
}
