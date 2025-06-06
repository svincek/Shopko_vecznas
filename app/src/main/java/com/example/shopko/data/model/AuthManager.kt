package com.example.shopko.data.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

object AuthManager {
    private val auth = FirebaseAuth.getInstance()

    fun register(
        email: String,
        password: String,
        name: String,
        surname: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userProfile = hashMapOf(
                        "name" to name,
                        "surname" to surname,
                        "email" to email
                    )

                    db.collection("users").document(userId).set(userProfile)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            onResult(false, "Failed to save profile: ${e.message}")
                        }
                } else {
                    onResult(false, "Register failed: ${task.exception?.message}")
                }
            }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, it.exception?.message)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    fun fetchUserProfile(onResult: (String?, String?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name")
                val surname = doc.getString("surname")
                onResult(name, surname)
            }
            .addOnFailureListener {
                onResult(null, null)
            }
    }

    fun updateUserNameAndSurname(newName: String, newSurname: String){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val updatedUserData = mapOf(
            "name" to newName,
            "surname" to newSurname
        )

        db.collection("users").document(uid)
            .update(updatedUserData)
    }
}