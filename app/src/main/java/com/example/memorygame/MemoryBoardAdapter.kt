package com.example.memorygame

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.models.BoardSize
import kotlin.math.min

class MemoryBoardAdapter(private val context: Context, private val boardSize: BoardSize):
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {

    companion object{        //similar to static in java// it can be accessed by the containing class anywhere
        private const val MARGIN_SIZE = 12
        private const val TAG = "MemoryBoardAdapter"
    }


    //Inner Class here
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)
        fun bind(position: Int) {
            imageButton.setOnClickListener{
                Log.i(TAG,"Clicked on  $position")
            }
        }

//        fun fuck() {
//            Log.i(TAG,"Clicked on position")
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val cardWidth = parent.width / 2 - (MARGIN_SIZE*2)
//        val cardHeight = parent.height / 4 - (MARGIN_SIZE*2)

        //making dynamic using enum n shit
        val cardWidth = parent.width / boardSize.getWidth() - (2* MARGIN_SIZE)
        val cardHeight = parent.height / boardSize.getHeight()-(2* MARGIN_SIZE)



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
//        holder.fuck()
    }



    override fun getItemCount() = boardSize.numCards  //it returns numPieces
}
