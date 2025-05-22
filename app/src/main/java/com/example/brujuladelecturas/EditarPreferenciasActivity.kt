package com.example.brujuladelecturas

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar // Necesario para la Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditarPreferenciasActivity : AppCompatActivity() {

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var userId: String? = null

    // UI Elements - Toolbar
    private lateinit var toolbarEditarPreferencias: Toolbar

    // UI Elements - Pregunta 1: Géneros
    private lateinit var cbGeneroFantasia: CheckBox
    private lateinit var cbGeneroCienciaFiccion: CheckBox
    private lateinit var cbGeneroMisterioThriller: CheckBox
    private lateinit var cbGeneroFilosofia: CheckBox
    private lateinit var cbGeneroRomanceDrama: CheckBox
    private lateinit var cbGeneroHistorica: CheckBox
    private lateinit var cbGeneroTerrorSuspenso: CheckBox
    private lateinit var cbGeneroClasicaContemporanea: CheckBox

    // UI Elements - Pregunta 2: Autores (Modificado)
    private lateinit var editTextAutor1: EditText
    private lateinit var editTextAutor2: EditText
    private lateinit var editTextAutor3: EditText

    // UI Elements - Pregunta 3: Libros Favoritos del Formulario (Modificado)
    private lateinit var editTextLibro1: EditText
    private lateinit var editTextLibro2: EditText
    private lateinit var editTextLibro3: EditText

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
    private lateinit var radioButtonFisico: RadioButton
    private lateinit var radioButtonDigital: RadioButton
    private lateinit var radioButtonAmbosFormatos: RadioButton


    // Botón
    private lateinit var buttonGuardarCambios: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_preferencias) // Asegúrate que este sea el layout correcto

        toolbarEditarPreferencias = findViewById(R.id.toolbarEditarPreferencias)
        setSupportActionBar(toolbarEditarPreferencias)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Error: Usuario no autenticado. Por favor, inicie sesión de nuevo.", Toast.LENGTH_LONG).show()
            // Considera redirigir al login o cerrar esta actividad
            // Ejemplo: startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        inicializarVistas()
        cargarPreferenciasActuales()

        buttonGuardarCambios.setOnClickListener {
            guardarPreferenciasActualizadas()
        }
    }

    private fun inicializarVistas() {
        // Géneros
        cbGeneroFantasia = findViewById(R.id.cbGeneroFantasia)
        cbGeneroCienciaFiccion = findViewById(R.id.cbGeneroCienciaFiccion)
        cbGeneroMisterioThriller = findViewById(R.id.cbGeneroMisterioThriller)
        cbGeneroFilosofia = findViewById(R.id.cbGeneroFilosofia)
        cbGeneroRomanceDrama = findViewById(R.id.cbGeneroRomanceDrama)
        cbGeneroHistorica = findViewById(R.id.cbGeneroHistorica)
        cbGeneroTerrorSuspenso = findViewById(R.id.cbGeneroTerrorSuspenso)
        cbGeneroClasicaContemporanea = findViewById(R.id.cbGeneroClasicaContemporanea)

        // Autores
        editTextAutor1 = findViewById(R.id.editTextAutor1)
        editTextAutor2 = findViewById(R.id.editTextAutor2)
        editTextAutor3 = findViewById(R.id.editTextAutor3)

        // Libros Favoritos (del formulario)
        editTextLibro1 = findViewById(R.id.editTextLibro1)
        editTextLibro2 = findViewById(R.id.editTextLibro2)
        editTextLibro3 = findViewById(R.id.editTextLibro3)

        // Búsqueda Principal
        cbBusquedaAventura = findViewById(R.id.cbBusquedaAventura)
        cbBusquedaIntriga = findViewById(R.id.cbBusquedaIntriga)
        cbBusquedaDesarrolloPersonal = findViewById(R.id.cbBusquedaDesarrolloPersonal)
        cbBusquedaTemasSociales = findViewById(R.id.cbBusquedaTemasSociales)
        cbBusquedaAmorRelaciones = findViewById(R.id.cbBusquedaAmorRelaciones)
        cbBusquedaHistoriaPolitica = findViewById(R.id.cbBusquedaHistoriaPolitica)
        cbBusquedaMagiaSeres = findViewById(R.id.cbBusquedaMagiaSeres)
        cbBusquedaDivulgacion = findViewById(R.id.cbBusquedaDivulgacion)

        // Emoción
        cbEmocionAlegria = findViewById(R.id.cbEmocionAlegria)
        cbEmocionReflexion = findViewById(R.id.cbEmocionReflexion)
        cbEmocionSuspenso = findViewById(R.id.cbEmocionSuspenso)
        cbEmocionEmocionDrama = findViewById(R.id.cbEmocionEmocionDrama)
        cbEmocionRelajacion = findViewById(R.id.cbEmocionRelajacion)
        cbEmocionInspiracion = findViewById(R.id.cbEmocionInspiracion)
        cbEmocionSorpresa = findViewById(R.id.cbEmocionSorpresa)
        cbEmocionCuriosidad = findViewById(R.id.cbEmocionCuriosidad)

        // Formato
        radioGroupFormato = findViewById(R.id.radioGroupFormato)
        radioButtonFisico = findViewById(R.id.radioButtonFisico)
        radioButtonDigital = findViewById(R.id.radioButtonDigital)
        radioButtonAmbosFormatos = findViewById(R.id.radioButtonAmbosFormatos)

        // Botón
        buttonGuardarCambios = findViewById(R.id.buttonGuardarCambios)
    }

    private fun cargarPreferenciasActuales() {
        userId?.let { uid ->
            firestore.collection("usuarios_preferencias").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val data = document.data

                        // Poblar Géneros
                        val generosGuardados = data?.get("generos") as? List<String>
                        generosGuardados?.forEach { genero ->
                            when (genero) {
                                cbGeneroFantasia.text.toString() -> cbGeneroFantasia.isChecked = true
                                cbGeneroCienciaFiccion.text.toString() -> cbGeneroCienciaFiccion.isChecked = true
                                cbGeneroMisterioThriller.text.toString() -> cbGeneroMisterioThriller.isChecked = true
                                cbGeneroFilosofia.text.toString() -> cbGeneroFilosofia.isChecked = true
                                cbGeneroRomanceDrama.text.toString() -> cbGeneroRomanceDrama.isChecked = true
                                cbGeneroHistorica.text.toString() -> cbGeneroHistorica.isChecked = true
                                cbGeneroTerrorSuspenso.text.toString() -> cbGeneroTerrorSuspenso.isChecked = true
                                cbGeneroClasicaContemporanea.text.toString() -> cbGeneroClasicaContemporanea.isChecked = true
                            }
                        }

                        // Poblar Autores
                        val autoresGuardados = data?.get("autoresFavoritos") as? List<String>
                        autoresGuardados?.let {
                            if (it.isNotEmpty()) editTextAutor1.setText(it[0])
                            if (it.size > 1) editTextAutor2.setText(it[1])
                            if (it.size > 2) editTextAutor3.setText(it[2])
                        }

                        // Poblar Libros Favoritos (del formulario)
                        val librosFormularioGuardados = data?.get("librosFavoritos") as? List<String>
                        librosFormularioGuardados?.let {
                            if (it.isNotEmpty()) editTextLibro1.setText(it[0])
                            if (it.size > 1) editTextLibro2.setText(it[1])
                            if (it.size > 2) editTextLibro3.setText(it[2])
                        }

                        // Poblar Búsqueda Principal
                        val busquedaGuardada = data?.get("busquedaPrincipal") as? List<String>
                        busquedaGuardada?.forEach { busqueda ->
                            when(busqueda) {
                                cbBusquedaAventura.text.toString() -> cbBusquedaAventura.isChecked = true
                                cbBusquedaIntriga.text.toString() -> cbBusquedaIntriga.isChecked = true
                                cbBusquedaDesarrolloPersonal.text.toString() -> cbBusquedaDesarrolloPersonal.isChecked = true
                                cbBusquedaTemasSociales.text.toString() -> cbBusquedaTemasSociales.isChecked = true
                                cbBusquedaAmorRelaciones.text.toString() -> cbBusquedaAmorRelaciones.isChecked = true
                                cbBusquedaHistoriaPolitica.text.toString() -> cbBusquedaHistoriaPolitica.isChecked = true
                                cbBusquedaMagiaSeres.text.toString() -> cbBusquedaMagiaSeres.isChecked = true
                                cbBusquedaDivulgacion.text.toString() -> cbBusquedaDivulgacion.isChecked = true
                            }
                        }

                        // Poblar Experiencia Emocional
                        val emocionGuardada = data?.get("experienciaEmocional") as? List<String>
                        emocionGuardada?.forEach { emocion ->
                            when(emocion) {
                                cbEmocionAlegria.text.toString() -> cbEmocionAlegria.isChecked = true
                                cbEmocionReflexion.text.toString() -> cbEmocionReflexion.isChecked = true
                                cbEmocionSuspenso.text.toString() -> cbEmocionSuspenso.isChecked = true
                                cbEmocionEmocionDrama.text.toString() -> cbEmocionEmocionDrama.isChecked = true
                                cbEmocionRelajacion.text.toString() -> cbEmocionRelajacion.isChecked = true
                                cbEmocionInspiracion.text.toString() -> cbEmocionInspiracion.isChecked = true
                                cbEmocionSorpresa.text.toString() -> cbEmocionSorpresa.isChecked = true
                                cbEmocionCuriosidad.text.toString() -> cbEmocionCuriosidad.isChecked = true
                            }
                        }

                        // Poblar Formato
                        val formatoGuardado = data?.get("formatoLectura") as? String
                        formatoGuardado?.let {
                            when (it) {
                                radioButtonFisico.text.toString() -> radioButtonFisico.isChecked = true
                                radioButtonDigital.text.toString() -> radioButtonDigital.isChecked = true
                                radioButtonAmbosFormatos.text.toString() -> radioButtonAmbosFormatos.isChecked = true
                            }
                        }
                    } else {
                        Toast.makeText(this, "No se encontraron preferencias previas para cargar.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar preferencias: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } ?: run {
            Toast.makeText(this, "Error: Usuario no disponible para cargar preferencias.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun validarPreferencias(): Boolean {
        // Validar Autores
        val autoresIngresados = listOf(
            editTextAutor1.text.toString().trim(),
            editTextAutor2.text.toString().trim(),
            editTextAutor3.text.toString().trim()
        ).filter { it.isNotEmpty() }
        if (autoresIngresados.size < 2) {
            Toast.makeText(this, "Por favor, ingresa al menos 2 autores favoritos.", Toast.LENGTH_LONG).show()
            // Opcional: Poner foco en el primer campo de autor vacío o mostrar error en el TextInputLayout
            if (editTextAutor1.text.isNullOrEmpty()) editTextAutor1.requestFocus()
            else if (editTextAutor2.text.isNullOrEmpty()) editTextAutor2.requestFocus()
            return false
        }

        // Validar Libros del Formulario
        val librosIngresados = listOf(
            editTextLibro1.text.toString().trim(),
            editTextLibro2.text.toString().trim(),
            editTextLibro3.text.toString().trim()
        ).filter { it.isNotEmpty() }
        if (librosIngresados.size < 2) {
            Toast.makeText(this, "Por favor, ingresa al menos 2 libros favoritos en el formulario.", Toast.LENGTH_LONG).show()
            if (editTextLibro1.text.isNullOrEmpty()) editTextLibro1.requestFocus()
            else if (editTextLibro2.text.isNullOrEmpty()) editTextLibro2.requestFocus()
            return false
        }

        // Validar Géneros
        val generosSeleccionados = mutableListOf<String>().apply {
            if (cbGeneroFantasia.isChecked) add(cbGeneroFantasia.text.toString())
            if (cbGeneroCienciaFiccion.isChecked) add(cbGeneroCienciaFiccion.text.toString())
            if (cbGeneroMisterioThriller.isChecked) add(cbGeneroMisterioThriller.text.toString())
            if (cbGeneroFilosofia.isChecked) add(cbGeneroFilosofia.text.toString())
            if (cbGeneroRomanceDrama.isChecked) add(cbGeneroRomanceDrama.text.toString())
            if (cbGeneroHistorica.isChecked) add(cbGeneroHistorica.text.toString())
            if (cbGeneroTerrorSuspenso.isChecked) add(cbGeneroTerrorSuspenso.text.toString())
            if (cbGeneroClasicaContemporanea.isChecked) add(cbGeneroClasicaContemporanea.text.toString())
        }
        if (generosSeleccionados.size < 2 || generosSeleccionados.size > 4) {
            Toast.makeText(this, "Selecciona entre 2 y 4 géneros literarios.", Toast.LENGTH_LONG).show()
            return false
        }

        // Validar Búsqueda Principal
        val busquedaPrincipalSeleccionada = mutableListOf<String>().apply {
            if (cbBusquedaAventura.isChecked) add(cbBusquedaAventura.text.toString())
            if (cbBusquedaIntriga.isChecked) add(cbBusquedaIntriga.text.toString())
            // ... (añade todos los CheckBox de búsqueda)
            if (cbBusquedaDivulgacion.isChecked) add(cbBusquedaDivulgacion.text.toString())
        }
        if (busquedaPrincipalSeleccionada.size < 2 || busquedaPrincipalSeleccionada.size > 4) {
            Toast.makeText(this, "Selecciona entre 2 y 4 opciones para lo que buscas en un libro.", Toast.LENGTH_LONG).show()
            return false
        }

        // Validar Experiencia Emocional
        val experienciaEmocionalSeleccionada = mutableListOf<String>().apply {
            if (cbEmocionAlegria.isChecked) add(cbEmocionAlegria.text.toString())
            if (cbEmocionReflexion.isChecked) add(cbEmocionReflexion.text.toString())
            // ... (añade todos los CheckBox de emoción)
            if (cbEmocionCuriosidad.isChecked) add(cbEmocionCuriosidad.text.toString())
        }
        if (experienciaEmocionalSeleccionada.size < 2 || experienciaEmocionalSeleccionada.size > 4) {
            Toast.makeText(this, "Selecciona entre 2 y 4 experiencias emocionales.", Toast.LENGTH_LONG).show()
            return false
        }

        // Aquí puedes añadir más validaciones si son necesarias (ej. formato de lectura seleccionado)

        return true // Todas las validaciones pasaron
    }

    private fun guardarPreferenciasActualizadas() {
        if (!validarPreferencias()) {
            return // Si la validación falla, no continuar.
        }

        userId?.let { uid ->
            // Recolectar Géneros (ya lo tienes de la validación, pero lo repetimos para claridad o puedes pasarlo como parámetro)
            val generosSeleccionados = mutableListOf<String>().apply {
                if (cbGeneroFantasia.isChecked) add(cbGeneroFantasia.text.toString())
                // ... (resto de géneros)
                if (cbGeneroClasicaContemporanea.isChecked) add(cbGeneroClasicaContemporanea.text.toString())
            }

            // Recolectar Autores
            val autores = listOfNotNull(
                editTextAutor1.text.toString().trim().takeIf { it.isNotEmpty() },
                editTextAutor2.text.toString().trim().takeIf { it.isNotEmpty() },
                editTextAutor3.text.toString().trim().takeIf { it.isNotEmpty() }
            )

            // Recolectar Libros Favoritos (del formulario)
            val librosFormulario = listOfNotNull(
                editTextLibro1.text.toString().trim().takeIf { it.isNotEmpty() },
                editTextLibro2.text.toString().trim().takeIf { it.isNotEmpty() },
                editTextLibro3.text.toString().trim().takeIf { it.isNotEmpty() }
            )

            // Recolectar Búsqueda Principal
            val busquedaPrincipalSeleccionada = mutableListOf<String>().apply {
                if (cbBusquedaAventura.isChecked) add(cbBusquedaAventura.text.toString())
                // ... (resto de búsqueda)
                if (cbBusquedaDivulgacion.isChecked) add(cbBusquedaDivulgacion.text.toString())
            }

            // Recolectar Experiencia Emocional
            val experienciaEmocionalSeleccionada = mutableListOf<String>().apply {
                if (cbEmocionAlegria.isChecked) add(cbEmocionAlegria.text.toString())
                // ... (resto de emoción)
                if (cbEmocionCuriosidad.isChecked) add(cbEmocionCuriosidad.text.toString())
            }

            // Recolectar Formato
            val idRadioButtonSeleccionado = radioGroupFormato.checkedRadioButtonId
            var formatoPreferido = ""
            if (idRadioButtonSeleccionado != -1) {
                formatoPreferido = findViewById<RadioButton>(idRadioButtonSeleccionado).text.toString()
            } else {
                // Opcional: Validar que se haya seleccionado un formato
                Toast.makeText(this, "Por favor, selecciona un formato de lectura.", Toast.LENGTH_LONG).show()
                return
            }

            val preferenciasUsuario = hashMapOf(
                "generos" to generosSeleccionados,
                "autoresFavoritos" to autores,
                "librosFavoritos" to librosFormulario,
                "busquedaPrincipal" to busquedaPrincipalSeleccionada,
                "experienciaEmocional" to experienciaEmocionalSeleccionada,
                "formatoLectura" to formatoPreferido,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            firestore.collection("usuarios_preferencias").document(uid)
                .set(preferenciasUsuario, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "Preferencias actualizadas con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar preferencias: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } ?: run {
            Toast.makeText(this, "Error: Usuario no disponible para guardar preferencias.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Maneja el botón de atrás en la Toolbar
        return true
    }

}