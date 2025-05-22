package com.example.brujuladelecturas // O com.example.brujuladelecturas.fragments

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

class GenerosFragment : Fragment(), ValidatableFragment {

    private val viewModel: PreRegistroViewModel by activityViewModels()

    // Cambiar CheckBox por MaterialButton
    private lateinit var btnFantasia: MaterialButton
    private lateinit var btnCienciaFiccion: MaterialButton
    private lateinit var btnMisterioThriller: MaterialButton
    private lateinit var btnFilosofia: MaterialButton
    private lateinit var btnRomanceDrama: MaterialButton
    private lateinit var btnHistorica: MaterialButton
    private lateinit var btnTerrorSuspenso: MaterialButton
    private lateinit var btnClasicaContemporanea: MaterialButton

    // Lista para facilitar el manejo de los botones
    // Es importante que el String aquí coincida exactamente con el que usas en el ViewModel y Firestore
    private val genreButtonPairs = mutableListOf<Pair<MaterialButton, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragment
        return inflater.inflate(R.layout.fragment_generos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar MaterialButtons
        btnFantasia = view.findViewById(R.id.btnGeneroFantasia)
        btnCienciaFiccion = view.findViewById(R.id.btnGeneroCienciaFiccion)
        btnMisterioThriller = view.findViewById(R.id.btnGeneroMisterioThriller)
        btnFilosofia = view.findViewById(R.id.btnGeneroFilosofia)
        btnRomanceDrama = view.findViewById(R.id.btnGeneroRomanceDrama)
        btnHistorica = view.findViewById(R.id.btnGeneroHistorica)
        btnTerrorSuspenso = view.findViewById(R.id.btnGeneroTerrorSuspenso)
        btnClasicaContemporanea = view.findViewById(R.id.btnGeneroClasicaContemporanea)

        // Limpiar y poblar la lista de pares (botón, nombre del género)
        genreButtonPairs.clear()
        genreButtonPairs.addAll(listOf(
            Pair(btnFantasia, "Fantasía"),
            Pair(btnCienciaFiccion, "Ciencia Ficción"),
            Pair(btnMisterioThriller, "Misterio y Thriller"),
            Pair(btnFilosofia, "Filosofía"),
            Pair(btnRomanceDrama, "Romance y Drama"),
            Pair(btnHistorica, "Histórica"),
            Pair(btnTerrorSuspenso, "Terror y suspenso"),
            Pair(btnClasicaContemporanea, "Literatura Clásica y Contemporánea")
        ))

        // Configurar listeners y estado inicial
        genreButtonPairs.forEach { (button, genreName) ->
            // El estado inicial se manejará por el observer
            button.addOnCheckedChangeListener { _, isChecked ->
                viewModel.setGeneroSeleccionado(genreName, isChecked) //
            }
        }

        // Observar cambios en el ViewModel para actualizar la UI (importante para el modo edición)
        viewModel.generosSeleccionados.observe(viewLifecycleOwner) { generosGuardados ->
            genreButtonPairs.forEach { (button, genreName) ->
                // Actualizar el estado 'checked' del botón sin disparar su propio listener
                // para evitar un posible bucle o llamada innecesaria al ViewModel.
                if (button.isChecked != generosGuardados.contains(genreName)) {
                    button.isChecked = generosGuardados.contains(genreName)
                }
            }
        }
    }

    override fun isValidPage(): Boolean { //
        val count = viewModel.generosSeleccionados.value?.size ?: 0 //
        val minGeneros = 2 // Mínimo 2 géneros según tu PDF
        val maxGeneros = 4 // Máximo 4 géneros según tu PDF (aunque el PDF dice "minimo y maximo " para la primera pregunta, luego especifica para otras)
        // Ajusta según tus reglas finales. Tu Toast actual decía min 2, max 4.

        if (count < minGeneros) {
            Toast.makeText(requireContext(), "Por favor, selecciona al menos $minGeneros géneros.", Toast.LENGTH_SHORT).show() //
            return false //
        }
        if (count > maxGeneros) {
            Toast.makeText(requireContext(), "Por favor, selecciona máximo $maxGeneros géneros.", Toast.LENGTH_SHORT).show() //
            return false //
        }
        return true //
    }

    companion object {
        @JvmStatic
        fun newInstance() = GenerosFragment() //
    }
}