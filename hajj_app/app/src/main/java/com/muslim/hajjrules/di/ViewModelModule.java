package com.muslim.hajjrules.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.muslim.hajjrules.viewmodel.CategoryViewModel;
import com.muslim.hajjrules.viewmodel.HomeViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.ClassKey;

/**
 * Dagger Hilt module for ViewModel dependencies
 * Provides ViewModel instances with proper dependency injection
 */
@Module
public abstract class ViewModelModule {

    /**
     * Binds HomeViewModel to the ViewModelProvider.Factory
     * This ensures HomeViewModel can be created with Hilt dependencies
     */
    @Binds
    @IntoMap
    @ClassKey(HomeViewModel.class)
    abstract ViewModel bindHomeViewModel(HomeViewModel homeViewModel);

    /**
     * Binds CategoryViewModel to the ViewModelProvider.Factory
     * This ensures CategoryViewModel can be created with Hilt dependencies
     */
    @Binds
    @IntoMap
    @ClassKey(CategoryViewModel.class)
    abstract ViewModel bindCategoryViewModel(CategoryViewModel categoryViewModel);

    /**
     * Provides ViewModelProvider.Factory for creating ViewModels with Hilt
     */
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(HiltViewModelFactory factory);
}