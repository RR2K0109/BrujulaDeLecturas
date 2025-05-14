package com.example.brujuladelecturas // Reemplaza con tu paquete

data class LibroRecomendado(
    val id: String,
    val titulo: String,
    val autor: String,
    val emojisRepresentativos: String?, // NUEVO: para la cadena de 5 emojis. Puede ser nullable.
    val justificacion: String,
    val palabrasClave: List<String>?,
    // La calificación la manejaremos por separado
)