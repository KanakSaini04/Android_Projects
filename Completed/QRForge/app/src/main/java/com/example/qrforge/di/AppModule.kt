package com.example.qrforge.di

import android.content.Context
import androidx.room.Room
import com.example.qrforge.data.local.QRForgeDatabase
import com.example.qrforge.data.local.ScanHistoryDao
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
    fun provideDatabase(@ApplicationContext ctx: Context): QRForgeDatabase =
        Room.databaseBuilder(
            ctx,
            QRForgeDatabase::class.java,
            QRForgeDatabase.DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideDao(db: QRForgeDatabase): ScanHistoryDao = db.scanHistoryDao()
}