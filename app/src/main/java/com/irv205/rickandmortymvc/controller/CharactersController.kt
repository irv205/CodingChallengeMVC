package com.irv205.rickandmortymvc.controller

import com.irv205.rickandmortymvc.model.CharacterRepository
import com.irv205.rickandmortymvc.view.CharactersView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * CONTROLADOR — El cerebro de la app.
 *
 * Es el único punto donde se conecta el Model con la View:
 *   1. La Activity le avisa de un evento -> [loadCharacters]
 *   2. El Controller le pide los datos al Model
 *   3. El Controller le indica a la View qué pintar
 *
 * Reglas de la capa:
 * - No sabe nada de Android (no importa ni una sola clase de `android.*`).
 * - No sabe nada de la API: solo conoce [CharacterRepository].
 * - No sabe cómo se ve la pantalla: solo conoce [CharactersView].
 *
 * Por eso se puede mover a otra plataforma (KMP) cambiando solo esta clase.
 *
 * SIN INYECCIÓN DE DEPENDENCIAS: el repositorio se crea aquí con `=`
 * en lugar de recibirlo por constructor. A cambio de poder sustituirlo en un
 * test, no hace falta ni un framework de DI ni una factoría.
 */
class CharactersController(private val view: CharactersView) {

    private val repository = CharacterRepository()

    // Dispatchers.Main: la View solo se toca desde el hilo principal.
    // SupervisorJob: un fallo en una corrutina no cancela las demás.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var job: Job? = null

    /**
     * Evento de la Vista: "quiero ver los personajes".
     * Se dispara en onStart, por lo que se relanza en cada rotación.
     */
    fun loadCharacters() {
        // Cancela una petición anterior: evita respuestas fuera de orden
        // si el usuario rota el dispositivo mientras carga.
        job?.cancel()
        view.showLoading()

        job = scope.launch {
            try {
                view.showCharacters(repository.getCharacters())
            } catch (e: Exception) {
                // Aquí NO se distingue entre error de red o de servidor:
                // para este challenge la View solo muestra un mensaje genérico.
                view.showError()
            }
        }
    }

    /** El Controller no es un objeto de Android, así que la Activity le avisa del ciclo de vida. */
    fun onDestroy() {
        job?.cancel()
    }
}
