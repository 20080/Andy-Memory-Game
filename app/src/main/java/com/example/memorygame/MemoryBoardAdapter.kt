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

    companion object{        //similar to static in java// it can be accessed by the containing class anywhere
        private const val MARGIN_SIZE = 10
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / 2 - (MARGIN_SIZE*2)
        val cardHeight = parent.height / 4 - (MARGIN_SIZE*2)
        val cardSideLength = min(cardWidth,cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card,parent,false)
        //Ripping view i.e. cardView Params form its layout file using some methods
        val layoutPrams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        //Setting height and width
        layoutPrams.height = cardSideLength
        layoutPrams.width = cardSideLength
        layoutPrams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }



    override fun getItemCount() = numPieces //it returns numPieces
}
