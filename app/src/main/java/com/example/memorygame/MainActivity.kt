package com.example.memorygame

import android.animation.ArgbEvaluator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
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
import com.example.memorygame.utilsvec.EXTRA_BOARD_SIZE
import kotlinx.android.synthetic.main.dialog_board_size.*

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
        private const val CREATE_REQUEST_CODE = 1024
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
                return true
            }
            R.id.mi_new_size->{
                showNewSizeDialog()
                return true
            }
            R.id.mi_custom->{
                showCreationDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCreationDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        radioGroupSize.check(R.id.rbEasy)
        showAlertDialog("Create your own memory board",boardSizeView,View.OnClickListener {
            //set new value for the grid of board
           val desiredBoardSize = when(radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy->BoardSize.EASY
                R.id.rbMedium->BoardSize.MEDIUM

                else ->BoardSize.HARD

            }
            //navigate to new activity
            val intent = Intent(this,CreateActivity::class.java)
            intent.putExtra(EXTRA_BOARD_SIZE,desiredBoardSize)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        })
    }

    private fun showNewSizeDialog() {
        //Inflating a view soo that we can pass it to the showAlertDialog function as view parameter to show options
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when(boardSize){
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }
        showAlertDialog("Choose new size",boardSizeView,View.OnClickListener {
            //set new value for the grid of board
            boardSize = when(radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy->BoardSize.EASY
                R.id.rbMedium->BoardSize.MEDIUM

                else ->BoardSize.HARD

            }
            setupBoard()
        })
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

        when(boardSize){
            BoardSize.EASY -> {
                tvNumPairs.text = "Pairs: 0/4"
                tvNumMoves.text = "Easy: 4x2"
            }
            BoardSize.MEDIUM -> {
                tvNumPairs.text = "Pairs: 0/9"
                tvNumMoves.text = "Medium: 6x3"
            }
            BoardSize.HARD -> {
                tvNumPairs.text = "Pairs: 0/12"
                tvNumMoves.text = "Hard: 6x4"
            }
        }

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