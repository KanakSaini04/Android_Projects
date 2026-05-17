package com.codexcraft.fileflow.data.remote

import com.codexcraft.fileflow.data.local.db.dao.FavoriteFileDao
import com.codexcraft.fileflow.data.local.db.entities.FavoriteFileEntity
import com.codexcraft.fileflow.data.remote.dto.FavoriteDto
import com.codexcraft.fileflow.data.remote.dto.UserSyncData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSyncService @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val favoriteFileDao: FavoriteFileDao
) {

    suspend fun syncFavorites(): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("User not authenticated"))
        
        return try {
            val localFavorites = favoriteFileDao.getAllFavorites().first()
            val syncData = UserSyncData(
                userId = user.uid,
                favorites = localFavorites.map { it.toDto() },
                lastSyncTimestamp = System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(user.uid)
                .set(syncData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreFavorites(): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("User not authenticated"))

        return try {
            val document = firestore.collection("users")
                .document(user.uid)
                .get()
                .await()

            val syncData = document.toObject(UserSyncData::class.java)
            syncData?.favorites?.forEach { dto ->
                favoriteFileDao.addFavorite(dto.toEntity())
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun FavoriteFileEntity.toDto() = FavoriteDto(
        uri = uri,
        name = name,
        mimeType = mimeType,
        addedAt = addedAt
    )

    private fun FavoriteDto.toEntity() = FavoriteFileEntity(
        uri = uri,
        name = name,
        mimeType = mimeType,
        addedAt = addedAt
    )
}
