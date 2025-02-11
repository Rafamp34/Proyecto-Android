package com.alaturing.umusicapp.authentication.data.remote

import android.util.Log
import com.alaturing.umusicapp.authentication.data.remote.model.toSong
import com.alaturing.umusicapp.common.remote.StrapiApi
import com.alaturing.umusicapp.main.song.model.Song
import javax.inject.Inject

class SongRemoteDatasourceStrapi @Inject constructor(
    private val api: StrapiApi
) : SongRemoteDatasource {

    override suspend fun getAll(): Result<List<Song>> {
        return try {
            val response = api.getSongs()
            if (response.isSuccessful && response.body() != null) {
                val songs = response.body()!!.data.map { it.toSong() }
                Log.d("SongRemoteDS", "Songs loaded: ${songs.size}")
                Result.success(songs)
            } else {
                Log.e("SongRemoteDS", "Error loading songs: ${response.code()}")
                Log.e("SongRemoteDS", "Error body: ${response.errorBody()?.string()}")
                Result.failure(RuntimeException("Error loading songs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("SongRemoteDS", "Exception loading songs", e)
            Result.failure(e)
        }
    }

    override suspend fun getById(id: Int): Result<Song> {
        return try {
            val response = api.getSongById(id)
            if (response.isSuccessful && response.body() != null) {
                val song = response.body()!!.data.toSong()
                Log.d("SongRemoteDS", "Song loaded: ${song.name}")
                Result.success(song)
            } else {
                Log.e("SongRemoteDS", "Error loading song: ${response.code()}")
                Log.e("SongRemoteDS", "Error body: ${response.errorBody()?.string()}")
                Result.failure(RuntimeException("Error loading song: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("SongRemoteDS", "Exception loading song", e)
            Result.failure(e)
        }
    }
}