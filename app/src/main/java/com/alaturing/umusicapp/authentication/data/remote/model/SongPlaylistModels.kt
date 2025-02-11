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
    val artists_IDS: ArtistsData  // Cambiado de 'artists' a 'artists_IDS'
)

data class ArtistsData(
    val data: List<ArtistResponse>
)

data class ArtistResponse(
    val id: Int,
    val attributes: ArtistAttributes
)

data class ArtistAttributes(
    val Name: String,  // Cambiado de 'name' a 'Name' para coincidir con la API
    val listeners: String,  // Cambiado de Int a String para coincidir con la API
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

data class PlaylistAttributes(
    val name: String,
    val author: String,
    val duration: String,  // Cambiado a String ya que viene como string en la API
    val image: Media?,
    val song_IDS: PlaylistSongIdsData?,
    val users_IDS: UsersIdsData?
)

data class PlaylistSongIdsData(
    val data: List<PlaylistSongResponse>
)

data class PlaylistSongResponse(
    val id: Int,
    val attributes: PlaylistSongAttributes
)

data class PlaylistSongAttributes(
    val name: String,
    val album: String,
    val duration: Int,
    val lyrics: String?,
    val image: Media?,
    val artists_IDS: ArtistsData?
)

data class UsersIdsData(           // Nueva clase para users_IDS
    val data: List<UserData>
)

data class UserData(               // Nueva clase para representar el usuario en la respuesta
    val id: Int,
    val attributes: UserAttributes
)

data class UserAttributes(         // Nueva clase para los atributos del usuario
    val username: String,
    val email: String,
    val provider: String,
    val confirmed: Boolean,
    val blocked: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val followers: Int?,
    val following: Int?
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