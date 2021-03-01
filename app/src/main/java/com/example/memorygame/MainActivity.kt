package com.example.memorygame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.models.BoardSize
import com.example.memorygame.models.MemGame
import com.example.memorygame.models.MemoryCard
import com.example.memorygame.utilsvec.DEFAULT_ICONS

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
    }

    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private var boardSize: BoardSize = BoardSize.EASY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

//        val chosenImages=DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
//        val randomizedImages = (chosenImages+chosenImages).shuffled()
//        //creating list of randomized Images by using memoryCard class
//
//        val memoryCards = randomizedImages.map { MemoryCard(it) }


        val memGame = MemGame(boardSize)
        //Setting up the recycler view
//        rvBoard.adapter = MemoryBoardAdapter(this, boardSize, randomizedImages)
        rvBoard.adapter = MemoryBoardAdapter(this, boardSize, memGame.cards,object : MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                Log.i(TAG,"Clicked here the position is $position")
            }

        })
        rvBoard.setHasFixedSize(true)//optimization step
        rvBoard.layoutManager = GridLayoutManager(this,boardSize.getWidth())

    }
}