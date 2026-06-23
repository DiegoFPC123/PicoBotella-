package com.example.pico_botella.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "challenges")
data class Challenge(
    @PrimaryKey
    val id: String = "", // Usaremos el ID de Firestore como Primary Key para evitar duplicados
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Serializable {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", "", System.currentTimeMillis())
}