package com.example.flashcard.data.local

data class NewCardInput(
    val question: String,
    val correctAnswer: String,
    val wrong1: String,
    val wrong2: String,
    val wrong3: String
)
