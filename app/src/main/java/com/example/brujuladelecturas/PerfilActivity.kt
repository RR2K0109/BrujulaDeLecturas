package com.example.brujuladelecturas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PerfilActivity : AppCompatActivity() {

    private lateinit var toolbarPerfil: Toolbar
    private lateinit var buttonEditarPreferencias: Button
    private lateinit var viewPagerFavoritos: ViewPager2
    private lateinit var tabLayoutIndicatorFavoritos: TabLayout

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var userId: String? = null

    // Necesitarás un adaptador similar a RecomendacionesViewPagerAdapter
    // pero para los libros favoritos. Vamos a llamarlo FavoritosViewPagerAdapter
    private lateinit var favoritosAdapter: RecomendacionesViewPagerAdapter // Puedes REUTILIZAR RecomendacionesViewPagerAdapter si la data class es la misma
    private var listaLibrosFavoritos: MutableList<LibroRecomendado> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        toolbarPerfil = findViewById(R.id.toolbarPerfil)
        setSupportActionBar(toolbarPerfil)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Para el botón de atrás
        supportActionBar?.setDisplayShowHomeEnabled(true)

        buttonEditarPreferencias = findViewById(R.id.buttonEditarPreferencias)
        viewPagerFavoritos = findViewById(R.id.viewPagerFavoritos)
        tabLayoutIndicatorFavoritos = findViewById(R.id.tabLayoutIndicatorFavoritos)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        userId = auth.currentUser?.uid

        // Configurar el adaptador para el ViewPager2 de favoritos
        // Inicialmente estará vacío, luego cargarás los datos.
        favoritosAdapter = RecomendacionesViewPagerAdapter(listaLibrosFavoritos) // Reutilizando el adaptador existente
        viewPagerFavoritos.adapter = favoritosAdapter

        // Conectar TabLayout (indicador de puntos) con ViewPager2
        TabLayoutMediator(tabLayoutIndicatorFavoritos, viewPagerFavoritos) { tab, position ->
            // No es necesario hacer nada aquí si solo son puntos
        }.attach()

        buttonEditarPreferencias.setOnClickListener {
            // Navegar a la actividad para editar preferencias
            val intent = Intent(this, EditarPreferenciasActivity::class.java)
            startActivity(intent)
        }

        cargarLibrosFavoritos()
    }

    private fun cargarLibrosFavoritos() {
        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        // Asumiremos que tienes una colección "usuarios" y cada usuario tiene
        // una subcolección "librosFavoritos" o un array de IDs/objetos de libros.
        // Ejemplo: usuarios/{userId}/librosFavoritos/{libroId} -> Documento del libro
        // O usuarios/{userId} -> campo "listaFavoritosIds": ["id1", "id2"]

        // Por ahora, vamos a simular la carga, ya que la estructura exacta
        // de cómo guardas los detalles del libro favorito aún no está implementada.
        // TODO: Implementar la lógica real para cargar desde Firestore.

        // Ejemplo de cómo sería si guardas los objetos LibroRecomendado directamente:
        firestore.collection("usuarios_preferencias").document(userId!!)
            .collection("favoritos") // Asumiendo una subcolección "favoritos"
            .get()
            .addOnSuccessListener { documents ->
                val nuevosFavoritos = mutableListOf<LibroRecomendado>()
                for (document in documents) {
                    // Asumiendo que el documento en la subcolección "favoritos"
                    // tiene la misma estructura que LibroRecomendado
                    val libro = document.toObject(LibroRecomendado::class.java)
                    if (libro != null) {
                        nuevosFavoritos.add(libro)
                    }
                }
                listaLibrosFavoritos.clear()
                listaLibrosFavoritos.addAll(nuevosFavoritos)
                favoritosAdapter.actualizarLibros(listaLibrosFavoritos) // Actualiza el adaptador

                if (listaLibrosFavoritos.isEmpty()) {
                    Toast.makeText(this, "Aún no tienes libros favoritos.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar favoritos: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Maneja el botón de atrás en la Toolbar
        return true
    }

    override fun onResume() {
        super.onResume()
        // Recargar libros favoritos por si el usuario añadió/quitó alguno
        // y volvió a esta pantalla.
        // Opcionalmente, podrías usar un listener de Firestore para actualizaciones en tiempo real.
        cargarLibrosFavoritos()
    }
}