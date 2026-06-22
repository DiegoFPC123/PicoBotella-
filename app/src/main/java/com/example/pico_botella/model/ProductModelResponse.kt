package com.example.pico_botella.model

import com.google.gson.annotations.SerializedName

data class ProductModelResponse(
    @SerializedName("pokemon")
    val pokemon: List<Pokemon>
)

data class Pokemon(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("img")
    val img: String
)