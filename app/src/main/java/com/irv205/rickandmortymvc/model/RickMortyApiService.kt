package com.irv205.rickandmortymvc.model

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * MODELO — Contrato de la API Rick & Morty.
 *
 * Es una interfaz: Retrofit genera la implementación en tiempo de ejecución.
 * Solo se declaran endpoints, no hay cuerpo de métodos.
 *
 * - `@GET("character")` se concatena a la baseUrl del repository.
 * - `@Query("page")` mapea el parámetro de la query string.
 * - `suspend` permite que Retrofit ejecute la llamada fuera del main thread y
 *   que la corrutina se reanude sola con el resultado (o la excepción).
 */
interface RickMortyApiService {

    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int = 1): CharactersResponse
}
