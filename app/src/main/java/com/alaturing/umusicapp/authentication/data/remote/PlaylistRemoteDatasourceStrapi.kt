package com.alaturing.umusicapp.authentication.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.alaturing.umusicapp.authentication.data.remote.model.CreatePlaylistBody
import com.alaturing.umusicapp.authentication.data.remote.model.CreatePlaylistData
import com.alaturing.umusicapp.authentication.data.remote.model.ImageReference
import com.alaturing.umusicapp.authentication.data.remote.model.toModel
import com.alaturing.umusicapp.authentication.data.remote.model.toSong
import com.alaturing.umusicapp.common.remote.PlaylistUpdateBody
import com.alaturing.umusicapp.common.remote.PlaylistUpdateData
import com.alaturing.umusicapp.common.remote.StrapiApi
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class PlaylistRemoteDatasourceStrapi @Inject constructor(
    private val api: StrapiApi,
    @ApplicationContext private val context: Context
) : PlaylistRemoteDatasource {
    data class ImageData(val id: Int)

    override suspend fun getAll(): Result<List<Playlist>> {
        return try {
            val response = api.getPlaylists()
            if (response.isSuccessful && response.body() != null) {
                val playlists = response.body()!!.data.map { it.toModel() }
                Result.success(playlists)
            } else {
                Result.failure(RuntimeException("Error loading playlists: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getById(id: Int): Result<Playlist> {
        return try {
            val response = api.getPlaylistById(id)
            if (response.isSuccessful && response.body() != null) {
                val playlist = response.body()!!.data.toModel()
                Result.success(playlist)
            } else {
                Result.failure(RuntimeException("Error loading playlist: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlaylistSongs(id: Int): Result<List<Song>> {
        return try {
            val response = api.getPlaylistSongs(id)

            if (response.isSuccessful && response.body() != null) {
                val songs = response.body()!!.data.map { it.toSong() }
                Result.success(songs)
            } else {
                Result.failure(RuntimeException("Error loading songs: ${response.code()}"))
            }
        } catch (e: Exception) {
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
                Result.success(Unit)
            } else {
                Result.failure(RuntimeException("Error removing song: ${response.code()}"))
            }
        } catch (e: Exception) {
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
                Result.success(Unit)
            } else {
                Result.failure(RuntimeException("Error adding song: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun createPlaylist(name: String, author: String, imageId: Int?): Result<Playlist> {
        try {
            val response = api.createPlaylist(
                CreatePlaylistBody(
                    data = CreatePlaylistData(
                        name = name,
                        author = author,
                        duration = "0",
                        image = imageId?.let { ImageReference(listOf(it)) }
                    )
                )
            )

            return if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data.toModel())
            } else {
                Result.failure(RuntimeException("Error creating playlist"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }


    override suspend fun uploadImage(uri: Uri): Result<Int> {
        try {
            // Convertir Uri a File y MultipartBody.Part
            val file = getFileFromUri(uri, context)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("files", file.name, requestFile)

            val response = api.uploadFile(body)
            return if (response.isSuccessful && response.body() != null) {
                val imageId = response.body()!!.first().id
                Result.success(imageId)
            } else {
                Result.failure(RuntimeException("Error uploading image"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun getFileFromUri(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile("upload", null, context.cacheDir)

        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}