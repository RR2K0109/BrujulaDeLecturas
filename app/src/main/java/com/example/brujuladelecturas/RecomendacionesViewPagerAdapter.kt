package com.example.brujuladelecturas // Reemplaza con tu paquete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView // Aún lo usamos para el corazón y el ícono de libro
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
// Ya NO necesitas importar Glide o Picasso si solo usarás emojis

class RecomendacionesViewPagerAdapter(
    private var libros: List<LibroRecomendado> // Usa tu data class LibroRecomendado actualizada
) : RecyclerView.Adapter<RecomendacionesViewPagerAdapter.RecomendacionViewHolder>() {

    inner class RecomendacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencias a las vistas en item_recomendacion_pagina.xml
        // ¡ASEGÚRATE QUE ESTOS IDs COINCIDAN CON TU XML!

        // CAMBIO: De ImageView a TextView para la "portada"
        val portadaEmojisTextView: TextView = itemView.findViewById(R.id.textViewEmojisPortada)

        val tituloTextView: TextView = itemView.findViewById(R.id.textViewLibroTituloPagina)
        val autorTextView: TextView = itemView.findViewById(R.id.textViewLibroAutorPagina)
        val palabrasClaveTextView: TextView = itemView.findViewById(R.id.textViewLibroPalabrasClavePagina)
        val layoutSinopsis: ViewGroup = itemView.findViewById(R.id.layoutSinopsisPagina)
        val corazonImageView: ImageView = itemView.findViewById(R.id.imageViewCorazonPagina)
        val numCorazonesTextView: TextView = itemView.findViewById(R.id.textViewNumeroCorazonesPagina)
        // Si tenías un imageViewIconoLibroPagina, asegúrate de tenerlo aquí también si lo usas
        val iconoLibroSinopsis: ImageView = itemView.findViewById(R.id.imageViewIconoLibroPagina)


        init {
            layoutSinopsis.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val libroClickeado = libros[position]
                    Toast.makeText(itemView.context, "Sinopsis de: ${libroClickeado.titulo}", Toast.LENGTH_SHORT).show()
                    // TODO: Implementar la lógica real para mostrar sinopsis
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecomendacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recomendacion_pagina, parent, false)
        return RecomendacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecomendacionViewHolder, position: Int) {
        val libroActual = libros[position]

        holder.tituloTextView.text = libroActual.titulo
        holder.autorTextView.text = libroActual.autor
        holder.palabrasClaveTextView.text = libroActual.palabrasClave?.joinToString(", ") ?: "Palabras clave no disponibles"

        // CAMBIO: Mostrar Emojis en lugar de cargar imagen con Glide
        if (!libroActual.emojisRepresentativos.isNullOrEmpty()) {
            holder.portadaEmojisTextView.text = libroActual.emojisRepresentativos
        } else {
            // Placeholder si no hay emojis definidos para el libro
            holder.portadaEmojisTextView.text = "📖❓🤔🌟💡" // Ejemplo de 5 emojis placeholder
        }

        // Lógica para la calificación (actualmente placeholder)
        // Esto se llenará cuando implementes el sistema de calificación por usuarios
        holder.numCorazonesTextView.text = "..." // Placeholder para el número de corazones
        holder.corazonImageView.setImageResource(R.drawable.ic_favorite_border) // Placeholder para el ícono de corazón

    }

    override fun getItemCount(): Int {
        return libros.size
    }

    fun actualizarLibros(nuevosLibros: List<LibroRecomendado>) {
        libros = nuevosLibros
        notifyDataSetChanged()
    }
}