package com.example.brujuladelecturas // O .fragments.ExperienciasFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
// Asegúrate de que PreRegistroViewModel y R se importen correctamente
// import com.example.brujuladelecturas.PreRegistroViewModel
// import com.example.brujuladelecturas.R
import com.google.android.material.button.MaterialButton // Importar MaterialButton

class ExperienciasFragment : Fragment(), ValidatableFragment {

    private val viewModel: PreRegistroViewModel by activityViewModels()

    // Declarar MaterialButtons
    private lateinit var btnAlegria: MaterialButton
    private lateinit var btnReflexion: MaterialButton
    private lateinit var btnSuspenso: MaterialButton
    private lateinit var btnEmocionDrama: MaterialButton
    private lateinit var btnRelajacion: MaterialButton
    private lateinit var btnInspiracion: MaterialButton
    private lateinit var btnSorpresa: MaterialButton
    private lateinit var btnCuriosidad: MaterialButton

    // Lista para facilitar el manejo de los botones
    private val experienciaButtonPairs = mutableListOf<Pair<MaterialButton, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_experiencias, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar MaterialButtons desde el layout
        btnAlegria = view.findViewById(R.id.btnEmocionAlegria)
        btnReflexion = view.findViewById(R.id.btnEmocionReflexion)
        btnSuspenso = view.findViewById(R.id.btnEmocionSuspenso)
        btnEmocionDrama = view.findViewById(R.id.btnEmocionEmocionDrama)
        btnRelajacion = view.findViewById(R.id.btnEmocionRelajacion)
        btnInspiracion = view.findViewById(R.id.btnEmocionInspiracion)
        btnSorpresa = view.findViewById(R.id.btnEmocionSorpresa)
        btnCuriosidad = view.findViewById(R.id.btnEmocionCuriosidad)

        // Limpiar y poblar la lista de pares (botón, nombre de la experiencia)
        // Es importante que el String aquí coincida exactamente con el que usas en el ViewModel y Firestore
        experienciaButtonPairs.clear()
        experienciaButtonPairs.addAll(listOf(
            Pair(btnAlegria, "Alegría / Humor"),
            Pair(btnReflexion, "Reflexión / Profundidad"),
            Pair(btnSuspenso, "Suspenso / Tensión"),
            Pair(btnEmocionDrama, "Emoción / Drama"),
            Pair(btnRelajacion, "Relajación / Calma"),
            Pair(btnInspiracion, "Inspiración / Motivación"),
            Pair(btnSorpresa, "Sorpresa / Intriga"),
            Pair(btnCuriosidad, "Curiosidad / Aprendizaje")
        ))

        // Configurar listeners
        experienciaButtonPairs.forEach { (button, experiencia) ->
            button.addOnCheckedChangeListener { _, isChecked ->
                viewModel.setExperienciaEmocional(experiencia, isChecked)
            }
        }

        // Observar cambios en el ViewModel para actualizar la UI (importante para el modo edición)
        viewModel.experienciaEmocionalSeleccionada.observe(viewLifecycleOwner) { experienciasGuardadas ->
            experienciaButtonPairs.forEach { (button, experiencia) ->
                if (button.isChecked != experienciasGuardadas.contains(experiencia)) {
                    button.isChecked = experienciasGuardadas.contains(experiencia)
                }
            }
        }
    }

    override fun isValidPage(): Boolean {
        val count = viewModel.experienciaEmocionalSeleccionada.value?.size ?: 0
        val minOpciones = 2 // Según PDF "Selecciona minima 2..."
        val maxOpciones = 4 // Según PDF "...y maxis 4"

        if (count < minOpciones) {
            Toast.makeText(requireContext(), "Por favor, selecciona al menos $minOpciones experiencias.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (count > maxOpciones) {
            Toast.makeText(requireContext(), "Por favor, selecciona máximo $maxOpciones experiencias.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance() = ExperienciasFragment()
    }
}