package com.example.brujuladelecturas // Reemplaza con tu paquete

import android.content.Intent // Necesario para la navegación desde BottomNav (ejemplo)
import android.os.Bundle
import android.widget.Toast // Para el BottomNav (ejemplo)
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
// Asegúrate de que estas importaciones estén presentes y sean correctas para tu proyecto
// import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var toolbarMain: Toolbar
    private lateinit var viewPagerRecomendaciones: ViewPager2
    private lateinit var tabLayoutIndicator: TabLayout
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var recomendacionViewPagerAdapter: RecomendacionesViewPagerAdapter
    private var listaDeLibros: MutableList<LibroRecomendado> = mutableListOf()

    // Firebase (las necesitarás más adelante para cargar datos reales)
    // private lateinit var firebaseAuth: FirebaseAuth
    // private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Configurar la Toolbar
        toolbarMain = findViewById(R.id.toolbarMain)
        setSupportActionBar(toolbarMain)
        // supportActionBar?.title = "Mis Recomendaciones" // Opcional: Cambiar título desde código

        // 2. Inicializar Vistas
        viewPagerRecomendaciones = findViewById(R.id.viewPagerRecomendaciones)
        tabLayoutIndicator = findViewById(R.id.tabLayoutIndicator)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Inicializar Firebase (lo harás cuando cargues datos reales)
        // firebaseAuth = FirebaseAuth.getInstance()
        // firestore = FirebaseFirestore.getInstance()

        // 3. Configurar ViewPager2 con el Adaptador
        // Primero, crea el adaptador con una lista vacía o de prueba
        recomendacionViewPagerAdapter = RecomendacionesViewPagerAdapter(listaDeLibros)
        viewPagerRecomendaciones.adapter = recomendacionViewPagerAdapter

        // 4. Conectar TabLayout (indicador de puntos) con ViewPager2
        TabLayoutMediator(tabLayoutIndicator, viewPagerRecomendaciones) { tab, position ->
            // No necesitamos texto en las tabs si solo son puntos indicadores
            // Puedes dejar este bloque vacío si el drawable tab_pager_indicator maneja los estados
        }.attach()

        // 5. Configurar BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_recomendaciones -> {
                    // Ya estamos en esta vista (o podrías recargar/actualizar)
                    Toast.makeText(this, "Recomendaciones", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_busqueda_nueva -> {
                    Toast.makeText(this, "Búsqueda Nueva (próximamente)", Toast.LENGTH_SHORT).show()
                    // Ejemplo de cómo podrías navegar a otra actividad:
                    // startActivity(Intent(this, BusquedaActivity::class.java))
                    true
                }
                R.id.navigation_perfil -> {
                    Toast.makeText(this, "Perfil (próximamente)", Toast.LENGTH_SHORT).show()
                    // Ejemplo de cómo podrías navegar a otra actividad:
                    // startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }
        // Seleccionar el primer ítem del BottomNav por defecto (opcional)
        // bottomNavigationView.selectedItemId = R.id.navigation_recomendaciones


        // Cargar datos de prueba para ver el ViewPager2 en acción
        cargarDatosDePruebaConEmojis()

        // TODO Más Adelante:
        //  - Verificar si el usuario está logueado. Si no, redirigir a Login/Registro.
        //  - Cargar preferencias del usuario desde Firestore.
        //  - Construir el prompt para Gemini.
        //  - Llamar a la API de Gemini.
        //  - Actualizar 'listaDeLibros' con las recomendaciones reales y notificar al adaptador.
    }

    // --- Función de Prueba para Cargar Emojis ---
    private fun cargarDatosDePruebaConEmojis() {
        val librosDePrueba = listOf(
            LibroRecomendado("id1", "El Señor de los Anillos", "J.R.R. Tolkien", "🧙‍♂️💍🌋⛰️✨", "Una épica aventura en la Tierra Media para destruir el Anillo Único.", listOf("fantasía", "aventura", "épico")),
            LibroRecomendado("id2", "Dune", "Frank Herbert", "🏜️ WormsSpice👁️‍🗨️👑", "Luchas de poder en el desértico planeta Arrakis, la única fuente de la especia melange.", listOf("ciencia ficción", "política", "ecología")),
            LibroRecomendado("id3", "1984", "George Orwell", "👁️📺📰🧠⛓️", "Un futuro distópico donde el Gran Hermano todo lo ve y controla.", listOf("distopía", "política", "vigilancia"))
        )
        listaDeLibros.clear()
        listaDeLibros.addAll(librosDePrueba)
        recomendacionViewPagerAdapter.actualizarLibros(listaDeLibros) // Usa el método que creaste en el adaptador

        // También puedes notificar directamente al adaptador si no tienes el método actualizarLibros,
        // pero es mejor práctica tener un método dedicado en el adaptador.
        // recomendacionViewPagerAdapter.notifyDataSetChanged()
    }
}