package com.alaturing.umusicapp.authentication.data.remote

import com.alaturing.umusicapp.main.song.model.Song

interface SongRemoteDatasource {
    suspend fun getAll(): Result<List<Song>>
    suspend fun getById(id: Int): Result<Song>
}