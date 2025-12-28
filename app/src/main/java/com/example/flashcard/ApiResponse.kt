package com.example.flashcard

data class ApiResponse (
  val response_code: Int,
  val results: List<OpenTDBQuestion>
)

data class OpenTDBQuestion(
  val question: String,
  val correct_answer: String,
  val incorrect_answers: List<String>
)