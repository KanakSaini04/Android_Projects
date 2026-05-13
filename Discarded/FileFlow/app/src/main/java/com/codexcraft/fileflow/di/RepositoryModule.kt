package com.codexcraft.fileflow.di

import com.codexcraft.fileflow.data.repository.FileRepositoryImpl
import com.codexcraft.fileflow.data.repository.MetadataRepositoryImpl
import com.codexcraft.fileflow.data.repository.ReaderRepositoryImpl
import com.codexcraft.fileflow.data.repository.ToolsRepositoryImpl
import com.codexcraft.fileflow.data.repository.VaultRepositoryImpl
import com.codexcraft.fileflow.domain.repository.FileRepository
import com.codexcraft.fileflow.domain.repository.MetadataRepository
import com.codexcraft.fileflow.domain.repository.ReaderRepository
import com.codexcraft.fileflow.domain.repository.ToolsRepository
import com.codexcraft.fileflow.domain.repository.VaultRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFileRepository(impl: FileRepositoryImpl): FileRepository

    @Binds
    @Singleton
    abstract fun bindMetadataRepository(impl: MetadataRepositoryImpl): MetadataRepository

    @Binds
    @Singleton
    abstract fun bindReaderRepository(impl: ReaderRepositoryImpl): ReaderRepository

    @Binds
    @Singleton
    abstract fun bindToolsRepository(impl: ToolsRepositoryImpl): ToolsRepository

    @Binds
    @Singleton
    abstract fun bindVaultRepository(impl: VaultRepositoryImpl): VaultRepository
}
