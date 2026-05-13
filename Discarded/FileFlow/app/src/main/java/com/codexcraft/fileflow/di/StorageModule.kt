package com.codexcraft.fileflow.di

import android.content.ContentResolver
import android.content.Context
import com.codexcraft.fileflow.data.source.SafStorageDataSource
import com.codexcraft.fileflow.data.source.SafStorageDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolver

    @Provides
    @Singleton
    fun provideSafStorageDataSource(
        @ApplicationContext context: Context,
        contentResolver: ContentResolver
    ): SafStorageDataSource = SafStorageDataSourceImpl(context, contentResolver)
}
