package com.alaturing.umusicapp.authentication.data.remote

import com.alaturing.umusicapp.authentication.data.remote.model.SongAttributes

data class PlaylistSongsResponseBody(
    val data: List<PlaylistSongResponse>
)

data class PlaylistSongResponse(
    val id: Int,
    val attributes: SongAttributes
)