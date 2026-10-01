package com.irv205.rickandmortymvc.view

import com.irv205.rickandmortymvc.model.Character

/**
 * VISTA — Contrato de lo que la Vista sabe hacer.
 *
 * En MVC la Vista es *pasiva*: no pide datos, solo sabe pintar. El Controller
 * es quien la invoca. Esta interfaz es lo que permite que el Controller hable
 * con la Activity sin importarla ni conocerla.
 *
 * Cada método corresponde a un estado visual distinto de la pantalla:
 * cargando, con datos, o con error.
 */
interface CharactersView {
    fun showLoading()
    fun showCharacters(characters: List<Character>)
    fun showError()
}
