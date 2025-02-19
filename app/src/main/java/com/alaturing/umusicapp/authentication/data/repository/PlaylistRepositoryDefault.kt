package com.alaturing.umusicapp.authentication.data.repository

import android.net.Uri
import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.PlaylistLocalDatasourceDS
import com.alaturing.umusicapp.authentication.data.remote.PlaylistRemoteDatasource
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PlaylistRepositoryDefault @Inject constructor(
    private val remote: PlaylistRemoteDatasource,
    private val local: PlaylistLocalDatasourceDS
) : PlaylistRepository {

    override suspend fun readAll(): Result<List<Playlist>> {
        val localPlaylists = local.getPlaylists()

        try {
            val remoteResult = remote.getAll()
            if (remoteResult.isSuccess) {
                val remotePlaylists = remoteResult.getOrNull()!!
                local.savePlaylists(remotePlaylists)
                return Result.success(remotePlaylists)
            }
        } catch (e: Exception) {
        }

        return Result.success(localPlaylists)
    }

    override suspend fun readById(id: Int): Result<Playlist> {
        val localPlaylists = local.getPlaylists()
        val localPlaylist = localPlaylists.find { it.id == id }?.copy(
            songs = local.getPlaylistSongs(id)
        )

        try {
            val remoteResult = remote.getById(id)
            if (remoteResult.isSuccess) {
                val playlist = remoteResult.getOrNull()!!
                local.savePlaylistSongs(playlist.id, playlist.songs)
                return Result.success(playlist)
            }
        } catch (e: Exception) {
        }

        return if (localPlaylist != null) {
            Result.success(localPlaylist)
        } else {
            Result.failure(Exception("Playlist no encontrada"))
        }
    }

    override fun observeAll(): Flow<Result<List<Playlist>>> = flow {
        emit(Result.success(local.getPlaylists()))

        try {
            val remoteResult = remote.getAll()
            if (remoteResult.isSuccess) {
                val playlists = remoteResult.getOrNull()!!
                local.savePlaylists(playlists)
                emit(Result.success(playlists))
            }
        } catch (e: Exception) {
        }
    }

    override suspend fun getPlaylistSongs(id: Int): Result<List<Song>> {
        val localSongs = local.getPlaylistSongs(id)

        try {
            val remoteResult = remote.getPlaylistSongs(id)
            if (remoteResult.isSuccess) {
                val songs = remoteResult.getOrNull()!!
                local.savePlaylistSongs(id, songs)
                return Result.success(songs)
            }
        } catch (e: Exception) {
        }

        return Result.success(localSongs)
    }

    // Operaciones que requieren conexión
    override suspend fun addSongToPlaylist(playlistId: Int, songId: Int): Result<Unit> {
        val result = remote.addSongToPlaylist(playlistId, songId)
        if (result.isSuccess) {
            readById(playlistId)
        }
        return result
    }

    override suspend fun removeSongFromPlaylist(playlistId: Int, songId: Int): Result<Unit> {
        val result = remote.removeSongFromPlaylist(playlistId, songId)
        if (result.isSuccess) {
            readById(playlistId)
        }
        return result
    }

    override suspend fun createPlaylist(name: String, author: String, imageId: Int?): Result<Playlist> {
        val result = remote.createPlaylist(name, author, imageId)
        if (result.isSuccess) {
            readAll()
        }
        return result
    }

    override suspend fun uploadImage(uri: Uri): Result<Int> {
        return remote.uploadImage(uri)
    }

    override suspend fun deletePlaylist(id: Int): Result<Unit> {
        val result = remote.deletePlaylist(id)
        if (result.isSuccess) {
            local.deletePlaylist(id)
        }
        return result
    }
}