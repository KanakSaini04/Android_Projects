package com.codexcraft.fileflow.di

import android.content.Context
import androidx.room.Room
import com.codexcraft.fileflow.core.db.FileFlowDatabase
import com.codexcraft.fileflow.core.db.dao.FavoriteDao
import com.codexcraft.fileflow.core.db.dao.RecentDao
import com.codexcraft.fileflow.core.db.dao.RecycleBinDao
import com.codexcraft.fileflow.core.db.dao.VaultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FileFlowDatabase =
        Room.databaseBuilder(context, FileFlowDatabase::class.java, "fileflow.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideRecentDao(db: FileFlowDatabase): RecentDao = db.recentDao()

    @Provides
    fun provideFavoriteDao(db: FileFlowDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun provideRecycleBinDao(db: FileFlowDatabase): RecycleBinDao = db.recycleBinDao()

    @Provides
    fun provideVaultDao(db: FileFlowDatabase): VaultDao = db.vaultDao()
}
