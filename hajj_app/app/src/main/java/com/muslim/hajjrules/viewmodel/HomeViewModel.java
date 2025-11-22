package com.muslim.hajjrules.viewmodel;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.Application;

import com.muslim.hajjrules.data.repository.HajjRepositoryInterface;
import com.muslim.hajjrules.model.Category;
import com.muslim.hajjrules.model.HajjRule;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for HomeFragment that manages categories data
 * Uses Hilt for dependency injection
 */
@HiltViewModel
public class HomeViewModel extends AndroidViewModel {

    private final HajjRepositoryInterface repository;
    private final ExecutorService executorService;

    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public HomeViewModel(Application application, HajjRepositoryInterface repository) {
        super(application);
        this.repository = repository;
        this.executorService = Executors.newSingleThreadExecutor();
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
        isLoading.setValue(true);
        errorMessage.setValue(null);

        executorService.execute(() -> {
            try {
                List<Category> categoryList = repository.getAllCategories();

                // Post categories to main thread
                categories.postValue(categoryList);
                isLoading.postValue(false);

            } catch (Exception e) {
                // Post error to main thread
                errorMessage.postValue("Error loading categories: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
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
        executorService.shutdown();
    }
}