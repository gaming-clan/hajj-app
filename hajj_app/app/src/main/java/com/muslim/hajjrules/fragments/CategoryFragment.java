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

import com.muslim.hajjrules.R;
import com.muslim.hajjrules.adapters.RuleAdapter;
import com.muslim.hajjrules.model.HajjRule;
import com.muslim.hajjrules.util.DataManager;
import com.muslim.hajjrules.DetailActivity;

import java.util.List;

public class CategoryFragment extends Fragment {

    private static final String ARG_CATEGORY_ID = "categoryId";
    private static final String ARG_CATEGORY_TITLE = "categoryTitle";

    private int categoryId;
    private String categoryTitle;

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
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
            categoryTitle = getArguments().getString(ARG_CATEGORY_TITLE);
            getActivity().setTitle(categoryTitle);
        }

        RecyclerView rv = view.findViewById(R.id.recycler_view_items);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        List<HajjRule> rules = DataManager.getInstance(getContext()).getRulesByCategory(categoryId);
        RuleAdapter adapter = new RuleAdapter(getContext(), rules);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(rule -> {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("ruleTitle", rule.getTitle());
            intent.putExtra("ruleDescription", rule.getDescription());
            intent.putExtra("ruleImageId", rule.getImageResourceId());
            startActivity(intent);
        });
        return view;
    }
}
