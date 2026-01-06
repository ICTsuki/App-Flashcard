package com.example.flashcard.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["deckId"],
            childColumns = ["deckOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deckOwnerId")]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val cardId: Long = 0L,
    val deckOwnerId: Long,
    val question: String,
    val correctAnswer: String,
    val wrong1: String,
    val wrong2: String,
    val wrong3: String
)
