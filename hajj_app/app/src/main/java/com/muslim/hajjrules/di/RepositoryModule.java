package com.muslim.hajjrules.di;

import com.muslim.hajjrules.data.repository.HajjRepositoryImpl;
import com.muslim.hajjrules.data.repository.HajjRepositoryInterface;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * Dagger Hilt module for repository dependencies
 * Binds repository implementations to their interfaces
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    @Singleton
    public abstract HajjRepositoryInterface bindHajjRepository(HajjRepositoryImpl hajjRepository);
}