package com.alaturing.umusicapp.di

import com.alaturing.umusicapp.authentication.data.repository.*
import com.alaturing.umusicapp.authentication.data.local.UserLocalDatasource
import com.alaturing.umusicapp.authentication.data.local.UserLocalDatasourceDS
import com.alaturing.umusicapp.authentication.data.remote.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(r: UserRepositoryDefault): UserRepository

    @Binds
    @Singleton
    abstract fun bindSongRepository(r: SongRepositoryDefault): SongRepository

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(r: PlaylistRepositoryDefault): PlaylistRepository

    @Binds
    @Singleton
    abstract fun bindUserRemoteDatasource(ds: UserRemoteDatasourceStrapi): UserRemoteDatasource

    @Binds
    @Singleton
    abstract fun bindSongRemoteDatasource(ds: SongRemoteDatasourceStrapi): SongRemoteDatasource

    @Binds
    @Singleton
    abstract fun bindUserDatasourceLocal(ds: UserLocalDatasourceDS): UserLocalDatasource
}