package com.example.brujuladelecturas // O .fragments.AutoresFragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
// Asegúrate de que PreRegistroViewModel y R se importen correctamente
// import com.example.brujuladelecturas.PreRegistroViewModel
// import com.example.brujuladelecturas.R
import com.google.android.material.textfield.TextInputEditText

class AutoresFragment : Fragment(), ValidatableFragment {

    private val viewModel: PreRegistroViewModel by activityViewModels()

    private lateinit var editTextAutor1: TextInputEditText
    private lateinit var editTextAutor2: TextInputEditText
    private lateinit var editTextAutor3: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_autores, container, false) //
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextAutor1 = view.findViewById(R.id.editTextAutor1) //
        editTextAutor2 = view.findViewById(R.id.editTextAutor2) //
        editTextAutor3 = view.findViewById(R.id.editTextAutor3) //

        // Configurar listeners para actualizar el ViewModel cuando el texto cambie
        editTextAutor1.doAfterTextChanged { text ->
            viewModel.setAutor1(text.toString())
        }
        editTextAutor2.doAfterTextChanged { text ->
            viewModel.setAutor2(text.toString())
        }
        editTextAutor3.doAfterTextChanged { text ->
            viewModel.setAutor3(text.toString())
        }

        // Observar LiveData del ViewModel para actualizar la UI (importante para el modo edición)
        viewModel.autor1.observe(viewLifecycleOwner) { autor ->
            // Solo actualizar si el texto del EditText es diferente al del ViewModel
            // para evitar bucles o perder la posición del cursor.
            if (editTextAutor1.text.toString() != autor) {
                editTextAutor1.setText(autor)
            }
        }
        viewModel.autor2.observe(viewLifecycleOwner) { autor ->
            if (editTextAutor2.text.toString() != autor) {
                editTextAutor2.setText(autor)
            }
        }
        viewModel.autor3.observe(viewLifecycleOwner) { autor ->
            if (editTextAutor3.text.toString() != autor) {
                editTextAutor3.setText(autor)
            }
        }
    }

    override fun isValidPage(): Boolean {
        val autoresIngresados = listOf(
            // Usar directamente el valor del ViewModel que ya está sincronizado
            viewModel.autor1.value,
            viewModel.autor2.value,
            viewModel.autor3.value
        ).count { !it.isNullOrBlank() }

        // Según el PDF "Agrega mínimo 2 autores"
        if (autoresIngresados < 2) {
            Toast.makeText(requireContext(), "Por favor, ingresa al menos dos autores.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance() = AutoresFragment()
    }
}