package com.alaturing.umusicapp.authentication.data.repository

import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.SongLocalDatasource
import com.alaturing.umusicapp.authentication.data.remote.SongRemoteDatasource
import com.alaturing.umusicapp.main.song.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SongRepositoryDefault @Inject constructor(
    private val remote: SongRemoteDatasource,
    private val local: SongLocalDatasource
) : SongRepository {

    override suspend fun readAll(): Result<List<Song>> {
        val localSongs = local.getAllSongs()

        try {
            val remoteResult = remote.getAll()
            if (remoteResult.isSuccess) {
                val songs = remoteResult.getOrNull()!!
                local.saveSongs(songs)
                return Result.success(songs)
            }
        } catch (e: Exception) {
        }

        return Result.success(localSongs)
    }

    override suspend fun readById(id: Int): Result<Song> {
        val localSongs = local.getAllSongs()
        val localSong = localSongs.find { it.id == id }

        try {
            val remoteResult = remote.getById(id)
            if (remoteResult.isSuccess) {
                val song = remoteResult.getOrNull()!!
                local.saveSongs(localSongs.map { if (it.id == id) song else it })
                return Result.success(song)
            }
        } catch (e: Exception) {
        }

        return if (localSong != null) {
            Result.success(localSong)
        } else {
            Result.failure(Exception("Canción no encontrada"))
        }
    }

    override fun observeAll(): Flow<Result<List<Song>>> = flow {
        emit(Result.success(local.getAllSongs()))

        try {
            val remoteResult = remote.getAll()
            if (remoteResult.isSuccess) {
                val songs = remoteResult.getOrNull()!!
                local.saveSongs(songs)
                emit(Result.success(songs))
            }
        } catch (e: Exception) {
        }
    }
}