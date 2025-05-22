package com.example.brujuladelecturas // Asegúrate que coincida con tu paquete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log // Para depuración

class PreRegistroViewModel : ViewModel() {

    // Página 1: Géneros Literarios
    private val _generosSeleccionados = MutableLiveData<MutableSet<String>>(mutableSetOf())
    val generosSeleccionados: LiveData<MutableSet<String>> = _generosSeleccionados

    fun setGeneroSeleccionado(genero: String, isChecked: Boolean) {
        val setActual = _generosSeleccionados.value ?: mutableSetOf()
        if (isChecked) {
            setActual.add(genero)
        } else {
            setActual.remove(genero)
        }
        _generosSeleccionados.value = setActual
    }

    // Página 2: Autores Favoritos
    private val _autor1 = MutableLiveData<String>("")
    val autor1: LiveData<String> = _autor1
    fun setAutor1(autor: String) { _autor1.value = autor }

    private val _autor2 = MutableLiveData<String>("")
    val autor2: LiveData<String> = _autor2
    fun setAutor2(autor: String) { _autor2.value = autor }

    private val _autor3 = MutableLiveData<String>("")
    val autor3: LiveData<String> = _autor3
    fun setAutor3(autor: String) { _autor3.value = autor }

    // Página 3: Libros Favoritos
    private val _libro1 = MutableLiveData<String>("")
    val libro1: LiveData<String> = _libro1
    fun setLibro1(libro: String) { _libro1.value = libro }

    private val _libro2 = MutableLiveData<String>("")
    val libro2: LiveData<String> = _libro2
    fun setLibro2(libro: String) { _libro2.value = libro }

    private val _libro3 = MutableLiveData<String>("")
    val libro3: LiveData<String> = _libro3
    fun setLibro3(libro: String) { _libro3.value = libro }

    // Página 4: Qué Busca Principalmente en un Libro
    private val _busquedaPrincipalSeleccionada = MutableLiveData<MutableSet<String>>(mutableSetOf())
    val busquedaPrincipalSeleccionada: LiveData<MutableSet<String>> = _busquedaPrincipalSeleccionada

    fun setBusquedaPrincipal(item: String, isChecked: Boolean) {
        val setActual = _busquedaPrincipalSeleccionada.value ?: mutableSetOf()
        if (isChecked) {
            setActual.add(item)
        } else {
            setActual.remove(item)
        }
        _busquedaPrincipalSeleccionada.value = setActual
    }

    // Página 5: Experiencias Emocionales Buscadas
    private val _experienciaEmocionalSeleccionada = MutableLiveData<MutableSet<String>>(mutableSetOf())
    val experienciaEmocionalSeleccionada: LiveData<MutableSet<String>> = _experienciaEmocionalSeleccionada

    fun setExperienciaEmocional(item: String, isChecked: Boolean) {
        val setActual = _experienciaEmocionalSeleccionada.value ?: mutableSetOf()
        if (isChecked) {
            setActual.add(item)
        } else {
            setActual.remove(item)
        }
        _experienciaEmocionalSeleccionada.value = setActual
    }

    // Página 6: Formato de Lectura Preferido
    private val _formatoLectura = MutableLiveData<String>("")
    val formatoLectura: LiveData<String> = _formatoLectura
    fun setFormatoLectura(formato: String) { _formatoLectura.value = formato }


    fun obtenerPreferenciasParaFirestore(): HashMap<String, Any> {
        val autoresFavoritos = listOf(autor1.value, autor2.value, autor3.value)
            .mapNotNull { it?.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(", ")

        val librosFavoritos = listOf(libro1.value, libro2.value, libro3.value)
            .mapNotNull { it?.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(", ")

        return hashMapOf(
            "generos" to (_generosSeleccionados.value?.toList() ?: listOf<String>()),
            "autoresFavoritos" to autoresFavoritos,
            "librosFavoritos" to librosFavoritos,
            "busquedaPrincipal" to (_busquedaPrincipalSeleccionada.value?.toList() ?: listOf<String>()),
            "experienciaEmocional" to (_experienciaEmocionalSeleccionada.value?.toList() ?: listOf<String>()),
            "formatoLectura" to (formatoLectura.value ?: "")
        )
    }

    // NUEVO MÉTODO para cargar datos en modo edición
    fun cargarPreferenciasExistentes(datos: Map<String, Any>) {
        Log.d("PreRegistroViewModel", "Cargando preferencias existentes: $datos")

        // Cargar Géneros
        (datos["generos"] as? List<*>)?.let { listaGeneros ->
            _generosSeleccionados.value = listaGeneros.mapNotNull { it.toString() }.toMutableSet()
        }

        // Cargar Autores
        // Asumiendo que "autoresFavoritos" se guarda como un solo String separado por comas
        (datos["autoresFavoritos"] as? String)?.let { autoresString ->
            val listaAutores = autoresString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            if (listaAutores.isNotEmpty()) _autor1.value = listaAutores.getOrElse(0) { "" }
            if (listaAutores.size > 1) _autor2.value = listaAutores.getOrElse(1) { "" }
            if (listaAutores.size > 2) _autor3.value = listaAutores.getOrElse(2) { "" }
        }

        // Cargar Libros
        // Asumiendo que "librosFavoritos" se guarda como un solo String separado por comas
        (datos["librosFavoritos"] as? String)?.let { librosString ->
            val listaLibros = librosString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            if (listaLibros.isNotEmpty()) _libro1.value = listaLibros.getOrElse(0) { "" }
            if (listaLibros.size > 1) _libro2.value = listaLibros.getOrElse(1) { "" }
            if (listaLibros.size > 2) _libro3.value = listaLibros.getOrElse(2) { "" }
        }

        // Cargar Búsqueda Principal
        (datos["busquedaPrincipal"] as? List<*>)?.let { listaBusqueda ->
            _busquedaPrincipalSeleccionada.value = listaBusqueda.mapNotNull { it.toString() }.toMutableSet()
        }

        // Cargar Experiencia Emocional
        (datos["experienciaEmocional"] as? List<*>)?.let { listaExperiencia ->
            _experienciaEmocionalSeleccionada.value = listaExperiencia.mapNotNull { it.toString() }.toMutableSet()
        }

        // Cargar Formato de Lectura
        (datos["formatoLectura"] as? String)?.let {
            _formatoLectura.value = it
        }
    }
}