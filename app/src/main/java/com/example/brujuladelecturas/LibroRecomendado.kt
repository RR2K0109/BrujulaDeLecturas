package com.example.brujuladelecturas // Reemplaza con tu paquete

data class LibroRecomendado(
    val id: String, // Identificador único para el libro
    val titulo: String,
    val autor: String,
    val emojisRepresentativos: String?, // Para la cadena de 5 emojis
    val sinopsisBreve: String, // Cambiamos 'justificacion' por 'sinopsisBreve' para claridad
    // 'palabrasClave' se puede eliminar si ya no se usa en ningún lado,
    // o mantener si Gemini las provee y las quieres usar internamente aunque no se muestren.
    // Por ahora, la comentaremos para la UI.
    // val palabrasClave: List<String>?
)