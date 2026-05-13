package com.clustr.app.auth

import com.clustr.app.data.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    object Loading : AuthResult()
    data class Success(val user: FirebaseUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    val authState: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    // ── Email / Password ───────────────────────────────────────────────────────

    suspend fun signUpWithEmail(
        username: String,
        email: String,
        password: String
    ): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!
            // Write user doc to Firestore
            val user = User(
                uid = firebaseUser.uid,
                username = username,
                email = email,
                createdAt = Timestamp.now()
            )
            db.collection("users").document(firebaseUser.uid).set(user).await()
            AuthResult.Success(firebaseUser)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign-up failed")
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(result.user!!)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign-in failed")
        }
    }

    // ── Google Sign-In ─────────────────────────────────────────────────────────

    suspend fun signInWithGoogle(account: GoogleSignInAccount): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result     = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user!!

            // Create user doc if this is their first time
            val docRef = db.collection("users").document(firebaseUser.uid)
            val doc    = docRef.get().await()
            if (!doc.exists()) {
                val user = User(
                    uid       = firebaseUser.uid,
                    username  = firebaseUser.displayName ?: "User",
                    email     = firebaseUser.email ?: "",
                    photoUrl  = firebaseUser.photoUrl?.toString() ?: "",
                    createdAt = Timestamp.now()
                )
                docRef.set(user).await()
            }
            AuthResult.Success(firebaseUser)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google sign-in failed")
        }
    }

    // ── User Profile ───────────────────────────────────────────────────────────

    suspend fun getUserProfile(uid: String): User? {
        return try {
            db.collection("users").document(uid).get().await().toObject(User::class.java)
        } catch (e: Exception) { null }
    }

    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>) {
        try { db.collection("users").document(uid).update(updates).await() }
        catch (_: Exception) {}
    }

    fun signOut() = auth.signOut()
}