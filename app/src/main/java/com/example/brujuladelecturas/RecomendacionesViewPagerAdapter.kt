package com.example.brujuladelecturas // Reemplaza con tu paquete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
// import android.widget.ImageView; // Ya no se usa para el corazón
// import android.widget.Toast; // Si no hay clics en el item, podría no necesitarse
import androidx.recyclerview.widget.RecyclerView

class RecomendacionesViewPagerAdapter(
    private var libros: List<LibroRecomendado>
) : RecyclerView.Adapter<RecomendacionesViewPagerAdapter.RecomendacionViewHolder>() {

    inner class RecomendacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val portadaEmojisTextView: TextView = itemView.findViewById(R.id.textViewEmojisPortada)
        val tituloTextView: TextView = itemView.findViewById(R.id.textViewLibroTituloPagina)
        val autorTextView: TextView = itemView.findViewById(R.id.textViewLibroAutorPagina)
        val sinopsisTextView: TextView = itemView.findViewById(R.id.textViewLibroSinopsisDirecta) // Nuevo TextView

        // Ya no se necesitan referencias a palabrasClave, layoutSinopsis, corazonImageView, numCorazonesTextView

        // init {
        //     itemView.setOnClickListener {
        //         // Si quieres alguna acción al clickear toda la tarjeta
        //     }
        // }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecomendacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recomendacion_pagina, parent, false) //
        return RecomendacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecomendacionViewHolder, position: Int) {
        val libroActual = libros[position]

        holder.tituloTextView.text = libroActual.titulo
        holder.autorTextView.text = libroActual.autor
        holder.sinopsisTextView.text = libroActual.sinopsisBreve // Usar el nuevo campo

        if (!libroActual.emojisRepresentativos.isNullOrEmpty()) {
            holder.portadaEmojisTextView.text = libroActual.emojisRepresentativos
        } else {
            holder.portadaEmojisTextView.text = "📖❓🤔🌟💡" // Placeholder
        }
    }

    override fun getItemCount(): Int {
        return libros.size
    }

    fun actualizarLibros(nuevosLibros: List<LibroRecomendado>) {
        libros = nuevosLibros
        notifyDataSetChanged() // O usar DiffUtil para mejor rendimiento
    }
}