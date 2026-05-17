package com.codexcraft.fileflow.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codexcraft.fileflow.data.local.db.dao.FavoriteFileDao
import com.codexcraft.fileflow.data.local.db.dao.RecentFileDao
import com.codexcraft.fileflow.data.local.db.dao.VaultEntryDao
import com.codexcraft.fileflow.data.local.db.entities.FavoriteFileEntity
import com.codexcraft.fileflow.data.local.db.entities.RecentFileEntity
import com.codexcraft.fileflow.data.local.db.entities.VaultEntryEntity

@Database(
    entities = [
        FavoriteFileEntity::class,
        RecentFileEntity::class,
        VaultEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FileFlowDatabase : RoomDatabase() {
    abstract fun favoriteFileDao(): FavoriteFileDao
    abstract fun recentFileDao(): RecentFileDao
    abstract fun vaultEntryDao(): VaultEntryDao
}
