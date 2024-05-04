package com.tejasdev.bunkbuddy.di

import android.content.Context
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.tejasdev.bunkbuddy.util.AppReviewHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppReviewModule {

    @Provides
    @Singleton
    fun provideReviewHelper(
        @ApplicationContext context: Context
    ): AppReviewHelper{
        return AppReviewHelper(context)
    }

    @Provides
    @Singleton
    fun provideReviewManager(
        @ApplicationContext context: Context
    ): ReviewManager{
        return ReviewManagerFactory.create(context)
    }
}