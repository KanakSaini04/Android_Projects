package com.codexcraft.fileflow.di

import android.content.Context
import androidx.room.Room
import com.codexcraft.fileflow.data.local.db.FileFlowDatabase
import com.codexcraft.fileflow.data.local.db.dao.FavoriteFileDao
import com.codexcraft.fileflow.data.local.db.dao.RecentFileDao
import com.codexcraft.fileflow.data.local.db.dao.VaultEntryDao
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
    fun provideFileFlowDatabase(
        @ApplicationContext context: Context
    ): FileFlowDatabase {
        return Room.databaseBuilder(
            context,
            FileFlowDatabase::class.java,
            "fileflow_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideFavoriteFileDao(database: FileFlowDatabase): FavoriteFileDao {
        return database.favoriteFileDao()
    }
    
    @Provides
    fun provideRecentFileDao(database: FileFlowDatabase): RecentFileDao {
        return database.recentFileDao()
    }
    
    @Provides
    fun provideVaultEntryDao(database: FileFlowDatabase): VaultEntryDao {
        return database.vaultEntryDao()
    }
}
