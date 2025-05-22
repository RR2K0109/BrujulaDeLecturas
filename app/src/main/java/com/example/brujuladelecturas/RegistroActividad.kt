package com.example.brujuladelecturas

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
// No es necesario Firestore aquí si no guardamos el nombre directamente en esta actividad

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

        firebaseAuth = Firebase.auth //

        textFieldEmailRegistro = findViewById(R.id.textFieldEmailRegistro) //
        editTextEmailRegistro = findViewById(R.id.editTextEmailRegistro) //
        textFieldPasswordRegistro = findViewById(R.id.textFieldPasswordRegistro) //
        editTextPasswordRegistro = findViewById(R.id.editTextPasswordRegistro) //
        buttonRegistrarse = findViewById(R.id.buttonRegistrarse) //
        textViewIrALogin = findViewById(R.id.textViewIrALogin) //

        buttonRegistrarse.setOnClickListener {
            validarYRegistrarUsuario()
        }

        textViewIrALogin.setOnClickListener {
            // Navegar a LoginActivity (que crearemos)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            // Considera finish() si no quieres que el usuario vuelva a Registro desde Login con el botón atrás
            // finish()
        }
    }

    private fun validarYRegistrarUsuario() {
        val email = editTextEmailRegistro.text.toString().trim() //
        val password = editTextPasswordRegistro.text.toString().trim() //

        textFieldEmailRegistro.error = null //
        textFieldPasswordRegistro.error = null //

        if (email.isEmpty()) { //
            textFieldEmailRegistro.error = "El correo es obligatorio" //
            editTextEmailRegistro.requestFocus() //
            return //
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //
            textFieldEmailRegistro.error = "Correo no válido" //
            editTextEmailRegistro.requestFocus() //
            return //
        }

        if (password.isEmpty()) { //
            textFieldPasswordRegistro.error = "La contraseña es obligatoria" //
            editTextPasswordRegistro.requestFocus() //
            return //
        }

        if (password.length < 6) { //
            textFieldPasswordRegistro.error = "La contraseña debe tener al menos 6 caracteres" //
            editTextPasswordRegistro.requestFocus() //
            return //
        }

        Toast.makeText(baseContext, "Registrando...", Toast.LENGTH_SHORT).show() //

        firebaseAuth.createUserWithEmailAndPassword(email, password) //
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser //
                    Toast.makeText(baseContext, "Registro exitoso: ${user?.email}", Toast.LENGTH_LONG).show() //

                    // Navegar a la actividad de preferencias (actualmente PreRegistroGustosActivity, luego será PreferenceHostActivity)
                    // Tu código original navega a PreRegistroStepperActivity, lo cual es PreRegistroGustosActivity en tus archivos.
                    // Mantenemos PreRegistroGustosActivity por ahora hasta que creemos PreferenceHostActivity en Fase 2.
                    val intent = Intent(this, PreRegistroStepperActivity::class.java) //
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //
                    startActivity(intent) //
                    finish() //

                } else {
                    Toast.makeText(baseContext, "Falló el registro: ${task.exception?.message}", Toast.LENGTH_LONG).show() //
                }
            }
    }
}