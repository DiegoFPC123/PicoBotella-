package com.example.pico_botella.model

data class UserResponse(
    val email: String? = null,
    val isRegister: Boolean = false,
    val message: String = ""
)