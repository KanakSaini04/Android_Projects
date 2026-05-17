package com.codexcraft.fileflow.di

import com.codexcraft.fileflow.core.security.CryptoManager
import com.codexcraft.fileflow.core.security.CryptoManagerImpl
import com.codexcraft.fileflow.core.security.KeystoreManager
import com.codexcraft.fileflow.core.security.KeystoreManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindKeystoreManager(impl: KeystoreManagerImpl): KeystoreManager

    @Binds
    @Singleton
    abstract fun bindCryptoManager(impl: CryptoManagerImpl): CryptoManager
}