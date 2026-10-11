package com.muslim.hajjrules.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.muslim.hajjrules.data.repository.HajjRepositoryInterface;
import com.muslim.hajjrules.model.HajjRule;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SavedViewModel extends ViewModel {
    private final HajjRepositoryInterface repository;

    @Inject
    public SavedViewModel(HajjRepositoryInterface repository) {
        this.repository = repository;
    }

    public LiveData<List<HajjRule>> getSavedRules() {
        return repository.getFavoriteRules();
    }

    public void toggleSaved(HajjRule rule) {
        rule.setFavorite(!rule.isFavorite());
        repository.update(rule);
    }
}
