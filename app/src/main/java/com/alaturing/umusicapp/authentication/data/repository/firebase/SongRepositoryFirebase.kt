package com.alaturing.umusicapp.authentication.data.repository.firebase

import com.alaturing.umusicapp.authentication.data.local.localDatasource.SongLocalDatasource
import com.alaturing.umusicapp.authentication.data.repository.SongRepository
import com.alaturing.umusicapp.authentication.model.Artist
import com.alaturing.umusicapp.firebase.FirebaseManager
import com.alaturing.umusicapp.main.song.model.Song
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SongRepositoryFirebase @Inject constructor(
    private val firebaseManager: FirebaseManager,
    private val local: SongLocalDatasource
) : SongRepository {

    private val firestore: FirebaseFirestore
        get() = firebaseManager.getFirestore()

    override suspend fun readAll(): Result<List<Song>> {
        val localSongs = local.getAllSongs()

        try {
            val songsSnapshot = firestore.collection(FirebaseManager.SONGS_COLLECTION)
                .get()
                .await()

            if (!songsSnapshot.isEmpty) {
                val songs = songsSnapshot.documents.mapNotNull { document ->
                    val songData = document.data ?: return@mapNotNull null

                    // Recuperar artistas relacionados
                    val artistIds = songData["artistIds"] as? List<String> ?: emptyList()
                    val artists = if (artistIds.isNotEmpty()) {
                        getArtistsForSong(artistIds)
                    } else {
                        emptyList()
                    }

                    Song(
                        id = document.id.hashCode(),
                        name = songData["name"] as String,
                        lyrics = songData["lyrics"] as? String,
                        album = songData["album"] as String,
                        duration = (songData["duration"] as Long).toInt(),
                        imageUrl = songData["imageUrl"] as? String,
                        artists = artists
                    )
                }

                // Guardar canciones en caché local
                local.saveSongs(songs)
                return Result.success(songs)
            }
        } catch (e: Exception) {
            // Si falla la carga remota, devolvemos los datos locales
        }

        return Result.success(localSongs)
    }

    override suspend fun readById(id: Int): Result<Song> {
        val localSongs = local.getAllSongs()
        val localSong = localSongs.find { it.id == id }

        try {
            // Buscar el documento basado en su hash id
            // Nota: Esta no es la manera ideal. En producción deberíamos usar un método más robusto
            // para mapear entre IDs de Strapi y Firebase
            val songsSnapshot = firestore.collection(FirebaseManager.SONGS_COLLECTION)
                .get()
                .await()

            val document = songsSnapshot.documents.find { it.id.hashCode() == id }

            if (document != null) {
                val songData = document.data ?: return Result.failure(Exception("Canción no encontrada"))

                // Recuperar artistas relacionados
                val artistIds = songData["artistIds"] as? List<String> ?: emptyList()
                val artists = if (artistIds.isNotEmpty()) {
                    getArtistsForSong(artistIds)
                } else {
                    emptyList()
                }

                val song = Song(
                    id = document.id.hashCode(),
                    name = songData["name"] as String,
                    lyrics = songData["lyrics"] as? String,
                    album = songData["album"] as String,
                    duration = (songData["duration"] as Long).toInt(),
                    imageUrl = songData["imageUrl"] as? String,
                    artists = artists
                )

                return Result.success(song)
            }
        } catch (e: Exception) {
            // Si falla la carga remota, intentamos usar los datos locales
        }

        return if (localSong != null) {
            Result.success(localSong)
        } else {
            Result.failure(Exception("Canción no encontrada"))
        }
    }

    override fun observeAll(): Flow<Result<List<Song>>> = flow {
        // Primero emitimos los datos locales para respuesta rápida
        emit(Result.success(local.getAllSongs()))

        try {
            // Luego cargamos datos remotos
            val songsSnapshot = firestore.collection(FirebaseManager.SONGS_COLLECTION)
                .get()
                .await()

            if (!songsSnapshot.isEmpty) {
                val songs = songsSnapshot.documents.mapNotNull { document ->
                    val songData = document.data ?: return@mapNotNull null

                    // Recuperar artistas relacionados
                    val artistIds = songData["artistIds"] as? List<String> ?: emptyList()
                    val artists = if (artistIds.isNotEmpty()) {
                        getArtistsForSong(artistIds)
                    } else {
                        emptyList()
                    }

                    Song(
                        id = document.id.hashCode(),
                        name = songData["name"] as String,
                        lyrics = songData["lyrics"] as? String,
                        album = songData["album"] as String,
                        duration = (songData["duration"] as Long).toInt(),
                        imageUrl = songData["imageUrl"] as? String,
                        artists = artists
                    )
                }

                // Guardar en caché local
                local.saveSongs(songs)
                emit(Result.success(songs))
            }
        } catch (e: Exception) {
            // Si falla, no emitimos error para que la UI siga usando los datos locales
        }
    }

    /**
     * Obtiene los artistas relacionados con una canción
     */
    private suspend fun getArtistsForSong(artistIds: List<String>): List<Artist> {
        return try {
            val artistsSnapshot = firestore.collection(FirebaseManager.ARTISTS_COLLECTION)
                .whereIn("__name__", artistIds)
                .get()
                .await()

            artistsSnapshot.documents.mapNotNull { document ->
                val artistData = document.data ?: return@mapNotNull null

                Artist(
                    id = document.id.hashCode(),
                    name = artistData["name"] as String,
                    listeners = (artistData["listeners"] as? Long)?.toString() ?: "0",
                    imageUrl = artistData["imageUrl"] as? String
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}