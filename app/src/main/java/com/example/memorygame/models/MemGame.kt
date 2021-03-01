package com.example.memorygame.models

import com.example.memorygame.utilsvec.DEFAULT_ICONS

class MemGame(private val boardSize: BoardSize) {

    val cards: List<MemoryCard>
    val numPairsFound = 0

    init {
        val chosenImages= DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages+chosenImages).shuffled()
        //creating list of randomized Images by using memoryCard class
        cards = randomizedImages.map { MemoryCard(it) }
    }

}