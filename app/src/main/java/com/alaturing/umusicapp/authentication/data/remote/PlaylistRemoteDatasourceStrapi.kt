package com.alaturing.umusicapp.authentication.data.remote

import android.util.Log
import com.alaturing.umusicapp.authentication.data.remote.model.toModel
import com.alaturing.umusicapp.authentication.data.remote.model.toSong
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
        return try {
            val response = api.getPlaylists()
            if (response.isSuccessful && response.body() != null) {
                val playlists = response.body()!!.data.map { it.toModel() }
                Log.d("PlaylistRemoteDS", "Playlists loaded: ${playlists.size}")
                Result.success(playlists)
            } else {
                Log.e("PlaylistRemoteDS", "Error loading playlists: ${response.code()}")
                Result.failure(RuntimeException("Error loading playlists: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PlaylistRemoteDS", "Exception loading playlists", e)
            Result.failure(e)
        }
    }

    override suspend fun getById(id: Int): Result<Playlist> {
        return try {
            val response = api.getPlaylistById(id)
            if (response.isSuccessful && response.body() != null) {
                val playlist = response.body()!!.data.toModel()
                Log.d("PlaylistRemoteDS", "Playlist loaded: ${playlist.name}")
                Result.success(playlist)
            } else {
                Log.e("PlaylistRemoteDS", "Error loading playlist: ${response.code()}")
                Result.failure(RuntimeException("Error loading playlist: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PlaylistRemoteDS", "Exception loading playlist", e)
            Result.failure(e)
        }
    }

    override suspend fun getPlaylistSongs(id: Int): Result<List<Song>> {
        return try {
            val response = api.getPlaylistSongs(id)
            Log.d("PlaylistRemoteDS", "Response: ${response.isSuccessful}, Code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val songs = response.body()!!.data.map { it.toSong() }
                Log.d("PlaylistRemoteDS", "Songs loaded: ${songs.size}")
                Result.success(songs)
            } else {
                Log.e("PlaylistRemoteDS", "Error body: ${response.errorBody()?.string()}")
                Result.failure(RuntimeException("Error loading songs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PlaylistRemoteDS", "Exception loading songs", e)
            Result.failure(e)
        }
    }

    override suspend fun removeSongFromPlaylist(playlistId: Int, songId: Int): Result<Unit> {
        return try {
            val playlistResult = getById(playlistId)
            if (playlistResult.isFailure) {
                return Result.failure(playlistResult.exceptionOrNull() ?: RuntimeException())
            }

            val currentPlaylist = playlistResult.getOrNull()!!
            val updatedSongIds = currentPlaylist.songs.map { it.id }.filter { it != songId }

            val response = api.updatePlaylist(
                playlistId,
                PlaylistUpdateBody(PlaylistUpdateData(updatedSongIds))
            )

            if (response.isSuccessful) {
                Log.d("PlaylistRemoteDS", "Song removed successfully")
                Result.success(Unit)
            } else {
                Log.e("PlaylistRemoteDS", "Error removing song: ${response.errorBody()?.string()}")
                Result.failure(RuntimeException("Error removing song: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PlaylistRemoteDS", "Exception removing song", e)
            Result.failure(e)
        }
    }

    override suspend fun addSongToPlaylist(playlistId: Int, songId: Int): Result<Unit> {
        return try {
            val playlistResult = getById(playlistId)
            if (playlistResult.isFailure) {
                return Result.failure(playlistResult.exceptionOrNull() ?: RuntimeException())
            }

            val currentPlaylist = playlistResult.getOrNull()!!
            val updatedSongIds = currentPlaylist.songs.map { it.id } + songId

            val response = api.updatePlaylist(
                playlistId,
                PlaylistUpdateBody(PlaylistUpdateData(updatedSongIds))
            )

            if (response.isSuccessful) {
                Log.d("PlaylistRemoteDS", "Song added successfully")
                Result.success(Unit)
            } else {
                Log.e("PlaylistRemoteDS", "Error adding song: ${response.errorBody()?.string()}")
                Result.failure(RuntimeException("Error adding song: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("PlaylistRemoteDS", "Exception adding song", e)
            Result.failure(e)
        }
    }
}