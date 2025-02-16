package com.alaturing.umusicapp.di

import android.content.Context
import com.alaturing.umusicapp.authentication.data.local.AppDatabase
import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.PlaylistLocalDatasource
import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.SongLocalDatasource
import com.alaturing.umusicapp.authentication.data.local.daos.ArtistDao
import com.alaturing.umusicapp.authentication.data.local.daos.PlaylistDao
import com.alaturing.umusicapp.authentication.data.local.daos.SongDao
import com.alaturing.umusicapp.authentication.data.local.daos.UserDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideSongDao(appDatabase: AppDatabase): SongDao {
        return appDatabase.songDao()
    }

    @Provides
    @Singleton
    fun provideArtistDao(appDatabase: AppDatabase): ArtistDao {
        return appDatabase.artistDao()
    }

    @Provides
    @Singleton
    fun providePlaylistDao(appDatabase: AppDatabase): PlaylistDao {
        return appDatabase.playlistDao()
    }

    @Provides
    @Singleton
    fun provideSongLocalDatasource(
        songDao: SongDao,
        artistDao: ArtistDao
    ): SongLocalDatasource {
        return SongLocalDatasource(songDao, artistDao)
    }

    @Provides
    @Singleton
    fun providePlaylistLocalDatasource(
        playlistDao: PlaylistDao
    ): PlaylistLocalDatasource {
        return PlaylistLocalDatasource(playlistDao)
    }

    @Provides
    @Singleton
    fun provideNetworkUtils(@ApplicationContext context: Context): NetworkUtils {
        return NetworkUtils(context)
    }
}