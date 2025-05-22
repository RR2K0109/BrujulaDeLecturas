package com.example.brujuladelecturas // O .fragments.BusquedaLibroFragment

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

class BusquedaLibroFragment : Fragment(), ValidatableFragment {

    private val viewModel: PreRegistroViewModel by activityViewModels()

    // Declarar MaterialButtons
    private lateinit var btnAventura: MaterialButton
    private lateinit var btnIntriga: MaterialButton
    private lateinit var btnDesarrolloPersonal: MaterialButton
    private lateinit var btnTemasSociales: MaterialButton
    private lateinit var btnAmorRelaciones: MaterialButton
    private lateinit var btnHistoriaPolitica: MaterialButton
    private lateinit var btnMagiaSeres: MaterialButton
    private lateinit var btnDivulgacion: MaterialButton

    // Lista para facilitar el manejo de los botones
    private val busquedaButtonPairs = mutableListOf<Pair<MaterialButton, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_busqueda_libro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar MaterialButtons desde el layout
        btnAventura = view.findViewById(R.id.btnBusquedaAventura)
        btnIntriga = view.findViewById(R.id.btnBusquedaIntriga)
        btnDesarrolloPersonal = view.findViewById(R.id.btnBusquedaDesarrolloPersonal)
        btnTemasSociales = view.findViewById(R.id.btnBusquedaTemasSociales)
        btnAmorRelaciones = view.findViewById(R.id.btnBusquedaAmorRelaciones)
        btnHistoriaPolitica = view.findViewById(R.id.btnBusquedaHistoriaPolitica)
        btnMagiaSeres = view.findViewById(R.id.btnBusquedaMagiaSeres)
        btnDivulgacion = view.findViewById(R.id.btnBusquedaDivulgacion)

        // Limpiar y poblar la lista de pares (botón, nombre de la opción de búsqueda)
        // Es importante que el String aquí coincida exactamente con el que usas en el ViewModel y Firestore
        busquedaButtonPairs.clear()
        busquedaButtonPairs.addAll(listOf(
            Pair(btnAventura, "Aventura y Exploración de Mundos"),
            Pair(btnIntriga, "Tramas con Intrigas y Secretos"),
            Pair(btnDesarrolloPersonal, "Desarrollo Personal y Superación"),
            Pair(btnTemasSociales, "Temas Sociales y Crítica a la Realidad"),
            Pair(btnAmorRelaciones, "Amor, Relaciones y Drama Humano"),
            Pair(btnHistoriaPolitica, "Historia o Política"),
            Pair(btnMagiaSeres, "Magia o Seres Fantásticos"),
            Pair(btnDivulgacion, "Divulgación científica")
        ))

        // Configurar listeners y estado inicial
        busquedaButtonPairs.forEach { (button, itemBusqueda) ->
            button.addOnCheckedChangeListener { _, isChecked ->
                viewModel.setBusquedaPrincipal(itemBusqueda, isChecked)
            }
        }

        // Observar cambios en el ViewModel para actualizar la UI (importante para el modo edición)
        viewModel.busquedaPrincipalSeleccionada.observe(viewLifecycleOwner) { busquedaGuardada ->
            busquedaButtonPairs.forEach { (button, itemBusqueda) ->
                if (button.isChecked != busquedaGuardada.contains(itemBusqueda)) {
                    button.isChecked = busquedaGuardada.contains(itemBusqueda)
                }
            }
        }
    }

    override fun isValidPage(): Boolean {
        val count = viewModel.busquedaPrincipalSeleccionada.value?.size ?: 0
        val minOpciones = 2 // Según PDF "Selecciona mínimo 2..."
        val maxOpciones = 4 // Según PDF "...y máximo 4"

        if (count < minOpciones) {
            Toast.makeText(requireContext(), "Por favor, selecciona al menos $minOpciones opciones.", Toast.LENGTH_SHORT).show()
            return false
        }
        if (count > maxOpciones) {
            Toast.makeText(requireContext(), "Por favor, selecciona máximo $maxOpciones opciones.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance() = BusquedaLibroFragment()
    }
}