package com.alaturing.umusicapp.authentication.data.repository.firebase

import com.alaturing.umusicapp.authentication.data.local.localDatasource.UserLocalDatasource
import com.alaturing.umusicapp.authentication.data.repository.UserRepository
import com.alaturing.umusicapp.authentication.model.User
import com.alaturing.umusicapp.firebase.FirebaseManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryFirebase @Inject constructor(
    private val firebaseManager: FirebaseManager,
    private val local: UserLocalDatasource
) : UserRepository {

    private val auth: FirebaseAuth
        get() = firebaseManager.getAuth()

    private val firestore: FirebaseFirestore
        get() = firebaseManager.getFirestore()

    override suspend fun login(identifier: String, password: String): Result<User> {
        return try {
            // Asumimos que el identifier es un email para Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(identifier, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // Obtener datos adicionales del usuario desde Firestore
                val userDoc = firestore.collection(FirebaseManager.USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                val user = firebaseUserToModel(firebaseUser, userDoc.data)
                local.saveUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Error al iniciar sesión"))
            }
        } catch (e: Exception) {
            // Intenta recuperar usuario local en caso de error
            val localUser = local.retrieveUser()
            if (localUser != null) {
                Result.success(localUser)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun register(user: String, email: String, password: String): Result<User> {
        return try {
            // Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // Actualizar el perfil con el nombre de usuario
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(user)
                    .build()

                firebaseUser.updateProfile(profileUpdates).await()

                // Crear documento de usuario en Firestore con datos adicionales
                val userData = hashMapOf(
                    "userName" to user,
                    "email" to email,
                    "followers" to 0,
                    "following" to 0,
                    "imageUrl" to null
                )

                firestore.collection(FirebaseManager.USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .set(userData)
                    .await()

                // Crear objeto de usuario
                val newUser = User(
                    id = firebaseUser.uid.hashCode(),
                    userName = user,
                    email = email,
                    imageUrl = null,
                    followers = 0,
                    following = 0,
                    token = firebaseUser.getIdToken(false).await().token
                )

                local.saveUser(newUser)
                Result.success(newUser)
            } else {
                Result.failure(Exception("Error al registrar usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
        local.clearUser()
    }

    override suspend fun getProfile(): Result<User> {
        val firebaseUser = auth.currentUser

        return try {
            if (firebaseUser != null) {
                // Obtener datos adicionales del usuario desde Firestore
                val userDoc = firestore.collection(FirebaseManager.USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                val user = firebaseUserToModel(firebaseUser, userDoc.data)
                local.saveUser(user)
                Result.success(user)
            } else {
                // Intentar recuperar usuario local
                val localUser = local.retrieveUser()
                if (localUser != null) {
                    Result.success(localUser)
                } else {
                    Result.failure(Exception("No hay usuario autenticado"))
                }
            }
        } catch (e: Exception) {
            val localUser = local.retrieveUser()
            if (localUser != null) {
                Result.success(localUser)
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Convierte un FirebaseUser a nuestro modelo User
     */
    private suspend fun firebaseUserToModel(firebaseUser: FirebaseUser, userData: Map<String, Any>?): User {
        val token = firebaseUser.getIdToken(false).await().token

        return User(
            id = firebaseUser.uid.hashCode(),
            userName = userData?.get("userName") as? String ?: firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            imageUrl = userData?.get("imageUrl") as? String ?: firebaseUser.photoUrl?.toString(),
            followers = (userData?.get("followers") as? Long)?.toInt() ?: 0,
            following = (userData?.get("following") as? Long)?.toInt() ?: 0,
            token = token
        )
    }
}