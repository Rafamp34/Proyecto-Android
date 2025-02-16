package com.alaturing.umusicapp.authentication.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alaturing.umusicapp.authentication.data.local.entities.ArtistEntity

@Dao
interface ArtistDao {
    @Query("SELECT * FROM artists")
    suspend fun getAllArtists(): List<ArtistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)
}