package com.example.flashcard.ui.model

data class FlashcardModel(
  val id: Int,
  val question: String,
  val answer: String,
  val options: List<String>,
  var isFlipped: Boolean = false,
  var selectedOptionIndex: Int? = null,
  var isCorrect: Boolean? = null
)
