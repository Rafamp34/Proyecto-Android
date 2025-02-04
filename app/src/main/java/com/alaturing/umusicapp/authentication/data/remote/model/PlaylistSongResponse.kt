package com.alaturing.umusicapp.authentication.data.remote.model

data class PlaylistSongsResponseBody(
    val data: List<PlaylistSongResponse>
)

data class PlaylistSongResponse(
    val id: Int,
    val attributes: SongAttributes
)