package com.alaturing.umusicapp.authentication.data.remote.model

data class SongsResponseBody(
    val data: List<SongResponse>
)

data class SongResponseBody(
    val data: SongResponse
)

data class SongResponse(
    val id: Int,
    val attributes: SongAttributes
)

data class SongAttributes(
    val name: String,
    val lyrics: String?,
    val album: String,
    val duration: Int,
    val image: Media?,
    val artists: ArtistsData?
)

data class ArtistsData(
    val data: List<ArtistResponse>
)

data class ArtistResponse(
    val id: Int,
    val attributes: ArtistAttributes
)

data class ArtistAttributes(
    val name: String,
    val listeners: Int,
    val image: Media?
)

data class PlaylistsResponseBody(
    val data: List<PlaylistResponse>
)

data class PlaylistResponseBody(
    val data: PlaylistResponse
)

data class PlaylistResponse(
    val id: Int,
    val attributes: PlaylistAttributes
)

data class SongIdsData(
    val data: List<SongResponse>
)

data class PlaylistAttributes(
    val name: String,
    val author: String,
    val duration: Int,
    val image: Media?,
    val song_IDS: SongIdsData?
)

data class Media(
    val data: MediaAttributes?
)

data class MediaAttributes(
    val attributes: MediaFormat
)

data class MediaFormat(
    val url: String,
    val formats: MediaFormats?
)

data class MediaFormats(
    val small: ImageAttributes?,
    val thumbnail: ImageAttributes?
)

data class ImageAttributes(
    val url: String
)