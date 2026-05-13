package com.codexcraft.fileflow.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codexcraft.fileflow.core.db.dao.FavoriteDao
import com.codexcraft.fileflow.core.db.dao.RecentDao
import com.codexcraft.fileflow.core.db.dao.RecycleBinDao
import com.codexcraft.fileflow.core.db.dao.VaultDao
import com.codexcraft.fileflow.core.db.entity.FavoriteEntity
import com.codexcraft.fileflow.core.db.entity.RecentEntity
import com.codexcraft.fileflow.core.db.entity.RecycleBinEntity
import com.codexcraft.fileflow.core.db.entity.VaultEntity

@Database(
    entities = [RecentEntity::class, FavoriteEntity::class, RecycleBinEntity::class, VaultEntity::class],
    version = 2,
    exportSchema = false
)
abstract class FileFlowDatabase : RoomDatabase() {
    abstract fun recentDao(): RecentDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recycleBinDao(): RecycleBinDao
    abstract fun vaultDao(): VaultDao
}
