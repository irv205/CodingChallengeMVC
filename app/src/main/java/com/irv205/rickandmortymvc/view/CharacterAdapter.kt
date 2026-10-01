package com.irv205.rickandmortymvc.view

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irv205.rickandmortymvc.R
import com.irv205.rickandmortymvc.databinding.ItemCharacterBinding
import com.irv205.rickandmortymvc.model.Character
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * VISTA — Adapter de la lista de personajes.
 *
 * Es parte de la Vista, no del Model: solo pinta lo que le dan. No pide datos
 * a nadie y no sabe de qué vienen.
 *
 * DETALLE: la descarga de la imagen usa OkHttp y NO Retrofit. Retrofit está
 * pensado para JSON; aquí no hay nada que parsear, solo bytes. OkHttp ya viene
 * como dependencia transitiva de Retrofit, así que no cuesta nada.
 * (En un proyecto real se usaría Coil o Glide para esto.)
 */
class CharacterAdapter(private val scope: CoroutineScope) :
    RecyclerView.Adapter<CharacterAdapter.ViewHolder>() {

    private var characters: List<Character> = emptyList()
    private var loadJob: Job? = null
    private val client = OkHttpClient()

    /** La Activity avisa: "estos son los datos, repinta". */
    fun setCharacters(characters: List<Character>) {
        this.characters = characters
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCharacterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // El scroll es rápido: cancelar la carga anterior evita decodificar
        // imágenes que ya no se ven y saturar la CPU.
        loadJob?.cancel()
        loadJob = scope.launch {
            holder.bind(characters[position])
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.clear()
    }

    override fun getItemCount(): Int = characters.size

    /**
     * ViewHolder: una instancia por fila visible. Cachea las referencias a las
     * vistas con ViewBinding para no hacer findViewById en cada scroll.
     */
    inner class ViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        suspend fun bind(character: Character) {
            binding.name.text = character.name
            binding.status.text = character.status
            binding.species.text = character.species
            binding.image.setImageResource(R.mipmap.ic_launcher)

            // `?:` sale temprano si la descarga falló: se conserva el placeholder.
            val bytes = withContext(Dispatchers.IO) { download(character.imageUrl) } ?: return
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            binding.image.setImageBitmap(bitmap)
        }

        /** Al reciclar la fila se restaura el placeholder para no arrastrar la imagen anterior. */
        fun clear() {
            binding.image.setImageResource(R.mipmap.ic_launcher)
        }
    }

    /** Descarga binaria. Devuelve null en lugar de propagar: una foto rota no debe romper la lista. */
    private fun download(url: String): ByteArray? {
        return try {
            client.newCall(Request.Builder().url(url).build()).execute().use { response ->
                if (response.isSuccessful) response.body?.bytes() else null
            }
        } catch (e: Exception) {
            null
        }
    }
}
