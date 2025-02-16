package com.alaturing.umusicapp.authentication.data.repository

import com.alaturing.umusicapp.authentication.data.local.LocalDatasource.SongLocalDatasource
import com.alaturing.umusicapp.authentication.data.remote.SongRemoteDatasource
import com.alaturing.umusicapp.di.NetworkUtils
import com.alaturing.umusicapp.main.song.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SongRepositoryDefault @Inject constructor(
    private val remote: SongRemoteDatasource,
    private val local: SongLocalDatasource,
    private val networkUtils: NetworkUtils
) : SongRepository {

    override suspend fun readAll(): Result<List<Song>> {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                // Si hay internet, obtener datos remotos y guardarlos localmente
                val result = remote.getAll()
                if (result.isSuccess) {
                    result.getOrNull()?.let { songs ->
                        local.saveSongs(songs)
                    }
                }
                result
            } else {
                // Si no hay internet, usar datos locales
                Result.success(local.getAllSongs())
            }
        } catch (e: Exception) {
            // En caso de error, intentar usar datos locales
            try {
                Result.success(local.getAllSongs())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun readById(id: Int): Result<Song> {
        return remote.getById(id)
    }

    override fun observeAll(): Flow<Result<List<Song>>> = flow {
        try {
            // Emitir datos locales primero
            emit(Result.success(local.getAllSongs()))

            // Si hay conexión, actualizar con datos remotos
            if (networkUtils.isNetworkAvailable()) {
                val remoteResult = remote.getAll()
                if (remoteResult.isSuccess) {
                    remoteResult.getOrNull()?.let { songs ->
                        local.saveSongs(songs)
                        emit(Result.success(songs))
                    }
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}