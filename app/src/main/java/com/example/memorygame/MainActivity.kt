package com.example.memorygame

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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

    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var memGame: MemGame
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

        setupBoard()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //reset the game or setup the game again
//        setupBoard()
        //the game alert that you might lose your progress mortal
        when(item.itemId){
            R.id.mi_refresh ->{
                if(memGame.getNumMoves()>0&&!memGame.haveWonGame()){
                    showAlertDialog("Quit your current game?",null, View.OnClickListener {
                        setupBoard()
                    })
                }
                else {
                    setupBoard()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog(title:String,view: View?,positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel",null)
            .setPositiveButton("OK"){_,_->
                positiveClickListener.onClick(null)
            }.show()
    }

    private fun setupBoard() {
        tvNumPairs.setTextColor(ContextCompat.getColor(this,R.color.color_progress_none))

//        val chosenImages=DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
//        val randomizedImages = (chosenImages+chosenImages).shuffled()
//        //creating list of randomized Images by using memoryCard class
//
//        val memoryCards = randomizedImages.map { MemoryCard(it) }


        memGame = MemGame(boardSize)
        //Setting up the recycler view
//        rvBoard.adapter = MemoryBoardAdapter(this, boardSize, randomizedImages)
        //can use like this but using as property is fancy i guess
//            rvBoard.adapter =  MemoryBoardAdapter(this, boardSize, memGame.cards,object : MemoryBoardAdapter.CardClickListener
        adapter =  MemoryBoardAdapter(this, boardSize, memGame.cards,object : MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
//                Log.i(TAG,"Clicked here the position is $position")
                updateGameWithFlip(position)
            }


        })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)//optimization step
        rvBoard.layoutManager = GridLayoutManager(this,boardSize.getWidth())
    }

    private fun updateGameWithFlip(position: Int) {

        //Error checking
        if(memGame.haveWonGame()){
            //alert the user of invalid move
            Toast.makeText(this,"You already won boi",Toast.LENGTH_LONG).show()
            return
        }

        if(memGame.isCardFaceUp(position)){
            //alert user of invalid move
            Toast.makeText(this,"Invalid move boi",Toast.LENGTH_SHORT).show()
            return
        }

        //no error then Card Flips//core logic function
        if(memGame.flipCard(position)) {
            Log.i(TAG, "Found match number of pairs found: ${memGame.numPairsFound}")
            tvNumPairs.text = "Pairs: ${memGame.numPairsFound}/${boardSize.getNumPairs()}"

            val color = ArgbEvaluator().evaluate(
                (memGame.numPairsFound.toFloat() / boardSize.getNumPairs()),
                ContextCompat.getColor(this,R.color.color_progress_none),
                ContextCompat.getColor(this,R.color.color_progress_full)
            ) as Int
            tvNumPairs.setTextColor(color)
            if(memGame.haveWonGame())
                Toast.makeText(this,"You WON! Congratulations",Toast.LENGTH_LONG).show()
        }

        tvNumMoves.text = "Moves: ${memGame.getNumMoves()}"
        //related to line 44 for now the memoryBoardAdapter thingy
//        rvBoard.adapter!!.notifyDataSetChanged()
        adapter.notifyDataSetChanged()
    }
}