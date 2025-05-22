package com.example.brujuladelecturas // O .fragments.FormatoFragment

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
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

class FormatoFragment : Fragment(), ValidatableFragment {

    private val viewModel: PreRegistroViewModel by activityViewModels()

    private lateinit var toggleButtonGroupFormato: MaterialButtonToggleGroup
    private lateinit var btnFisico: MaterialButton
    private lateinit var btnDigital: MaterialButton
    private lateinit var btnAmbos: MaterialButton

    // Mapa para asociar IDs de botón con el string del formato
    private val buttonFormatMap = mutableMapOf<Int, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_formato, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleButtonGroupFormato = view.findViewById(R.id.toggleButtonGroupFormato)
        btnFisico = view.findViewById(R.id.btnFormatoFisico)
        btnDigital = view.findViewById(R.id.btnFormatoDigital)
        btnAmbos = view.findViewById(R.id.btnFormatoAmbos)

        // Poblar el mapa
        buttonFormatMap.clear()
        buttonFormatMap[btnFisico.id] = btnFisico.text.toString()
        buttonFormatMap[btnDigital.id] = btnDigital.text.toString()
        buttonFormatMap[btnAmbos.id] = btnAmbos.text.toString()

        // Actualizar ViewModel cuando la selección cambie en el ToggleGroup
        toggleButtonGroupFormato.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) { // Solo actuar sobre el botón que se ha marcado
                val formatoSeleccionado = buttonFormatMap[checkedId]
                formatoSeleccionado?.let {
                    viewModel.setFormatoLectura(it)
                }
            } else {
                // Si todos los botones se desmarcan y selectionRequired es false,
                // podríamos necesitar limpiar el ViewModel.
                // Pero con selectionRequired=true, uno siempre debería estar activo si el usuario interactúa.
                // Si el grupo permite la deselección (selectionRequired=false) y se deselecciona el último,
                // checkedId podría ser View.NO_ID o el id del botón que fue desmarcado.
                if (group.checkedButtonId == View.NO_ID) {
                    viewModel.setFormatoLectura("") // Limpiar si no hay nada seleccionado
                }
            }
        }

        // Observar LiveData del ViewModel para actualizar la UI (importante para el modo edición)
        viewModel.formatoLectura.observe(viewLifecycleOwner) { formatoGuardado ->
            // Limpiar selección previa para evitar múltiples checks si el LiveData se emite varias veces
            // toggleButtonGroupFormato.clearChecked() // Esto puede disparar el listener y causar problemas.
            // Es mejor hacerlo con cuidado.

            var buttonIdToCheck: Int? = null
            for ((buttonId, formatText) in buttonFormatMap) {
                if (formatText == formatoGuardado) {
                    buttonIdToCheck = buttonId
                    break
                }
            }

            buttonIdToCheck?.let {
                if (toggleButtonGroupFormato.checkedButtonId != it) {
                    toggleButtonGroupFormato.check(it)
                }
            } ?: run {
                // Si el formatoGuardado no coincide con ninguno, o está vacío, limpiar la selección
                if (toggleButtonGroupFormato.checkedButtonId != View.NO_ID && formatoGuardado.isNullOrEmpty()) {
                    toggleButtonGroupFormato.clearChecked()
                }
            }
        }
    }

    override fun isValidPage(): Boolean {
        // El `MaterialButtonToggleGroup` con `app:selectionRequired="true"`
        // podría ya forzar una selección. Pero es bueno validar también.
        if (viewModel.formatoLectura.value.isNullOrBlank() || toggleButtonGroupFormato.checkedButtonId == View.NO_ID) {
            Toast.makeText(requireContext(), "Por favor, selecciona un formato de lectura.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance() = FormatoFragment()
    }
}