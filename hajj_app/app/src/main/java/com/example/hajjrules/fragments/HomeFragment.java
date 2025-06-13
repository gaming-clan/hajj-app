package com.example.hajjrules.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hajjrules.R;
import com.example.hajjrules.adapters.CategoryAdapter;
import com.example.hajjrules.model.Category;
import com.example.hajjrules.util.DataManager;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        recyclerView = view.findViewById(R.id.recycler_view_categories);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        loadCategories();
        
        return view;
    }
    
    private void loadCategories() {
        categories = DataManager.getInstance(getContext()).getCategories();
        adapter = new CategoryAdapter(getContext(), categories);
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
}
