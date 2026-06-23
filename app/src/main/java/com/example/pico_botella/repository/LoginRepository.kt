package com.example.pico_botella.repository

import com.example.pico_botella.model.UserRequest
import com.example.pico_botella.model.UserResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun registerUser(userRequest: UserRequest, userResponse: (UserResponse) -> Unit) {
        withContext(Dispatchers.IO) {
            firebaseAuth.createUserWithEmailAndPassword(userRequest.email, userRequest.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        userResponse(UserResponse(userRequest.email, true, "Registro Exitoso"))
                    } else {
                        val message = if (task.exception is FirebaseAuthUserCollisionException) {
                            "Error en el registro"
                        } else {
                            "Error en el registro"
                        }
                        userResponse(UserResponse(isRegister = false, message = message))
                    }
                }
        }
    }

    fun loginUser(email: String, pass: String, isLogin: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isLogin(true)
                } else {
                    isLogin(false)
                }
            }
    }

    fun sesion(email: String?, isEnableView: (Boolean) -> Unit) {
        if (email != null) {
            isEnableView(true)
        } else {
            isEnableView(false)
        }
    }
}