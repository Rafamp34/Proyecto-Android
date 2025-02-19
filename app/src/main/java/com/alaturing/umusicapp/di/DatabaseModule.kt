package com.alaturing.umusicapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.alaturing.umusicapp.authentication.data.local.AppDatabase
import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.PlaylistLocalDatasourceDS
import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.SongLocalDatasourceDS
import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.UserLocalDatasourceDS
import com.alaturing.umusicapp.authentication.data.local.daos.ArtistDao
import com.alaturing.umusicapp.authentication.data.local.daos.PlaylistDao
import com.alaturing.umusicapp.authentication.data.local.daos.SongDao
import com.alaturing.umusicapp.authentication.data.local.daos.UserDao
import com.google.gson.Gson
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
    fun provideUserLocalDatasource(
        preferences: DataStore<Preferences>
    ) = UserLocalDatasourceDS(preferences)

    @Provides
    @Singleton
    fun provideSongLocalDatasource(
        preferences: DataStore<Preferences>,
        gson: Gson
    ) = SongLocalDatasourceDS(preferences, gson)

    @Provides
    @Singleton
    fun providePlaylistLocalDatasource(
        preferences: DataStore<Preferences>,
        gson: Gson
    ) = PlaylistLocalDatasourceDS(preferences, gson)

    // Room providers
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) =
        AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

    @Provides
    @Singleton
    fun provideSongDao(appDatabase: AppDatabase) = appDatabase.songDao()

    @Provides
    @Singleton
    fun provideArtistDao(appDatabase: AppDatabase) = appDatabase.artistDao()

    @Provides
    @Singleton
    fun providePlaylistDao(appDatabase: AppDatabase) = appDatabase.playlistDao()
}