package com.example.hajjrules.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hajjrules.R;
import com.example.hajjrules.adapters.RuleAdapter;
import com.example.hajjrules.model.HajjRule;
import com.example.hajjrules.util.DataManager;
import com.example.hajjrules.DetailActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RuleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = view.findViewById(R.id.search_view);
        RecyclerView rv = view.findViewById(R.id.recycler_view_search);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RuleAdapter(getContext(), new ArrayList<>());
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(rule -> {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("ruleTitle", rule.getTitle());
            intent.putExtra("ruleDescription", rule.getDescription());
            startActivity(intent);
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateResults(newText);
                return true;
            }

            private void updateResults(String text) {
                List<HajjRule> results = DataManager.getInstance(getContext()).searchRules(text);
                adapter.updateRules(results);
            }
        });
    }
}
