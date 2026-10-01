package com.irv205.rickandmortymvc.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * MODELO — Repositorio: única puerta de salida hacia la red.
 *
 * Responsabilidades:
 * 1. Configurar Retrofit (baseUrl + conversor Gson).
 * 2. Pedir los datos a [RickMortyApiService].
 * 3. Traducir DTOs -> modelos de dominio ([Character]).
 *
 * IMPORTANTE (arquitectura sin inyección de dependencias): este repositorio
 * se instancia a sí mismo con `Retrofit.Builder()`. No hay contenedor de DI,
 * no hay interfaz, no hay `provideX()`. Es la contrapartida directa de usar
 * interfaces: menos flexible para testear, pero cero infraestructura extra.
 *
 * Tampoco hace falta `withContext(Dispatchers.IO)`: al ser `suspend`, Retrofit
 * ya ejecuta la llamada en su propio hilo y devuelve el control al main thread.
 */
class CharacterRepository {

    private val api: RickMortyApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RickMortyApiService::class.java)

    /**
     * Descarga la página de personajes.
     *
     * @throws retrofit2.HttpException si el servidor responde con error (4xx/5xx).
     * @throws java.io.IOException si no hay red o se interrumpe la conexión.
     * Es intencional que NO se capturen aquí: el Controller decide qué hacer.
     */
    suspend fun getCharacters(page: Int = 1): List<Character> {
        return api.getCharacters(page).results.map { dto ->
            Character(
                id = dto.id,
                name = dto.name,
                imageUrl = dto.imageUrl,
                status = dto.status,
                species = dto.species
            )
        }
    }

    companion object {
        /** Debe terminar en "/" o Retrofit lanza IllegalArgumentException. */
        private const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}
