package com.alaturing.umusicapp.authentication.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val userName: String,
    val email: String,
    val imageUrl: String?,
    val followers: Int,
    val following: Int,
    val token: String?
)

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val lyrics: String?,
    val album: String,
    val duration: Int,
    val imageUrl: String?
)

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val listeners: String,
    val imageUrl: String?
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val author: String,
    val duration: Int,
    val imageUrl: String?,
    val userId: Int
)

// Relaciones N:M
@Entity(
    tableName = "song_artist_cross_ref",
    primaryKeys = ["songId", "artistId"]
)
data class SongArtistCrossRef(
    val songId: Int,
    val artistId: Int
)

@Entity(
    tableName = "playlist_song_cross_ref",
    primaryKeys = ["playlistId", "songId"]
)
data class PlaylistSongCrossRef(
    val playlistId: Int,
    val songId: Int
)
