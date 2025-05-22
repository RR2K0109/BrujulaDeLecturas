package com.example.brujuladelecturas // Asegúrate que coincida con tu paquete

import android.content.Intent
import android.os.Bundle
import android.util.Log // Asegúrate de que Log esté importado
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PreRegistroStepperActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var buttonAnterior: Button
    private lateinit var buttonSiguiente: Button

    private val viewModel: PreRegistroViewModel by viewModels()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val fragments: MutableList<Fragment> = mutableListOf()
    private val NUM_PAGES = 6 // Número total de páginas/fragments
    private var isEditMode = false // Variable para rastrear el modo edición

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_registro_stepper)

        val toolbar: Toolbar = findViewById(R.id.toolbarPreRegistroStepper)
        setSupportActionBar(toolbar)

        firebaseAuth = Firebase.auth
        firestore = Firebase.firestore

        viewPager = findViewById(R.id.viewPagerPreRegistro)
        buttonAnterior = findViewById(R.id.buttonAnterior)
        buttonSiguiente = findViewById(R.id.buttonSiguiente)

        if (fragments.isEmpty()) {
            fragments.add(GenerosFragment.newInstance())
            fragments.add(AutoresFragment.newInstance())
            fragments.add(LibrosFragment.newInstance())
            fragments.add(BusquedaLibroFragment.newInstance())
            fragments.add(ExperienciasFragment.newInstance())
            fragments.add(FormatoFragment.newInstance())
        }

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false // Deshabilitar swipe

        setupButtonListeners()
        updateNavigationButtons(0) // Estado inicial de los botones

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateNavigationButtons(position)
            }
        })

        // --- INICIO DE LÓGICA PARA MODO EDICIÓN ---
        isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false)
        Log.d("PreRegistroStepper", "onCreate - Modo Edición: $isEditMode")

        if (isEditMode) {
            supportActionBar?.title = "Editar Preferencias" // Cambiar título
            Log.d("PreRegistroStepper", "onCreate - Llamando a cargarPreferenciasExistentes...")
            cargarPreferenciasExistentes()
        } else {
            supportActionBar?.title = "Preferencias Literarias"
            // Para un nuevo usuario, podrías querer asegurarte de que el ViewModel esté limpio
            // (aunque por defecto ya lo está si es una nueva instancia).
            // Si reutilizas la misma instancia de ViewModel en ciertos escenarios,
            // podrías necesitar un método viewModel.limpiarDatos() o similar.
            // Por ahora, asumimos que para nuevos usuarios, el ViewModel ya está en su estado inicial.
            Log.d("PreRegistroStepper", "onCreate - Modo Nuevo Usuario (no se cargan preferencias).")
        }
        // --- FIN DE LÓGICA PARA MODO EDICIÓN ---
    }

    // --- NUEVA FUNCIÓN PARA CARGAR PREFERENCIAS EN MODO EDICIÓN ---
    private fun cargarPreferenciasExistentes() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado. No se pueden editar preferencias.", Toast.LENGTH_LONG).show()
            Log.e("PreRegistroStepper", "cargarPreferenciasExistentes: Usuario es nulo.")
            finish() // Salir de la actividad si no hay usuario
            return
        }
        val userId = currentUser.uid

        Log.d("PreRegistroStepper", "cargarPreferenciasExistentes - Cargando para userId: $userId")
        Toast.makeText(this, "Cargando tus preferencias...", Toast.LENGTH_SHORT).show()

        firestore.collection("usuarios_preferencias").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val preferenciasGuardadas = document.data
                    if (preferenciasGuardadas != null) {
                        Log.d("PreRegistroStepper", "cargarPreferenciasExistentes - Preferencias encontradas en Firestore: $preferenciasGuardadas")
                        viewModel.cargarPreferenciasExistentes(preferenciasGuardadas) // Llamada al ViewModel
                        // Los fragments deberían actualizarse automáticamente gracias a sus Observers
                    } else {
                        Log.w("PreRegistroStepper", "cargarPreferenciasExistentes - Documento de preferencias vacío para $userId.")
                        Toast.makeText(this, "No se encontraron preferencias guardadas previamente.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("PreRegistroStepper", "cargarPreferenciasExistentes - No existe documento de preferencias para $userId.")
                    Toast.makeText(this, "Aún no has guardado ninguna preferencia.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("PreRegistroStepper", "cargarPreferenciasExistentes - Error al cargar de Firestore", e)
                Toast.makeText(this, "Error al cargar tus preferencias: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
    // --- FIN DE NUEVA FUNCIÓN ---

    private fun setupButtonListeners() {
        buttonAnterior.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem - 1
        }

        buttonSiguiente.setOnClickListener {
            val currentPagePosition = viewPager.currentItem
            // (Tu lógica de validación y navegación existente está bien)
            if (currentPagePosition >= fragments.size) {
                Log.e("PreRegistroStepper", "Error: currentPagePosition fuera de límites de fragments.")
                return@setOnClickListener
            }
            val currentFragment = fragments[currentPagePosition]

            var canProceed = true
            if (currentFragment is ValidatableFragment) {
                canProceed = currentFragment.isValidPage()
            }

            if (!canProceed) {
                return@setOnClickListener
            }

            if (currentPagePosition < NUM_PAGES - 1) {
                viewPager.currentItem = currentPagePosition + 1
            } else {
                // Última página, el botón "Siguiente" ahora funciona como "Guardar"
                guardarPreferencias()
            }
        }
    }

    private fun updateNavigationButtons(position: Int) {
        buttonAnterior.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        if (position == NUM_PAGES - 1) {
            buttonSiguiente.text = "Guardar Preferencias"
        } else {
            buttonSiguiente.text = "Siguiente"
        }
    }

    private fun guardarPreferencias() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Error: Usuario no autenticado.", Toast.LENGTH_LONG).show()
            return
        }
        val userId = currentUser.uid
        val preferencias = viewModel.obtenerPreferenciasParaFirestore() //
        val preferenciasConTimestamp = HashMap(preferencias)
        preferenciasConTimestamp["timestamp"] = FieldValue.serverTimestamp()

        Toast.makeText(this, "Guardando preferencias...", Toast.LENGTH_SHORT).show()

        firestore.collection("usuarios_preferencias").document(userId)
            .set(preferenciasConTimestamp) // Usar set() para sobrescribir o crear si no existe
            .addOnSuccessListener {
                Toast.makeText(this, "Preferencias guardadas con éxito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar preferencias: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private inner class ScreenSlidePagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = NUM_PAGES
        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}