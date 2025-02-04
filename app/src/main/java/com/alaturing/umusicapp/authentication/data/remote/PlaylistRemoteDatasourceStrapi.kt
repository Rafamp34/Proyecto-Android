package com.alaturing.umusicapp.authentication.data.remote

import android.util.Log
import com.alaturing.umusicapp.authentication.data.remote.model.toModel
import com.alaturing.umusicapp.common.remote.PlaylistUpdateBody
import com.alaturing.umusicapp.common.remote.PlaylistUpdateData
import com.alaturing.umusicapp.common.remote.StrapiApi
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song
import javax.inject.Inject

class PlaylistRemoteDatasourceStrapi @Inject constructor(
    private val api: StrapiApi
) : PlaylistRemoteDatasource {

    override suspend fun getAll(): Result<List<Playlist>> {
        val response = api.getPlaylists()
        return if (response.isSuccessful) {
            Result.success(response.body()!!.data.map { it.toModel() })
        } else {
            Result.failure(RuntimeException())
        }
    }

    override suspend fun getById(id: Int): Result<Playlist> {
        val response = api.getPlaylistById(id)
        return if (response.isSuccessful) {
            Result.success(response.body()!!.data.toModel())
        } else {
            Result.failure(RuntimeException())
        }
    }

    override suspend fun getPlaylistSongs(id: Int): Result<List<Song>> {
        try {
            val response = api.getPlaylistSongs(id)
            Log.d("PlaylistRemoteDS", "Response: ${response.isSuccessful}, Code: ${response.code()}")

            return if (response.isSuccessful && response.body() != null) {
                val songs = response.body()!!.data.map { it.toModel() }
                Log.d("PlaylistRemoteDS", "Songs loaded: ${songs.size}")
                Result.success(songs)
            } else {
                Log.e("PlaylistRemoteDS", "Error body: ${response.errorBody()?.string()}")
                Result.failure(RuntimeException("Error loading songs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PlaylistRemoteDS", "Exception loading songs", e)
            return Result.failure(e)
        }
    }

    override suspend fun removeSongFromPlaylist(playlistId: Int, songId: Int): Result<Unit> {
        try {
            // Primero obtenemos la playlist actual
            val playlistResult = getById(playlistId)
            if (playlistResult.isFailure) {
                return Result.failure(playlistResult.exceptionOrNull() ?: RuntimeException())
            }

            val currentPlaylist = playlistResult.getOrNull()!!
            // Filtramos la canción a eliminar
            val updatedSongIds = currentPlaylist.songs.map { it.id }.filter { it != songId }

            val response = api.updatePlaylist(
                playlistId,
                PlaylistUpdateBody(PlaylistUpdateData(updatedSongIds))
            )

            return if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Log.e("PlaylistRemoteDS", "Error removing song: ${response.errorBody()?.string()}")
                Result.failure(RuntimeException("Error removing song: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PlaylistRemoteDS", "Exception removing song", e)
            return Result.failure(e)
        }
    }

    override suspend fun addSongToPlaylist(playlistId: Int, songId: Int): Result<Unit> {
        try {
            val playlistResult = getById(playlistId)
            if (playlistResult.isFailure) {
                return Result.failure(playlistResult.exceptionOrNull() ?: RuntimeException())
            }

            val currentPlaylist = playlistResult.getOrNull()!!
            // Añadimos la nueva canción a la lista
            val updatedSongIds = currentPlaylist.songs.map { it.id } + songId

            val response = api.updatePlaylist(
                playlistId,
                PlaylistUpdateBody(PlaylistUpdateData(updatedSongIds))
            )

            return if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Log.e("PlaylistRemoteDS", "Error adding song: ${response.errorBody()?.string()}")
                Result.failure(RuntimeException("Error adding song: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PlaylistRemoteDS", "Exception adding song", e)
            return Result.failure(e)
        }
    }
}