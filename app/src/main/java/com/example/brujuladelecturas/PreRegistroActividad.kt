package com.example.brujuladelecturas // Reemplaza con tu paquete

import android.content.Intent
import android.os.Bundle
import android.widget.* // Importa Button, CheckBox, EditText, RadioGroup, TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PreRegistroGustosActivity : AppCompatActivity() {

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // UI Elements - Pregunta 1: Géneros
    private lateinit var cbGeneroFantasia: CheckBox
    private lateinit var cbGeneroCienciaFiccion: CheckBox
    private lateinit var cbGeneroMisterioThriller: CheckBox
    private lateinit var cbGeneroFilosofia: CheckBox
    private lateinit var cbGeneroRomanceDrama: CheckBox
    private lateinit var cbGeneroHistorica: CheckBox
    private lateinit var cbGeneroTerrorSuspenso: CheckBox
    private lateinit var cbGeneroClasicaContemporanea: CheckBox

    // UI Elements - Pregunta 2: Autores
    private lateinit var editTextAutores: EditText

    // UI Elements - Pregunta 3: Libros Favoritos
    private lateinit var editTextLibrosFavoritos: EditText

    // UI Elements - Pregunta 4: Búsqueda principal
    private lateinit var cbBusquedaAventura: CheckBox
    private lateinit var cbBusquedaIntriga: CheckBox
    private lateinit var cbBusquedaDesarrolloPersonal: CheckBox
    private lateinit var cbBusquedaTemasSociales: CheckBox
    private lateinit var cbBusquedaAmorRelaciones: CheckBox
    private lateinit var cbBusquedaHistoriaPolitica: CheckBox
    private lateinit var cbBusquedaMagiaSeres: CheckBox
    private lateinit var cbBusquedaDivulgacion: CheckBox

    // UI Elements - Pregunta 5: Emoción
    private lateinit var cbEmocionAlegria: CheckBox
    private lateinit var cbEmocionReflexion: CheckBox
    private lateinit var cbEmocionSuspenso: CheckBox
    private lateinit var cbEmocionEmocionDrama: CheckBox
    private lateinit var cbEmocionRelajacion: CheckBox
    private lateinit var cbEmocionInspiracion: CheckBox
    private lateinit var cbEmocionSorpresa: CheckBox
    private lateinit var cbEmocionCuriosidad: CheckBox

    // UI Elements - Pregunta 6: Formato
    private lateinit var radioGroupFormato: RadioGroup

    // Botón
    private lateinit var buttonGuardarPreferencias: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_pre_registro_actividad)

        firebaseAuth = Firebase.auth
        firestore = Firebase.firestore

        // Inicializar todas las vistas (¡esto será largo!)
        inicializarVistas()

        buttonGuardarPreferencias.setOnClickListener {
            guardarPreferencias()
        }
    }

    private fun inicializarVistas() {
        // Pregunta 1: Géneros
        cbGeneroFantasia = findViewById(R.id.cbGeneroFantasia)
        cbGeneroCienciaFiccion = findViewById(R.id.cbGeneroCienciaFiccion)
        cbGeneroMisterioThriller = findViewById(R.id.cbGeneroMisterioThriller)
        cbGeneroFilosofia = findViewById(R.id.cbGeneroFilosofia)
        cbGeneroRomanceDrama = findViewById(R.id.cbGeneroRomanceDrama)
        cbGeneroHistorica = findViewById(R.id.cbGeneroHistorica)
        cbGeneroTerrorSuspenso = findViewById(R.id.cbGeneroTerrorSuspenso)
        cbGeneroClasicaContemporanea = findViewById(R.id.cbGeneroClasicaContemporanea)

        // Pregunta 2: Autores
        editTextAutores = findViewById(R.id.editTextAutores) // Asegúrate que el ID en XML sea editTextAutores

        // Pregunta 3: Libros Favoritos
        editTextLibrosFavoritos = findViewById(R.id.editTextLibrosFavoritos) // Asegúrate que el ID en XML sea editTextLibrosFavoritos

        // Pregunta 4: Búsqueda principal
        cbBusquedaAventura = findViewById(R.id.cbBusquedaAventura)
        cbBusquedaIntriga = findViewById(R.id.cbBusquedaIntriga)
        cbBusquedaDesarrolloPersonal = findViewById(R.id.cbBusquedaDesarrolloPersonal)
        cbBusquedaTemasSociales = findViewById(R.id.cbBusquedaTemasSociales)
        cbBusquedaAmorRelaciones = findViewById(R.id.cbBusquedaAmorRelaciones)
        cbBusquedaHistoriaPolitica = findViewById(R.id.cbBusquedaHistoriaPolitica)
        cbBusquedaMagiaSeres = findViewById(R.id.cbBusquedaMagiaSeres)
        cbBusquedaDivulgacion = findViewById(R.id.cbBusquedaDivulgacion)

        // Pregunta 5: Emoción
        cbEmocionAlegria = findViewById(R.id.cbEmocionAlegria)
        cbEmocionReflexion = findViewById(R.id.cbEmocionReflexion)
        cbEmocionSuspenso = findViewById(R.id.cbEmocionSuspenso)
        cbEmocionEmocionDrama = findViewById(R.id.cbEmocionEmocionDrama)
        cbEmocionRelajacion = findViewById(R.id.cbEmocionRelajacion)
        cbEmocionInspiracion = findViewById(R.id.cbEmocionInspiracion)
        cbEmocionSorpresa = findViewById(R.id.cbEmocionSorpresa)
        cbEmocionCuriosidad = findViewById(R.id.cbEmocionCuriosidad)

        // Pregunta 6: Formato
        radioGroupFormato = findViewById(R.id.radioGroupFormato)

        // Botón
        buttonGuardarPreferencias = findViewById(R.id.buttonGuardarPreferencias)
    }

    private fun guardarPreferencias() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Error: Usuario no autenticado.", Toast.LENGTH_LONG).show()
            // Podrías redirigir a la pantalla de login/registro
            // startActivity(Intent(this, RegistroActividad::class.java))
            // finish()
            return
        }

        val userId = currentUser.uid

        // Recolectar Géneros
        val generosSeleccionados = mutableListOf<String>()
        if (cbGeneroFantasia.isChecked) generosSeleccionados.add(cbGeneroFantasia.text.toString())
        if (cbGeneroCienciaFiccion.isChecked) generosSeleccionados.add(cbGeneroCienciaFiccion.text.toString())
        if (cbGeneroMisterioThriller.isChecked) generosSeleccionados.add(cbGeneroMisterioThriller.text.toString())
        if (cbGeneroFilosofia.isChecked) generosSeleccionados.add(cbGeneroFilosofia.text.toString())
        if (cbGeneroRomanceDrama.isChecked) generosSeleccionados.add(cbGeneroRomanceDrama.text.toString())
        if (cbGeneroHistorica.isChecked) generosSeleccionados.add(cbGeneroHistorica.text.toString())
        if (cbGeneroTerrorSuspenso.isChecked) generosSeleccionados.add(cbGeneroTerrorSuspenso.text.toString())
        if (cbGeneroClasicaContemporanea.isChecked) generosSeleccionados.add(cbGeneroClasicaContemporanea.text.toString())

        // Recolectar Autores
        val autores = editTextAutores.text.toString().trim()

        // Recolectar Libros Favoritos
        val librosFavoritos = editTextLibrosFavoritos.text.toString().trim()

        // Recolectar Búsqueda Principal
        val busquedaPrincipalSeleccionada = mutableListOf<String>()
        if (cbBusquedaAventura.isChecked) busquedaPrincipalSeleccionada.add(cbBusquedaAventura.text.toString())
        if (cbBusquedaIntriga.isChecked) busquedaPrincipalSeleccionada.add(cbBusquedaIntriga.text.toString())
        if (cbBusquedaDesarrolloPersonal.isChecked) busquedaPrincipalSeleccionada.add(cbBusquedaDesarrolloPersonal.text.toString())
        if (cbBusquedaTemasSociales.isChecked) busquedaPrincipalSeleccionada.add(cbBusquedaTemasSociales.text.toString())
        if (cbBusquedaAmorRelaciones.isChecked) busquedaPrincipalSeleccionada.add(cbBusquedaAmorRelaciones.text.toString())
        if (cbBusquedaHistoriaPolitica.isChecked) busquedaPrincipalSeleccionada.add(cbBusquedaHistoriaPolitica.text.toString())
        if (cbBusquedaMagiaSeres.isChecked) busquedaPrincipalSeleccionada.add(cbBusquedaMagiaSeres.text.toString())
        if (cbBusquedaDivulgacion.isChecked) busquedaPrincipalSeleccionada.add(cbBusquedaDivulgacion.text.toString())


        // Recolectar Experiencia Emocional
        val experienciaEmocionalSeleccionada = mutableListOf<String>()
        if (cbEmocionAlegria.isChecked) experienciaEmocionalSeleccionada.add(cbEmocionAlegria.text.toString())
        if (cbEmocionReflexion.isChecked) experienciaEmocionalSeleccionada.add(cbEmocionReflexion.text.toString())
        if (cbEmocionSuspenso.isChecked) experienciaEmocionalSeleccionada.add(cbEmocionSuspenso.text.toString())
        if (cbEmocionEmocionDrama.isChecked) experienciaEmocionalSeleccionada.add(cbEmocionEmocionDrama.text.toString())
        if (cbEmocionRelajacion.isChecked) experienciaEmocionalSeleccionada.add(cbEmocionRelajacion.text.toString())
        if (cbEmocionInspiracion.isChecked) experienciaEmocionalSeleccionada.add(cbEmocionInspiracion.text.toString())
        if (cbEmocionSorpresa.isChecked) experienciaEmocionalSeleccionada.add(cbEmocionSorpresa.text.toString())
        if (cbEmocionCuriosidad.isChecked) experienciaEmocionalSeleccionada.add(cbEmocionCuriosidad.text.toString())


        // Recolectar Formato
        val idRadioButtonSeleccionado = radioGroupFormato.checkedRadioButtonId
        var formatoPreferido = ""
        if (idRadioButtonSeleccionado != -1) {
            val radioButtonSeleccionado = findViewById<RadioButton>(idRadioButtonSeleccionado)
            formatoPreferido = radioButtonSeleccionado.text.toString()
        }

        // Crear un objeto (HashMap o una clase de datos) para guardar en Firestore
        val preferenciasUsuario = hashMapOf(
            "generos" to generosSeleccionados,
            "autoresFavoritos" to autores,
            "librosFavoritos" to librosFavoritos,
            "busquedaPrincipal" to busquedaPrincipalSeleccionada,
            "experienciaEmocional" to experienciaEmocionalSeleccionada,
            "formatoLectura" to formatoPreferido,
            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp() // Para saber cuándo se guardó
        )

        // Guardar en Firestore
        // Crearemos una colección "usuarios_preferencias" y cada documento tendrá el ID del usuario
        firestore.collection("usuarios_preferencias").document(userId)
            .set(preferenciasUsuario)
            .addOnSuccessListener {
                Toast.makeText(this, "Preferencias guardadas con éxito", Toast.LENGTH_SHORT).show()
                // Navegar a la pantalla principal
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar preferencias: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}