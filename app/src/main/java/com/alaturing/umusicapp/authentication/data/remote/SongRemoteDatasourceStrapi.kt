package com.alaturing.umusicapp.authentication.data.remote

import com.alaturing.umusicapp.authentication.data.remote.model.toModel
import com.alaturing.umusicapp.common.remote.StrapiApi
import com.alaturing.umusicapp.main.song.model.Song
import javax.inject.Inject

class SongRemoteDatasourceStrapi @Inject constructor(
    private val api: StrapiApi
) : SongRemoteDatasource {

    override suspend fun getAll(): Result<List<Song>> {
        val response = api.getSongs()
        return if (response.isSuccessful) {
            Result.success(response.body()!!.data.map { it.toModel() })
        } else {
            Result.failure(RuntimeException())
        }
    }

    override suspend fun getById(id: Int): Result<Song> {
        val response = api.getSongById(id)
        return if (response.isSuccessful) {
            Result.success(response.body()!!.data.toModel())
        } else {
            Result.failure(RuntimeException())
        }
    }
}