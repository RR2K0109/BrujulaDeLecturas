package com.example.brujuladelecturas // O .fragments.LibrosFragment

import android.os.Bundle
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

class LibrosFragment : Fragment(), ValidatableFragment {

    private val viewModel: PreRegistroViewModel by activityViewModels()

    private lateinit var editTextLibro1: TextInputEditText
    private lateinit var editTextLibro2: TextInputEditText
    private lateinit var editTextLibro3: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_libros, container, false) //
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextLibro1 = view.findViewById(R.id.editTextLibro1) //
        editTextLibro2 = view.findViewById(R.id.editTextLibro2) //
        editTextLibro3 = view.findViewById(R.id.editTextLibro3) //

        // Configurar listeners para actualizar el ViewModel cuando el texto cambie
        editTextLibro1.doAfterTextChanged { text ->
            viewModel.setLibro1(text.toString())
        }
        editTextLibro2.doAfterTextChanged { text ->
            viewModel.setLibro2(text.toString())
        }
        editTextLibro3.doAfterTextChanged { text ->
            viewModel.setLibro3(text.toString())
        }

        // Observar LiveData del ViewModel para actualizar la UI (importante para el modo edición)
        viewModel.libro1.observe(viewLifecycleOwner) { libro ->
            if (editTextLibro1.text.toString() != libro) {
                editTextLibro1.setText(libro)
            }
        }
        viewModel.libro2.observe(viewLifecycleOwner) { libro ->
            if (editTextLibro2.text.toString() != libro) {
                editTextLibro2.setText(libro)
            }
        }
        viewModel.libro3.observe(viewLifecycleOwner) { libro ->
            if (editTextLibro3.text.toString() != libro) {
                editTextLibro3.setText(libro)
            }
        }
    }

    override fun isValidPage(): Boolean {
        val librosIngresados = listOf(
            viewModel.libro1.value,
            viewModel.libro2.value,
            viewModel.libro3.value
        ).count { !it.isNullOrBlank() }

        // Según el PDF "Agregue minimo 2 libros"
        if (librosIngresados < 2) {
            Toast.makeText(requireContext(), "Por favor, ingresa al menos dos libros.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance() = LibrosFragment()
    }
}