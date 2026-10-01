package com.irv205.rickandmortymvc

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.irv205.rickandmortymvc.controller.CharactersController
import com.irv205.rickandmortymvc.databinding.ActivityMainBinding
import com.irv205.rickandmortymvc.model.Character
import com.irv205.rickandmortymvc.view.CharacterAdapter
import com.irv205.rickandmortymvc.view.CharactersView

/**
 * VISTA — Activity: la pantalla, y a la vez el punto de entrada de la app.
 *
 * Cumple tres papeles a la vez (típico de MVC sin MVP):
 * 1. Es la VISTA: implementa [CharactersView] y sabe pintar los tres estados.
 * 2. Es la VISTA de nuevo: detecta el evento de usuario y lo delega.
 * 3. Es el composition root: donde se conectan Model, View y Controller.
 *
 * Sobre el punto 3: aquí se hace `CharactersController(this)`, el equivalente a
 * inyección de dependencias pero "a mano", sin framework. La Activity es la
 * única que conoce a todos; el Controller solo conoce interfaces.
 *
 * `lifecycleScope` se pasa al Adapter para que las descargas de imágenes se
 * cancelen solas al destruir la pantalla (evita fuga de memoria).
 */
class MainActivity : AppCompatActivity(), CharactersView {

    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: CharactersController
    private lateinit var adapter: CharacterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge-to-edge: el contenido va hasta el borde, se compensa con padding.
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = CharacterAdapter(lifecycleScope)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Composition root: se pasa `this` porque la Activity es la CharactersView.
        controller = CharactersController(this)
    }

    /**
     * onStart y no onCreate a propósito: al rotar el dispositivo se vuelve a
     * pedir la lista en vez de depender de un estado guardado.
     */
    override fun onStart() {
        super.onStart()
        controller.loadCharacters()
    }

    /** El Controller no es un `Component`, así que hay que avisarle del destroy. */
    override fun onDestroy() {
        controller.onDestroy()
        super.onDestroy()
    }

    // --- Implementación de CharactersView: solo pintar, nunca pedir datos ---

    override fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE
    }

    override fun showCharacters(characters: List<Character>) {
        binding.progressBar.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        adapter.setCharacters(characters)
    }

    override fun showError() {
        binding.progressBar.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
    }
}
