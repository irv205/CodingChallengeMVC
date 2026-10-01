package com.irv205.rickandmortymvc.model

import com.google.gson.annotations.SerializedName

/**
 * MODELO DE DATOS (DTO) — Reflejo exacto del JSON de la API.
 *
 * La API responde algo como:
 *   { "info": {...}, "results": [ { "id": 1, "name": "Rick Sanchez",
 *                                  "image": "https://...", ... } ] }
 *
 * Gson mapea por nombre de campo, así que solo hace falta @SerializedName cuando
 * el nombre en Kotlin difiere del JSON. Aquí `image` -> `imageUrl`.
 *
 * El `CharacterDto` nunca sale de la capa Model: [CharacterRepository] lo
 * convierte a [Character] para que el resto de la app no dependa del JSON.
 */
data class CharacterDto(
    val id: Int,
    val name: String,
    @SerializedName("image") val imageUrl: String,
    val status: String,
    val species: String
)

/** Envoltorio de la respuesta paginada de `GET /character`. */
data class CharactersResponse(
    val results: List<CharacterDto>
)
