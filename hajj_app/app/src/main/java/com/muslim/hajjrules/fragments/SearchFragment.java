package com.muslim.hajjrules.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;

import com.muslim.hajjrules.R;
import com.muslim.hajjrules.adapters.RuleAdapter;
import com.muslim.hajjrules.model.HajjRule;
import com.muslim.hajjrules.viewmodel.SearchViewModel;
import com.muslim.hajjrules.DetailActivity;

import java.util.Collections;

@dagger.hilt.android.AndroidEntryPoint
public class SearchFragment extends Fragment {
    private RuleAdapter adapter;
    private SearchViewModel searchViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        com.google.android.material.textfield.TextInputEditText searchInput = view.findViewById(R.id.search_input);
        RecyclerView rv = view.findViewById(R.id.recycler_view_search);
        android.widget.TextView emptyState = view.findViewById(R.id.empty_search);
        view.findViewById(R.id.suggestion_ihram).setOnClickListener(v -> searchInput.setText("Ihrami"));
        view.findViewById(R.id.suggestion_miqat).setOnClickListener(v -> searchInput.setText("Miqat"));
        view.findViewById(R.id.suggestion_umrah).setOnClickListener(v -> searchInput.setText("Umre"));
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RuleAdapter(requireContext(), Collections.emptyList());
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(rule -> {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("ruleTitle", rule.getTitle());
            intent.putExtra("ruleDescription", rule.getDescription());
            intent.putExtra("ruleImageId", rule.getImageResourceId());
            startActivity(intent);
        });
        adapter.setOnFavoriteClickListener(searchViewModel::toggleSaved);
        searchViewModel.getResults().observe(getViewLifecycleOwner(), results -> {
            adapter.updateRules(results);
            emptyState.setVisibility(results == null || results.isEmpty() ? View.VISIBLE : View.GONE);
        });
        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchViewModel.setQuery(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) { }
        });
        searchViewModel.setQuery(searchInput.getText() == null ? "" : searchInput.getText().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) getActivity().setTitle(R.string.search);
    }
}
