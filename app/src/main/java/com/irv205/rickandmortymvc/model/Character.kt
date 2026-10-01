package com.irv205.rickandmortymvc.model

/**
 * MODELO DE DOMINIO — Personaje.
 *
 * Esta es la única clase que la capa de Vista conoce. Es un `data class` puro:
 * no sabe nada de Android, de Retrofit ni de la API. Si mañana cambian los
 * nombres de los campos en el JSON, este archivo no se toca.
 *
 * Los DTOs que sí reflejan el JSON de la API viven en [CharacterDto].
 */
data class Character(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val status: String,
    val species: String
)
