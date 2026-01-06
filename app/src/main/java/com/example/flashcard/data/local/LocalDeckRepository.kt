package com.example.flashcard.data.local

class LocalDeckRepository(
    private val deckDao: DeckDao
) {
    fun observeDecks() = deckDao.observeDecks()

    suspend fun createDeck(name: String, cards: List<NewCardInput>): Long {
        return deckDao.insertDeckWithCards(
            deck = DeckEntity(name = name),
            cards = cards
        )
    }

    suspend fun getCards(deckId: Long): List<CardEntity> {
        return deckDao.getCardsForDeck(deckId)
    }

    suspend fun deleteDeck(deckId: Long) {
        deckDao.deleteDeck(deckId)
    }
}
