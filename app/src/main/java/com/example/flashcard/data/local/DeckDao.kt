package com.example.flashcard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

    @Query("SELECT * FROM decks ORDER BY createdAt DESC")
    fun observeDecks(): Flow<List<DeckEntity>>

    @Insert
    suspend fun insertDeck(deck: DeckEntity): Long

    @Insert
    suspend fun insertCards(cards: List<CardEntity>)

    @Transaction
    suspend fun insertDeckWithCards(deck: DeckEntity, cards: List<NewCardInput>): Long {
        val deckId = insertDeck(deck)
        val entities = cards.map {
            CardEntity(
                deckOwnerId = deckId,
                question = it.question,
                correctAnswer = it.correctAnswer,
                wrong1 = it.wrong1,
                wrong2 = it.wrong2,
                wrong3 = it.wrong3
            )
        }
        insertCards(entities)
        return deckId
    }

    @Query("SELECT * FROM cards WHERE deckOwnerId = :deckId")
    suspend fun getCardsForDeck(deckId: Long): List<CardEntity>

    @Query("DELETE FROM decks WHERE deckId = :deckId")
    suspend fun deleteDeck(deckId: Long)
}
