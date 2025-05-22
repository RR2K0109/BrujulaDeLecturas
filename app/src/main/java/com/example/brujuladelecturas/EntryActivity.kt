package com.example.brujuladelecturas // Ajusta tu paquete

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EntryActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry) // Usando tu convención de nombre

        firebaseAuth = Firebase.auth

        // Verificar si el usuario ya está autenticado
        if (firebaseAuth.currentUser != null) {
            // Si ya está logueado, ir directamente a MainActivity
            // En el futuro, aquí podrías añadir lógica para verificar si completó las preferencias
            // y dirigirlo a la pantalla de preferencias si no lo ha hecho.
            // Por ahora, simplificamos y vamos a MainActivity.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza EntryActivity para que no quede en la pila
            return   // Importante para no ejecutar el código de los botones si ya está logueado
        }

        val buttonIniciarSesion: Button = findViewById(R.id.buttonIniciarSesionEntry)
        val buttonCrearCuenta: Button = findViewById(R.id.buttonCrearCuentaEntry)

        buttonIniciarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java) // LoginActivity la crearemos después
            startActivity(intent)
        }

        buttonCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegistroActividad::class.java)
            startActivity(intent)
        }
    }
}