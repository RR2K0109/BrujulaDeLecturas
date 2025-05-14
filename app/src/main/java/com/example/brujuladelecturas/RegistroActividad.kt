package com.example.brujuladelecturas // MODIFICA ESTO si tu paquete es diferente

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText // Asegúrate de importar el EditText correcto
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout // Importa TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth // Importa la extensión KTX
import com.google.firebase.ktx.Firebase     // Importa Firebase KTX

class RegistroActividad : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    // Declara las vistas
    private lateinit var textFieldEmailRegistro: TextInputLayout
    private lateinit var editTextEmailRegistro: EditText
    private lateinit var textFieldPasswordRegistro: TextInputLayout
    private lateinit var editTextPasswordRegistro: EditText
    private lateinit var buttonRegistrarse: Button
    private lateinit var textViewIrALogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_registro) // Carga tu layout

        // Inicializar Firebase Auth
        firebaseAuth = Firebase.auth // Forma recomendada con KTX

        // Obtener referencias a las vistas del layout
        // ¡¡ASEGÚRATE QUE ESTOS IDs COINCIDAN CON TU activity_registro.xml!!
        textFieldEmailRegistro = findViewById(R.id.textFieldEmailRegistro)
        editTextEmailRegistro = findViewById(R.id.editTextEmailRegistro) // El EditText DENTRO del TextInputLayout
        textFieldPasswordRegistro = findViewById(R.id.textFieldPasswordRegistro)
        editTextPasswordRegistro = findViewById(R.id.editTextPasswordRegistro) // El EditText DENTRO del TextInputLayout
        buttonRegistrarse = findViewById(R.id.buttonRegistrarse)
        textViewIrALogin = findViewById(R.id.textViewIrALogin) // Si tienes un TextView para ir a Login

        buttonRegistrarse.setOnClickListener {
            validarYRegistrarUsuario()
        }

        // (Opcional) Si tienes una pantalla de Login y quieres navegar a ella:
        // textViewIrALogin.setOnClickListener {
        //     val intent = Intent(this, LoginActivity::class.java) // Asume que tienes LoginActivity
        //     startActivity(intent)
        // }
    }

    private fun validarYRegistrarUsuario() {
        val email = editTextEmailRegistro.text.toString().trim()
        val password = editTextPasswordRegistro.text.toString().trim()

        // Limpiar errores previos de los TextInputLayouts
        textFieldEmailRegistro.error = null
        textFieldPasswordRegistro.error = null

        // Validaciones
        if (email.isEmpty()) {
            textFieldEmailRegistro.error = "El correo es obligatorio"
            editTextEmailRegistro.requestFocus() // Pone el foco en el campo
            return // Detiene la ejecución si hay error
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textFieldEmailRegistro.error = "Correo no válido"
            editTextEmailRegistro.requestFocus()
            return
        }

        if (password.isEmpty()) {
            textFieldPasswordRegistro.error = "La contraseña es obligatoria"
            editTextPasswordRegistro.requestFocus()
            return
        }

        if (password.length < 6) {
            textFieldPasswordRegistro.error = "La contraseña debe tener al menos 6 caracteres"
            editTextPasswordRegistro.requestFocus()
            return
        }

        // Muestra un Toast para indicar que el proceso de registro ha comenzado (opcional)
        Toast.makeText(baseContext, "Registrando...", Toast.LENGTH_SHORT).show()

        // Si todas las validaciones pasan, proceder con el registro en Firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    val user = firebaseAuth.currentUser
                    Toast.makeText(baseContext, "Registro exitoso: ${user?.email}", Toast.LENGTH_LONG).show()

                    // NAVEGAR a la pantalla de pre-registro de gustos
                    val intent = Intent(this, PreRegistroGustosActivity::class.java)
                    // Estas flags limpian la pila de actividades para que el usuario no pueda volver
                    // a la pantalla de registro con el botón "atrás" después de registrarse.
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Cierra RegistroActividad

                } else {
                    // Si el registro falla, muestra un mensaje al usuario.
                    // Es útil mostrar el mensaje de error específico de Firebase.
                    Toast.makeText(baseContext, "Falló el registro: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }
}