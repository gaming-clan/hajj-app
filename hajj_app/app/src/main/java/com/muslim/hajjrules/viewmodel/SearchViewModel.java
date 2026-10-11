package com.muslim.hajjrules.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.muslim.hajjrules.data.repository.HajjRepositoryInterface;
import com.muslim.hajjrules.model.HajjRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SearchViewModel extends ViewModel {
    private final HajjRepositoryInterface repository;
    private final LiveData<List<HajjRule>> allRules;
    private final MediatorLiveData<List<HajjRule>> results = new MediatorLiveData<>();
    private String query = "";

    @Inject
    public SearchViewModel(HajjRepositoryInterface repository) {
        this.repository = repository;
        this.allRules = repository.getAllRules();
        results.addSource(allRules, this::filterRules);
    }

    public LiveData<List<HajjRule>> getResults() {
        return results;
    }

    public void setQuery(String query) {
        this.query = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<HajjRule> current = allRules.getValue();
        if (current != null) filterRules(current);
    }

    public void toggleSaved(HajjRule rule) {
        rule.setFavorite(!rule.isFavorite());
        repository.update(rule);
    }

    private void filterRules(List<HajjRule> rules) {
        if (rules == null) {
            results.setValue(new ArrayList<>());
            return;
        }
        if (query.isEmpty()) {
            results.setValue(rules);
            return;
        }
        List<HajjRule> filtered = new ArrayList<>();
        for (HajjRule rule : rules) {
            String title = rule.getTitle() == null ? "" : rule.getTitle().toLowerCase(Locale.ROOT);
            String description = rule.getDescription() == null ? "" : rule.getDescription().toLowerCase(Locale.ROOT);
            if (title.contains(query) || description.contains(query)) filtered.add(rule);
        }
        results.setValue(filtered);
    }
}
