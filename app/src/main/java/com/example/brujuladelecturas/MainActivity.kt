package com.example.brujuladelecturas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu // Importar para el menú
import android.view.MenuItem // Importar para el menú
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog // Importar para AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.brujuladelecturas.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
// import com.google.android.material.bottomnavigation.BottomNavigationView // YA NO SE USA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth // Importado para Firebase Auth KTX
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import android.widget.ProgressBar


class MainActivity : AppCompatActivity() {

    private lateinit var toolbarMain: Toolbar
    private lateinit var viewPagerRecomendaciones: ViewPager2
    // private lateinit var bottomNavigationView: BottomNavigationView // ELIMINADO

    private lateinit var recomendacionesAdapter: RecomendacionesViewPagerAdapter
    private var listaDeLibros: MutableList<LibroRecomendado> = mutableListOf()

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var generativeModel: GenerativeModel? = null // Hacer nullable y comprobar inicialización

    // NUEVOS BOTONES
    private lateinit var buttonGenerarRecomendaciones: Button // Renombrado para consistencia con layout
    private lateinit var buttonEditarPreferencias: Button

    private lateinit var progressBarMain: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbarMain = findViewById(R.id.toolbarMain) //
        setSupportActionBar(toolbarMain)
        supportActionBar?.title = "Brújula de Lecturas" // Título de la App

        viewPagerRecomendaciones = findViewById(R.id.viewPagerRecomendaciones) //
        recomendacionesAdapter = RecomendacionesViewPagerAdapter(listaDeLibros) //
        viewPagerRecomendaciones.adapter = recomendacionesAdapter

        progressBarMain = findViewById(R.id.progressBarMain) // INICIALIZAR PROGRESSBAR
        buttonGenerarRecomendaciones = findViewById(R.id.buttonGenerarRecomendaciones)
        buttonEditarPreferencias = findViewById(R.id.buttonEditarPreferencias)

        firebaseAuth = Firebase.auth // Usar KTX
        firestore = Firebase.firestore

        viewPagerRecomendaciones.offscreenPageLimit = 3

        try {
            generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash-latest",
                apiKey = BuildConfig.GEMINI_API_KEY
            )
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al inicializar GenerativeModel: ${e.message}", e)
            Toast.makeText(this, "Error al inicializar el servicio de IA.", Toast.LENGTH_LONG).show()
            // Considerar deshabilitar la funcionalidad de Gemini si falla la inicialización
        }

        // Configuración del ViewPager2 (opcional, la tenías antes)
        viewPagerRecomendaciones.offscreenPageLimit = 3
        viewPagerRecomendaciones.setPageTransformer { page, position ->
            val R = 0.70f // Escala mínima de las páginas adyacentes
            val S = 0.30f // Cantidad de escala a añadir para la página central (1.0f - R)

            val absPosition = Math.abs(position)

            // Aplicar escala y alfa
            val scale = R + (1 - absPosition) * S
            page.scaleX = scale
            page.scaleY = scale
            page.alpha = R + (1 - absPosition) * S
        }

        // Inicializar y configurar los nuevos botones
        buttonGenerarRecomendaciones = findViewById(R.id.buttonGenerarRecomendaciones) // ID del nuevo layout
        buttonEditarPreferencias = findViewById(R.id.buttonEditarPreferencias)     // ID del nuevo layout

        buttonGenerarRecomendaciones.setOnClickListener {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                if (generativeModel != null) { // Comprobar si Gemini se inicializó
                    solicitarNuevasRecomendacionesDesdeAPI(currentUser.uid)
                } else {
                    Toast.makeText(this, "Servicio de IA no disponible.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Debes iniciar sesión para buscar nuevas recomendaciones.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonEditarPreferencias.setOnClickListener {
            val intent = Intent(this, PreRegistroStepperActivity::class.java) // O como hayas llamado a tu Activity de Stepper
            intent.putExtra("IS_EDIT_MODE", true) // Indicar que es modo edición
            startActivity(intent)
        }

        obtenerYMostrarRecomendaciones()
    }

    // --- Menú de Opciones en la Toolbar ---
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // Asegúrate de tener main_menu.xml en res/menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_ver_correo -> {
                mostrarDialogoCorreoUsuario()
                true
            }
            R.id.action_cerrar_sesion -> {
                cerrarSesion()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarDialogoCorreoUsuario() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            AlertDialog.Builder(this)
                .setTitle("Correo Electrónico")
                .setMessage("Tu correo es: ${user.email}")
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            Toast.makeText(this, "No hay usuario autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cerrarSesion() {
        firebaseAuth.signOut()
        val intent = Intent(this, EntryActivity::class.java) // Ir a la pantalla de entrada
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun actualizarUIConRecomendaciones(libros: List<LibroRecomendado>) {
        listaDeLibros.clear()
        if (libros.isNotEmpty()) {
            listaDeLibros.addAll(libros)
            viewPagerRecomendaciones.visibility = View.VISIBLE // Asegurar que el ViewPager sea visible
        } else {
            Log.d("MainActivity", "No se recibieron libros para mostrar (UI).")
            viewPagerRecomendaciones.visibility = View.GONE // Ocultar si no hay nada
        }
        recomendacionesAdapter.actualizarLibros(listaDeLibros) //
    }

    private fun solicitarNuevasRecomendacionesDesdeAPI(userId: String) {
        if (generativeModel == null) { // Nueva comprobación
            Toast.makeText(this, "El servicio de recomendaciones no está disponible en este momento.", Toast.LENGTH_LONG).show()
            return
        }
        Log.d("MainActivity", "Solicitando NUEVAS recomendaciones desde API para $userId...")

        // MOSTRAR PROGRESSBAR Y DESHABILITAR BOTÓN
        progressBarMain.visibility = View.VISIBLE
        buttonGenerarRecomendaciones.isEnabled = false
        viewPagerRecomendaciones.visibility = View.INVISIBLE // Opcional: ocultar mientras carga

        CoroutineScope(Dispatchers.Main).launch {
            try {

                val preferencias = obtenerPreferenciasUsuario(userId)
                if (preferencias != null) {
                    val prompt = construirPromptParaGemini(preferencias)
                    Log.d("MainActivity", "Prompt para Gemini (NUEVAS): $prompt")
                    val respuestaGemini = llamarAPIGemini(prompt)

                    if (respuestaGemini != null && respuestaGemini.text != null) {
                        val librosRecomendados = parsearRespuestaGemini(respuestaGemini.text!!)
                        actualizarUIConRecomendaciones(librosRecomendados)
                        if (librosRecomendados.isNotEmpty()) {
                            guardarRecomendacionesEnFirestore(userId, librosRecomendados)
                        } else {
                            Toast.makeText(this@MainActivity, "No se encontraron nuevas recomendaciones que coincidan. Intenta ajustar tus preferencias.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Log.e("MainActivity", "La respuesta de Gemini fue nula o no contiene texto al solicitar NUEVAS.")
                        Toast.makeText(this@MainActivity, "Error al obtener nuevas recomendaciones de la IA.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.d("MainActivity", "No se encontraron preferencias para el usuario $userId al solicitar NUEVAS.")
                    Toast.makeText(this@MainActivity, "Completa tus preferencias para buscar recomendaciones.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                val tag = "MainActivity"
                val errorMessage = "Error al solicitar NUEVAS recomendaciones (catch general): ${e.message ?: "No message"}"
                Log.e(tag, errorMessage, e)
                Toast.makeText(this@MainActivity, "Error al buscar nuevas recomendaciones.", Toast.LENGTH_LONG).show()
            } finally {
                buttonGenerarRecomendaciones.isEnabled = true
                // OCULTAR PROGRESSBAR Y HABILITAR BOTÓN
                progressBarMain.visibility = View.GONE
                buttonGenerarRecomendaciones.isEnabled = true
            }
        }
    }

    private fun obtenerYMostrarRecomendaciones() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Log.d("MainActivity", "Usuario no logueado.")
            actualizarUIConRecomendaciones(emptyList()) // Limpiar UI
            val intent = Intent(this, EntryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        val userId = currentUser.uid
        Log.d("MainActivity", "Obteniendo y mostrando recomendaciones para $userId...")

        progressBarMain.visibility = View.VISIBLE
        buttonGenerarRecomendaciones.isEnabled = false // También deshabilitar el botón de generar nuevas
        viewPagerRecomendaciones.visibility = View.INVISIBLE // Ocultar mientras carga

        CoroutineScope(Dispatchers.Main).launch {

            val recomendacionesGuardadas = cargarRecomendacionesGuardadas(userId)

            if (recomendacionesGuardadas != null && recomendacionesGuardadas.isNotEmpty()) {
                Log.d("MainActivity", "Mostrando recomendaciones guardadas.")
                actualizarUIConRecomendaciones(recomendacionesGuardadas)
            } else {
                Log.d("MainActivity", "No hay recomendaciones guardadas válidas. Obteniendo iniciales de Gemini...")
                if (generativeModel != null) { // Comprobar si Gemini se inicializó
                    solicitarNuevasRecomendacionesDesdeAPI(userId)
                } else {
                    Toast.makeText(this@MainActivity, "Servicio de IA no disponible para carga inicial.", Toast.LENGTH_LONG).show()
                    actualizarUIConRecomendaciones(emptyList()) // Mostrar UI vacía si no hay IA
                }
            }
            // OCULTAR PROGRESSBAR Y HABILITAR BOTÓN (después de carga inicial)
            // La visibilidad del ProgressBar y la habilitación del botón en la carga inicial
            // se manejan mejor dentro de solicitarNuevasRecomendacionesDesdeAPI si esta se llama,
            // o aquí mismo si se cargan desde caché.
            if (recomendacionesGuardadas == null || recomendacionesGuardadas.isEmpty() && generativeModel == null){
                // Si no hay caché Y no hay IA, el progressBar y el botón deben volver a su estado normal.
                progressBarMain.visibility = View.GONE
                buttonGenerarRecomendaciones.isEnabled = true
                if (listaDeLibros.isEmpty()) viewPagerRecomendaciones.visibility = View.GONE // Si realmente no hay nada
            } else if (recomendacionesGuardadas != null && recomendacionesGuardadas.isNotEmpty()){
                // Si se cargó desde caché, el progressBar y el botón deben volver a su estado normal aquí.
                progressBarMain.visibility = View.GONE
                buttonGenerarRecomendaciones.isEnabled = true
            }
            // Si se llamó a solicitarNuevasRecomendacionesDesdeAPI, esa función se encarga del progressBar y el botón en su 'finally'.
        }
    }

    private suspend fun obtenerPreferenciasUsuario(userId: String): Map<String, Any>? {
        return try {
            val documentSnapshot = withContext(Dispatchers.IO) {
                firestore.collection("usuarios_preferencias")
                    .document(userId)
                    .get()
                    .await()
            }
            if (documentSnapshot.exists()) {
                Log.d("MainActivity", "Preferencias encontradas para $userId: ${documentSnapshot.data}")
                documentSnapshot.data
            } else {
                Log.d("MainActivity", "No se encontró documento de preferencias para $userId.")
                null
            }
        } catch (e: Exception) {
            val tag = "MainActivity"
            val errorMessage = "Error al obtener preferencias $userId: ${e.message ?: "No message"}"
            Log.e(tag, errorMessage, e)
            null
        }
    }

    private fun construirPromptParaGemini(preferencias: Map<String, Any>): String {
        val generos = (preferencias["generos"] as? List<*>)?.joinToString(", ") ?: "No especificados"
        val autores = preferencias["autoresFavoritos"] as? String ?: "No especificados"
        val librosFav = preferencias["librosFavoritos"] as? String ?: "No especificados"
        val busqueda = (preferencias["busquedaPrincipal"] as? List<*>)?.joinToString(", ") ?: "No especificada"
        val emociones = (preferencias["experienciaEmocional"] as? List<*>)?.joinToString(", ") ?: "No especificadas"
        val formato = preferencias["formatoLectura"] as? String ?: "No especificado"

        // Prompt actualizado para que NO pida palabras clave
        return """
        Eres un experto recomendador de libros llamado 'Brújula de Lecturas'. Basándote en las siguientes preferencias de un usuario, por favor sugiere EXACTAMENTE 3 libros. Para cada libro, proporciona: un título, el autor, una cadena de EXACTAMENTE 5 emojis que representen el libro temáticamente (sin espacios entre ellos, solo los 5 emojis juntos) y una sinopsis muy breve (1 o 2 frases concisas y atractivas).
        Formatea tu respuesta completa como un único objeto JSON. Este objeto JSON debe contener una lista llamada 'recomendaciones'. Cada elemento en esta lista será un objeto JSON representando un libro con las siguientes claves: 'id' (un identificador único para el libro que puedes generar, ej: 'libro_1'), 'titulo', 'autor', 'emojis', y 'sinopsis'. NO incluyas 'palabras_clave'.

        Preferencias del usuario:
        - Géneros favoritos: $generos
        - Autores favoritos: $autores
        - Libros favoritos: $librosFav
        - El usuario busca principalmente en un libro: $busqueda
        - El usuario busca las siguientes experiencias emocionales: $emociones
        - Formato de lectura preferido: $formato

        Por favor, asegúrate de que la respuesta sea solo el objeto JSON y nada más, y que cada libro contenga solo los campos solicitados.
        """.trimIndent()
    }


    private suspend fun llamarAPIGemini(prompt: String): GenerateContentResponse? {
        if (generativeModel == null) { // Comprobar si fue inicializada
            Log.e("MainActivity", "GenerativeModel no está inicializado. Verifique la API Key o errores previos.")
            return null
        }
        return try {
            withContext(Dispatchers.IO) {
                Log.d("MainActivity", "Enviando prompt a Gemini...")
                val response = generativeModel!!.generateContent(prompt) // Usar !! porque ya comprobamos
                Log.d("MainActivity", "Respuesta recibida de Gemini.")
                response
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Excepción al llamar a la API de Gemini: ${e.message}", e)
            null
        }
    }

    // Modificar parsearRespuestaGemini para que coincida con el nuevo LibroRecomendado y el prompt sin palabras_clave
    private fun parsearRespuestaGemini(respuestaJsonString: String): List<LibroRecomendado> {
        val librosRecomendados = mutableListOf<LibroRecomendado>()
        Log.d("MainActivity", "Intentando parsear JSON: $respuestaJsonString")
        try {
            var jsonStringLimpio = respuestaJsonString.trim()
            if (jsonStringLimpio.startsWith("```json")) {
                jsonStringLimpio = jsonStringLimpio.substring(7)
            }
            if (jsonStringLimpio.endsWith("```")) {
                jsonStringLimpio = jsonStringLimpio.substring(0, jsonStringLimpio.length - 3)
            }
            jsonStringLimpio = jsonStringLimpio.trim()
            Log.d("MainActivity", "JSON Limpio para parsear: $jsonStringLimpio")

            val jsonObjectPrincipal = JSONObject(jsonStringLimpio)
            val recomendacionesArray = jsonObjectPrincipal.getJSONArray("recomendaciones")

            for (i in 0 until recomendacionesArray.length()) {
                val libroJson = recomendacionesArray.getJSONObject(i)
                val id = libroJson.optString("id", "id_desconocido_${System.currentTimeMillis()}_$i")
                val titulo = libroJson.getString("titulo")
                val autor = libroJson.getString("autor")
                val emojis = libroJson.optString("emojis", "📖✨🌟💡🎉") // Placeholder si no viene
                val sinopsis = libroJson.getString("sinopsis") // 'sinopsis' según el nuevo prompt

                librosRecomendados.add(
                    LibroRecomendado( // Asegúrate que coincida con tu LibroRecomendado.kt
                        id = id,
                        titulo = titulo,
                        autor = autor,
                        emojisRepresentativos = emojis,
                        sinopsisBreve = sinopsis // Mapear a sinopsisBreve
                        // Ya no se esperan palabrasClave
                    )
                )
            }
            Log.d("MainActivity", "Parseo de JSON exitoso. Libros encontrados: ${librosRecomendados.size}")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al parsear la respuesta JSON de Gemini: ${e.message}", e)
            Toast.makeText(this, "Error al procesar recomendaciones de la IA.", Toast.LENGTH_LONG).show()
        }
        return librosRecomendados
    }

    private suspend fun guardarRecomendacionesEnFirestore(userId: String, libros: List<LibroRecomendado>) {
        if (libros.isEmpty()) {
            Log.d("MainActivity", "No hay recomendaciones para guardar.")
            return
        }
        // Asegúrate de que los nombres de campo aquí coincidan con cómo los lees en cargarRecomendacionesGuardadas
        // y con la estructura de LibroRecomendado.kt
        val librosParaFirestore = libros.map { libro ->
            mapOf(
                "id" to libro.id,
                "titulo" to libro.titulo,
                "autor" to libro.autor,
                "emojisRepresentativos" to libro.emojisRepresentativos,
                "sinopsisBreve" to libro.sinopsisBreve // Coincidir con LibroRecomendado.kt
                // "palabrasClave" ya no se guarda
            )
        }

        val datosParaGuardar = mapOf(
            "recomendacionesCacheadas" to librosParaFirestore,
            "fechaRecomendacionesCacheadas" to FieldValue.serverTimestamp()
        )
        try {
            withContext(Dispatchers.IO) {
                firestore.collection("usuarios_preferencias")
                    .document(userId)
                    .update(datosParaGuardar) // .set(datosParaGuardar, SetOptions.merge()) sería más seguro si el doc no existe
                    .await()
                Log.d("MainActivity", "Recomendaciones guardadas en Firestore para $userId.")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al guardar recomendaciones en Firestore para $userId: ${e.message}", e)
            Toast.makeText(this@MainActivity, "Error al guardar las recomendaciones.", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun cargarRecomendacionesGuardadas(userId: String): List<LibroRecomendado>? {
        Log.d("MainActivity", "Intentando cargar recomendaciones guardadas para $userId...")
        return try {
            val documentSnapshot = withContext(Dispatchers.IO) {
                firestore.collection("usuarios_preferencias")
                    .document(userId)
                    .get()
                    .await()
            }
            if (documentSnapshot.exists()) {
                @Suppress("UNCHECKED_CAST")
                val recomendacionesRaw = documentSnapshot.get("recomendacionesCacheadas") as? List<HashMap<String, Any>>
                if (recomendacionesRaw != null) {
                    val libros = recomendacionesRaw.mapNotNull { map ->
                        try {
                            LibroRecomendado(
                                id = map["id"] as String,
                                titulo = map["titulo"] as String,
                                autor = map["autor"] as String,
                                emojisRepresentativos = map["emojisRepresentativos"] as? String,
                                sinopsisBreve = map["sinopsisBreve"] as String, // Coincidir con LibroRecomendado.kt
                                // palabrasClave = map["palabrasClave"] as? List<String> // Ya no se carga
                            )
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Error al mapear una recomendación guardada: $map", e)
                            null
                        }
                    }
                    if (libros.isNotEmpty()) {
                        Log.d("MainActivity", "Recomendaciones guardadas cargadas exitosamente para $userId: ${libros.size} libros.")
                        return libros
                    } else {
                        Log.d("MainActivity", "El campo 'recomendacionesCacheadas' estaba vacío o todos los libros estaban malformados.")
                    }
                } else {
                    Log.d("MainActivity", "No se encontró el campo 'recomendacionesCacheadas' para $userId.")
                }
            } else {
                Log.d("MainActivity", "No se encontró el documento de preferencias para $userId.")
            }
            null
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al cargar recomendaciones guardadas de Firestore para $userId: ${e.message}", e)
            null
        }
    }
}