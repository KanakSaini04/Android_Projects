package com.codexcraft.lensora.di

import android.content.Context
import com.codexcraft.lensora.data.repository.AuthRepository
import com.codexcraft.lensora.data.repository.MediaRepository
import com.codexcraft.lensora.data.repository.SettingsRepository
import com.codexcraft.lensora.domain.usecase.AnalyzeSceneUseCase
import com.codexcraft.lensora.domain.usecase.GetMediaVaultUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LensoraModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context
    ): AuthRepository = AuthRepository(context)

    @Provides
    @Singleton
    fun provideMediaRepository(
        @ApplicationContext context: Context
    ): MediaRepository = MediaRepository(context)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository = SettingsRepository(context)

    @Provides
    fun provideAnalyzeSceneUseCase(): AnalyzeSceneUseCase = AnalyzeSceneUseCase()

    @Provides
    fun provideGetMediaVaultUseCase(
        mediaRepository: MediaRepository
    ): GetMediaVaultUseCase = GetMediaVaultUseCase(mediaRepository)
}