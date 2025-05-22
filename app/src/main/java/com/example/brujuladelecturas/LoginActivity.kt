package com.example.brujuladelecturas // Asegúrate que coincida con tu paquete

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    // Declarar vistas del layout actividad_login.xml
    private lateinit var textFieldEmailLogin: TextInputLayout
    private lateinit var editTextEmailLogin: EditText
    private lateinit var textFieldPasswordLogin: TextInputLayout
    private lateinit var editTextPasswordLogin: EditText
    private lateinit var buttonIniciarSesion: Button
    private lateinit var textViewIrARegistro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_login) // Enlaza con tu XML

        // Inicializar Firebase Auth
        firebaseAuth = Firebase.auth

        // Obtener referencias a las vistas
        textFieldEmailLogin = findViewById(R.id.textFieldEmailLogin)
        editTextEmailLogin = findViewById(R.id.editTextEmailLogin)
        textFieldPasswordLogin = findViewById(R.id.textFieldPasswordLogin)
        editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin)
        buttonIniciarSesion = findViewById(R.id.buttonIniciarSesion)
        textViewIrARegistro = findViewById(R.id.textViewIrARegistro)

        // Configurar listener para el botón de Iniciar Sesión
        buttonIniciarSesion.setOnClickListener {
            validarYAutenticarUsuario()
        }

        // Configurar listener para el TextView de "Ir a Registro"
        textViewIrARegistro.setOnClickListener {
            val intent = Intent(this, RegistroActividad::class.java)
            startActivity(intent)
            // No es necesario finish() aquí si quieres que el usuario pueda volver al login desde registro
        }
    }

    private fun validarYAutenticarUsuario() {
        val email = editTextEmailLogin.text.toString().trim()
        val password = editTextPasswordLogin.text.toString().trim()

        // Limpiar errores previos
        textFieldEmailLogin.error = null
        textFieldPasswordLogin.error = null

        // Validaciones
        if (email.isEmpty()) {
            textFieldEmailLogin.error = "El correo es obligatorio"
            editTextEmailLogin.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textFieldEmailLogin.error = "Correo no válido"
            editTextEmailLogin.requestFocus()
            return
        }

        if (password.isEmpty()) {
            textFieldPasswordLogin.error = "La contraseña es obligatoria"
            editTextPasswordLogin.requestFocus()
            return
        }

        // (Opcional) Mostrar un Toast mientras se procesa
        Toast.makeText(baseContext, "Iniciando sesión...", Toast.LENGTH_SHORT).show()

        // Autenticar con Firebase
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    val user = firebaseAuth.currentUser
                    Toast.makeText(baseContext, "Inicio de sesión exitoso: ${user?.email}", Toast.LENGTH_LONG).show()

                    // Navegar a MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    // Estas flags limpian la pila de actividades para que el usuario
                    // no pueda volver a la pantalla de login con el botón "atrás"
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Cierra LoginActivity
                } else {
                    // Si el inicio de sesión falla
                    Toast.makeText(baseContext, "Falló el inicio de sesión: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }
}