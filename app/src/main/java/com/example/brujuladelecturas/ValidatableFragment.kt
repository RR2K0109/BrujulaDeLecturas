package com.example.brujuladelecturas

interface ValidatableFragment {
    /**
     * Verifica si los datos en la página actual del fragment son válidos.
     * Debería mostrar un Toast o mensaje de error al usuario si no es válido.
     * @return true si es válido, false en caso contrario.
     */
    fun isValidPage(): Boolean
}