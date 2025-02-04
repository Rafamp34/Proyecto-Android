package com.alaturing.umusicapp.common.remote

import com.alaturing.umusicapp.authentication.data.remote.model.AuthRequestBody
import com.alaturing.umusicapp.authentication.data.remote.model.AuthResponseBody
import com.alaturing.umusicapp.authentication.data.remote.model.AuthResponseUser
import com.alaturing.umusicapp.authentication.data.remote.model.PlaylistResponseBody
import com.alaturing.umusicapp.authentication.data.remote.model.PlaylistsResponseBody
import com.alaturing.umusicapp.authentication.data.remote.model.RegisterRequestBody
import com.alaturing.umusicapp.authentication.data.remote.model.SongResponseBody
import com.alaturing.umusicapp.authentication.data.remote.model.SongsResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * API del remoto para Retrofit
 */
interface StrapiApi: StrapiAuthenticationApi,
    StrapiSongApi,
    StrapiPlaylistApi,
    StrapiProfileApi

data class PlaylistUpdateBody(
    val data: PlaylistUpdateData
)

data class PlaylistUpdateData(
    val song_IDS: List<Int>
)

/**
 * Autenticación
 */
interface StrapiAuthenticationApi {
    @POST("/api/auth/local")
    suspend fun login(@Body body: AuthRequestBody): Response<AuthResponseBody>

    @POST("/api/auth/local/register")
    suspend fun register(@Body body: RegisterRequestBody): Response<AuthResponseBody>
}

/**
 * Canciones
 */
interface StrapiSongApi {
    @GET("/api/songs")
    suspend fun getSongs(
        @Query("populate") populate: String = "*"
    ): Response<SongsResponseBody>

    @GET("/api/songs/{id}")
    suspend fun getSongById(
        @Path("id") id: Int,
        @Query("populate") populate: String = "*"
    ): Response<SongResponseBody>
}

/**
 * Playlists
 */
interface StrapiPlaylistApi {
    @GET("/api/playlists")
    suspend fun getPlaylists(
        @Query("populate") populate: String = "*"
    ): Response<PlaylistsResponseBody>

    @GET("/api/playlists/{id}")
    suspend fun getPlaylistById(
        @Path("id") id: Int,
        @Query("populate") populate: String = "image,song_IDS.image"
    ): Response<PlaylistResponseBody>

    @GET("/api/playlists/{id}/songs")
    suspend fun getPlaylistSongs(
        @Path("id") id: Int,
        @Query("populate") populate: String = "*"
    ): Response<SongsResponseBody>

    @PUT("/api/playlists/{id}")
    suspend fun updatePlaylist(
        @Path("id") id: Int,
        @Body data: PlaylistUpdateBody
    ): Response<PlaylistResponseBody>
}


interface StrapiProfileApi {
    @GET("/api/users/me?populate[image]=*")
    suspend fun getProfile(): Response<AuthResponseUser>
}