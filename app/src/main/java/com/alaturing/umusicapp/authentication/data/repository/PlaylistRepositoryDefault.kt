package com.alaturing.umusicapp.authentication.data.repository

import com.alaturing.umusicapp.authentication.data.remote.PlaylistRemoteDatasource
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PlaylistRepositoryDefault @Inject constructor(
    private val remote: PlaylistRemoteDatasource
) : PlaylistRepository {

    override suspend fun readAll(): Result<List<Playlist>> {
        return remote.getAll()
    }

    override suspend fun readById(id: Int): Result<Playlist> {
        return remote.getById(id)
    }

    override fun observeAll(): Flow<Result<List<Playlist>>> = flow {
        val result = remote.getAll()
        emit(result)
    }
    override suspend fun getPlaylistSongs(id: Int): Result<List<Song>> {
        return remote.getPlaylistSongs(id)
    }
}