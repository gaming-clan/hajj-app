package com.muslim.hajjrules.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.app.Application;

import com.muslim.hajjrules.data.repository.HajjRepositoryInterface;
import com.muslim.hajjrules.model.Category;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for HomeFragment that manages categories data
 * Uses Hilt for dependency injection
 */
@HiltViewModel
public class HomeViewModel extends AndroidViewModel {

    private final HajjRepositoryInterface repository;
    private final LiveData<List<Category>> categories;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public HomeViewModel(Application application, HajjRepositoryInterface repository) {
        super(application);
        this.repository = repository;
        this.categories = repository.getAllCategories();
        loadCategories();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Load all categories from repository
     */
    public void loadCategories() {
        errorMessage.setValue(null);
        isLoading.setValue(false);
    }

    /**
     * Refresh categories data
     */
    public void refreshCategories() {
        loadCategories();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
