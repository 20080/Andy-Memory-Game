package com.example.memorygame.models

import com.example.memorygame.utilsvec.DEFAULT_ICONS

class MemGame(private val boardSize: BoardSize) {
    val cards: List<MemoryCard>
    val numPairsFound = 0

    private var indOfSingleSelectedCard: Int ? = null
    init {
        val chosenImages= DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages+chosenImages).shuffled()
        //creating list of randomized Images by using memoryCard class
        cards = randomizedImages.map { MemoryCard(it) }
    }


    fun flipCard(position: Int) {
        val card = cards[position]
        //3 cases are there
        //1 card flipped over => flip the card check if there is a match? if yes the don't flip back the cards on case 2
        //0 cards flipped over => flip over the selected card // case 2 is similar
        //2 cards flipped over => flip the card and flip back other cards // case 0 is similar as well
        if (indOfSingleSelectedCard==null){
            //either 0 or two matching card is flipped over
            restoreCards()
            indOfSingleSelectedCard = position
        }
        card.isFaceUp = !card.isFaceUp
    }

    private fun restoreCards() {
        TODO("Not yet implemented")
    }
}
