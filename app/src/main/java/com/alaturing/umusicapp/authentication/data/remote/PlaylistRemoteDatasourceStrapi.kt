package com.alaturing.umusicapp.authentication.data.remote

import android.util.Log
import com.alaturing.umusicapp.authentication.data.remote.model.toModel
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
}