package com.example.flashcard.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class DeckWithCards(
    @Embedded val deck: DeckEntity,
    @Relation(
        parentColumn = "deckId",
        entityColumn = "deckOwnerId"
    )
    val cards: List<CardEntity>
)
