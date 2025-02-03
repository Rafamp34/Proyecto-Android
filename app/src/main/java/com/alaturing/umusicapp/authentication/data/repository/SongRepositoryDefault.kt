package com.alaturing.umusicapp.authentication.data.repository

import com.alaturing.umusicapp.authentication.data.remote.SongRemoteDatasource
import com.alaturing.umusicapp.main.song.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SongRepositoryDefault @Inject constructor(
    private val remote: SongRemoteDatasource
) : SongRepository {

    override suspend fun readAll(): Result<List<Song>> {
        return remote.getAll()
    }

    override suspend fun readById(id: Int): Result<Song> {
        return remote.getById(id)
    }

    override fun observeAll(): Flow<Result<List<Song>>> = flow {
        val result = remote.getAll()
        emit(result)
    }
}