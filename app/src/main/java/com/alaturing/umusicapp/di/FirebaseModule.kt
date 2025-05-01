package com.alaturing.umusicapp.di

import com.alaturing.umusicapp.authentication.data.local.localDatasource.PlaylistLocalDatasourceDS
import com.alaturing.umusicapp.authentication.data.local.localDatasource.SongLocalDatasource
import com.alaturing.umusicapp.authentication.data.local.localDatasource.UserLocalDatasource
import com.alaturing.umusicapp.authentication.data.repository.PlaylistRepository
import com.alaturing.umusicapp.authentication.data.repository.SongRepository
import com.alaturing.umusicapp.authentication.data.repository.UserRepository
import com.alaturing.umusicapp.authentication.data.repository.firebase.PlaylistRepositoryFirebase
import com.alaturing.umusicapp.authentication.data.repository.firebase.SongRepositoryFirebase
import com.alaturing.umusicapp.authentication.data.repository.firebase.UserRepositoryFirebase
import com.alaturing.umusicapp.firebase.FirebaseManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseManager(): FirebaseManager {
        return FirebaseManager()
    }

    /**
     * Proporciona la implementación de Firebase para UserRepository
     */
    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseManager: FirebaseManager,
        local: UserLocalDatasource
    ): UserRepository {
        return UserRepositoryFirebase(firebaseManager, local)
    }

    /**
     * Proporciona la implementación de Firebase para SongRepository
     */
    @Provides
    @Singleton
    fun provideSongRepository(
        firebaseManager: FirebaseManager,
        local: SongLocalDatasource
    ): SongRepository {
        return SongRepositoryFirebase(firebaseManager, local)
    }

    /**
     * Proporciona la implementación de Firebase para PlaylistRepository
     */
    @Provides
    @Singleton
    fun providePlaylistRepository(
        firebaseManager: FirebaseManager,
        songRepository: SongRepository,
        local: PlaylistLocalDatasourceDS
    ): PlaylistRepository {
        return PlaylistRepositoryFirebase(firebaseManager, songRepository, local)
    }
}