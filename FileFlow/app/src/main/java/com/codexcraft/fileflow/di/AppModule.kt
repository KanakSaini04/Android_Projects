package com.codexcraft.fileflow.di

import android.content.Context
import com.codexcraft.fileflow.data.local.encryption.KeystoreManager
import com.codexcraft.fileflow.data.local.encryption.VaultCryptoManager
import com.codexcraft.fileflow.data.local.storage.RecycleBinManager
import com.codexcraft.fileflow.data.local.storage.SafFileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideSafFileManager(
        @ApplicationContext context: Context
    ): SafFileManager = SafFileManager(context)
    
    @Provides
    @Singleton
    fun provideRecycleBinManager(
        @ApplicationContext context: Context
    ): RecycleBinManager = RecycleBinManager(context)
    
    @Provides
    @Singleton
    fun provideKeystoreManager(): KeystoreManager = KeystoreManager()
    
    @Provides
    @Singleton
    fun provideVaultCryptoManager(
        @ApplicationContext context: Context,
        keystoreManager: KeystoreManager
    ): VaultCryptoManager = VaultCryptoManager(context, keystoreManager)
}
