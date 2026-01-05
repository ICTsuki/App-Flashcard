package com.example.flashcard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true)
    val deckId: Long = 0L,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
