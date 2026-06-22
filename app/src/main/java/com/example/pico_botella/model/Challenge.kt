package com.example.pico_botella.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "challenges")
data class Challenge(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
) : Serializable