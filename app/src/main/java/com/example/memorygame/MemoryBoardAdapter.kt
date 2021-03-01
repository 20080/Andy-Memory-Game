package com.example.memorygame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min

class MemoryBoardAdapter(private val context: Context,private val  numPieces: Int):
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            //No-op
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / 2
        val cardHeight = parent.height / 4
        val cardSideLength = min(cardWidth,cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card,parent,false)
        //Ripping view i.e. cardView Params form its layout file using some methods
        val layoutPrams = view.findViewById<CardView>(R.id.cardView).layoutParams
        //Setting height and width
        layoutPrams.height = cardSideLength
        layoutPrams.width = cardSideLength
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }



    override fun getItemCount() = numPieces //it returns numPieces
}
