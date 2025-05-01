package com.alaturing.umusicapp.authentication.data.repository.firebase

import android.net.Uri
import com.alaturing.umusicapp.authentication.data.local.localDatasource.PlaylistLocalDatasourceDS
import com.alaturing.umusicapp.authentication.data.repository.PlaylistRepository
import com.alaturing.umusicapp.authentication.data.repository.SongRepository
import com.alaturing.umusicapp.firebase.FirebaseManager
import com.alaturing.umusicapp.main.playlist.model.Playlist
import com.alaturing.umusicapp.main.song.model.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class PlaylistRepositoryFirebase @Inject constructor(
    private val firebaseManager: FirebaseManager,
    private val songRepository: SongRepository,
    private val local: PlaylistLocalDatasourceDS
) : PlaylistRepository {

    private val firestore: FirebaseFirestore
        get() = firebaseManager.getFirestore()

    private val auth: FirebaseAuth
        get() = firebaseManager.getAuth()

    private val storage: FirebaseStorage
        get() = firebaseManager.getStorage()

    override suspend fun readAll(): Result<List<Playlist>> {
        val localPlaylists = local.getPlaylists()

        try {
            val playlistsSnapshot = firestore.collection(FirebaseManager.PLAYLISTS_COLLECTION)
                .get()
                .await()

            if (!playlistsSnapshot.isEmpty) {
                val playlists = playlistsSnapshot.documents.mapNotNull { document ->
                    val playlistData = document.data ?: return@mapNotNull null

                    // Obtener IDs de canciones relacionadas
                    val songIds = playlistData["songIds"] as? List<String> ?: emptyList()

                    // Crear objeto Playlist
                    Playlist(
                        id = document.id.hashCode(),
                        name = playlistData["name"] as String,
                        author = playlistData["author"] as String,
                        duration = (playlistData["duration"] as? Long)?.toInt() ?: 0,
                        imageUrl = playlistData["imageUrl"] as? String,
                        userId = (playlistData["userId"] as? String)?.hashCode() ?: 0,
                        songs = emptyList() // Las canciones se cargarán bajo demanda
                    )
                }

                // Guardar en caché local
                local.savePlaylists(playlists)
                return Result.success(playlists)
            }
        } catch (e: Exception) {
            // Si falla, devolvemos los datos locales
        }

        return Result.success(localPlaylists)
    }

    override suspend fun readById(id: Int): Result<Playlist> {
        val localPlaylists = local.getPlaylists()
        val localPlaylist = localPlaylists.find { it.id == id }?.copy(
            songs = local.getPlaylistSongs(id)
        )

        try {
            // Buscar el documento basado en su hash id
            // Nota: En producción deberíamos usar un método más robusto
            val playlistsSnapshot = firestore.collection(FirebaseManager.PLAYLISTS_COLLECTION)
                .get()
                .await()

            val document = playlistsSnapshot.documents.find { it.id.hashCode() == id }

            if (document != null) {
                val playlistData = document.data ?: return Result.failure(Exception("Playlist no encontrada"))

                // Obtener IDs de canciones relacionadas
                val songIds = playlistData["songIds"] as? List<String> ?: emptyList()

                // Cargar canciones
                val songs = if (songIds.isNotEmpty()) {
                    val songsResult = getPlaylistSongs(id)
                    songsResult.getOrNull() ?: emptyList()
                } else {
                    emptyList()
                }

                val playlist = Playlist(
                    id = document.id.hashCode(),
                    name = playlistData["name"] as String,
                    author = playlistData["author"] as String,
                    duration = (playlistData["duration"] as? Long)?.toInt() ?: 0,
                    imageUrl = playlistData["imageUrl"] as? String,
                    userId = (playlistData["userId"] as? String)?.hashCode() ?: 0,
                    songs = songs
                )

                // Guardar en caché local
                local.savePlaylistSongs(playlist.id, songs)

                return Result.success(playlist)
            }
        } catch (e: Exception) {
            // Si falla, intentamos usar los datos locales
        }

        return if (localPlaylist != null) {
            Result.success(localPlaylist)
        } else {
            Result.failure(Exception("Playlist no encontrada"))
        }
    }

    override fun observeAll(): Flow<Result<List<Playlist>>> = flow {
        // Primero emitimos los datos locales para respuesta rápida
        emit(Result.success(local.getPlaylists()))

        try {
            // Luego cargamos datos remotos
            val playlistsSnapshot = firestore.collection(FirebaseManager.PLAYLISTS_COLLECTION)
                .get()
                .await()

            if (!playlistsSnapshot.isEmpty) {
                val playlists = playlistsSnapshot.documents.mapNotNull { document ->
                    val playlistData = document.data ?: return@mapNotNull null

                    Playlist(
                        id = document.id.hashCode(),
                        name = playlistData["name"] as String,
                        author = playlistData["author"] as String,
                        duration = (playlistData["duration"] as? Long)?.toInt() ?: 0,
                        imageUrl = playlistData["imageUrl"] as? String,
                        userId = (playlistData["userId"] as? String)?.hashCode() ?: 0,
                        songs = emptyList() // Las canciones se cargarán bajo demanda
                    )
                }

                // Guardar en caché local
                local.savePlaylists(playlists)
                emit(Result.success(playlists))
            }
        } catch (e: Exception) {
            // Si falla, no emitimos error para que la UI siga usando los datos locales
        }
    }

    override suspend fun getPlaylistSongs(id: Int): Result<List<Song>> {
        val localSongs = local.getPlaylistSongs(id)

        try {
            // Buscar la playlist en Firestore
            val playlistsSnapshot = firestore.collection(FirebaseManager.PLAYLISTS_COLLECTION)
                .get()
                .await()

            val document = playlistsSnapshot.documents.find { it.id.hashCode() == id }

            if (document != null) {
                val playlistData = document.data ?: return Result.failure(Exception("Playlist no encontrada"))

                // Obtener IDs de canciones
                val songIds = playlistData["songIds"] as? List<String> ?: emptyList()

                if (songIds.isNotEmpty()) {
                    // Obtener datos de las canciones de Firestore
                    val songsSnapshot = firestore.collection(FirebaseManager.SONGS_COLLECTION)
                        .whereIn("__name__", songIds)
                        .get()
                        .await()

                    val songs = songsSnapshot.documents.mapNotNull { songDoc ->
                        val songData = songDoc.data ?: return@mapNotNull null

                        // Recuperar artistas relacionados
                        val artistIds = songData["artistIds"] as? List<String> ?: emptyList()
                        val artists = if (artistIds.isNotEmpty()) {
                            val artistsSnapshot = firestore.collection(FirebaseManager.ARTISTS_COLLECTION)
                                .whereIn("__name__", artistIds)
                                .get()
                                .await()

                            artistsSnapshot.documents.mapNotNull { artistDoc ->
                                val artistData = artistDoc.data ?: return@mapNotNull null

                                com.alaturing.umusicapp.authentication.model.Artist(
                                    id = artistDoc.id.hashCode(),
                                    name = artistData["name"] as String,
                                    listeners = (artistData["listeners"] as? Long)?.toString() ?: "0",
                                    imageUrl = artistData["imageUrl"] as? String
                                )
                            }
                        } else {
                            emptyList()
                        }

                        Song(
                            id = songDoc.id.hashCode(),
                            name = songData["name"] as String,
                            lyrics = songData["lyrics"] as? String,
                            album = songData["album"] as String,
                            duration = (songData["duration"] as Long).toInt(),
                            imageUrl = songData["imageUrl"] as? String,
                            artists = artists
                        )
                    }

                    // Guardar en caché local
                    local.savePlaylistSongs(id, songs)

                    return Result.success(songs)
                } else {
                    return Result.success(emptyList())
                }
            }
        } catch (e: Exception) {
            // Si falla, devolvemos los datos locales
        }

        return Result.success(localSongs)
    }

    override suspend fun addSongToPlaylist(playlistId: Int, songId: Int): Result<Unit> {
        try {
            // Buscar la playlist en Firestore
            val playlistsSnapshot = firestore.collection(FirebaseManager.PLAYLISTS_COLLECTION)
                .get()
                .await()

            val playlistDoc = playlistsSnapshot.documents.find { it.id.hashCode() == playlistId }
                ?: return Result.failure(Exception("Playlist no encontrada"))

            // Buscar la canción en Firestore
            val songsSnapshot = firestore.collection(FirebaseManager.SONGS_COLLECTION)
                .get()
                .await()

            val songDoc = songsSnapshot.documents.find { it.id.hashCode() == songId }
                ?: return Result.failure(Exception("Canción no encontrada"))

            // Obtener lista actual de canciones
            val playlistData = playlistDoc.data ?: return Result.failure(Exception("Error al leer datos de playlist"))
            val currentSongIds = playlistData["songIds"] as? MutableList<String> ?: mutableListOf()

            // Añadir la nueva canción si no existe ya
            if (!currentSongIds.contains(songDoc.id)) {
                currentSongIds.add(songDoc.id)

                // Actualizar duración
                val songData = songDoc.data ?: return Result.failure(Exception("Error al leer datos de canción"))
                val songDuration = (songData["duration"] as? Long)?.toInt() ?: 0
                val currentDuration = (playlistData["duration"] as? Long)?.toInt() ?: 0
                val newDuration = currentDuration + songDuration

                // Actualizar la playlist en Firestore
                firestore.collection(FirebaseManager.PLAYLISTS_COLLECTION)
                    .document(playlistDoc.id)
                    .update(
                        mapOf(
                            "songIds" to currentSongIds,
                            "duration" to newDuration
                        )
                    )
                    .await()

                // Actualizar la caché local
                readById(playlistId)

                return Result.success(Unit)
            } else {
                return Result.success(Unit) // La canción ya está en la playlist
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun removeSongFromPlaylist(playlistId: Int, songId: Int): Result<Unit> {
        try {
            // Buscar la playlist en Firestore
            val playlistsSnapshot = firestore.collection(FirebaseManager.PLAYLISTS_COLLECTION)
                .get()
                .await()

            val playlistDoc = playlistsSnapshot.documents.find { it.id.hashCode() == playlistId }
                ?: return Result.failure(Exception("Playlist no encontrada"))

            // Buscar la canción en Firestore
            val songsSnapshot = firestore.collection(FirebaseManager.SONGS_COLLECTION)
                .get()
                .await()

            val songDoc = songsSnapshot.documents.find { it.id.hashCode() == songId }
                ?: return Result.failure(Exception("Canción no encontrada"))

            // Obtener lista actual de canciones
            val playlistData = playlistDoc.data ?: return Result.failure(Exception("Error al leer datos de playlist"))
            val currentSongIds = playlistData["songIds"] as? MutableList<String> ?: mutableListOf()

            // Eliminar la canción si existe
            if (currentSongIds.contains(songDoc.id)) {
                currentSongIds.remove(songDoc.id)

                // Actualizar duración
                val songData = songDoc.data ?: return Result.failure(Exception("Error al leer datos de canción"))
                val songDuration = (songData["duration"] as? Long)?.toInt() ?: 0
                val currentDuration = (playlistData["duration"] as? Long)?.toInt() ?: 0
                val newDuration = (currentDuration - songDuration).coerceAtLeast(0)

                // Actualizar la playlist en Firestore
                firestore.collection(FirebaseManager.PLAYLISTS_COLLECTION)
                    .document(playlistDoc.id)
                    .update(
                        mapOf(
                            "songIds" to currentSongIds,
                            "duration" to newDuration
                        )
                    )
                    .await()

                // Actualizar la caché local
                readById(playlistId)

                return Result.success(Unit)
            } else {
                return Result.success(Unit) // La canción no está en la playlist
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun createPlaylist(name: String, author: String, imageId: Int?): Result<Playlist> {
        try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("Usuario no autenticado"))

            // Crear datos de la playlist
            val playlistData = hashMapOf(
                "name" to name,
                "author" to author,
                "duration" to 0,
                "userId" to currentUser.uid,
                "songIds" to listOf<String>(),
                "imageUrl" to null // Se actualizará después si hay imagen
            )

            // Crear documento en Firestore
            val documentRef = firestore.collection(FirebaseManager.PLAYLISTS_COLLECTION)
                .add(playlistData)
                .await()

            // Crear objeto Playlist
            val playlist = Playlist(
                id = documentRef.id.hashCode(),
                name = name,
                author = author,
                duration = 0,
                imageUrl = null,
                userId = currentUser.uid.hashCode(),
                songs = emptyList()
            )

            // Actualizar la caché local
            readAll()

            return Result.success(playlist)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun uploadImage(uri: Uri): Result<Int> {
        try {
            // Generar un nombre único para la imagen
            val imageName = "playlist_images/${UUID.randomUUID()}.jpg"
            val storageRef: StorageReference = storage.reference.child(imageName)

            // Subir la imagen a Firebase Storage
            val uploadTask = storageRef.putFile(uri).await()

            // Obtener la URL de descarga
            val downloadUrl = storageRef.downloadUrl.await()

            // Devolver un hash del path como ID de imagen
            return Result.success(imageName.hashCode())
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun deletePlaylist(id: Int): Result<Unit> {
        try {
            // Buscar la playlist en Firestore
            val playlistsSnapshot = firestore.collection(FirebaseManager.PLAYLISTS_COLLECTION)
                .get()
                .await()

            val document = playlistsSnapshot.documents.find { it.id.hashCode() == id }
                ?: return Result.failure(Exception("Playlist no encontrada"))

            // Obtener datos de la playlist para verificar permisos
            val playlistData = document.data ?: return Result.failure(Exception("Error al leer datos de playlist"))
            val userId = playlistData["userId"] as? String
            val currentUser = auth.currentUser

            // Verificar que el usuario actual sea el propietario
            if (currentUser == null || userId != currentUser.uid) {
                return Result.failure(Exception("No tienes permiso para eliminar esta playlist"))
            }

            // Eliminar la imagen si existe
            val imageUrl = playlistData["imageUrl"] as? String
            if (imageUrl != null && imageUrl.contains("playlist_images/")) {
                // Extraer el path de la imagen
                val imagePath = "playlist_images/" + imageUrl.substringAfter("playlist_images/")
                val imageRef = storage.reference.child(imagePath)
                try {
                    imageRef.delete().await()
                } catch (e: Exception) {
                    // Continuar incluso si falla el borrado de la imagen
                }
            }

            // Eliminar el documento de Firestore
            firestore.collection(FirebaseManager.PLAYLISTS_COLLECTION)
                .document(document.id)
                .delete()
                .await()

            // Eliminar de la caché local
            local.deletePlaylist(id)

            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}