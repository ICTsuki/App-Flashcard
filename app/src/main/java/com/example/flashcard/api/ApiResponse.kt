package com.example.flashcard.api

data class ApiResponse (
  val response_code: Int,
  val results: List<OpenTDBQuestion>
)
data class OpenTDBQuestion(
  val question: String,
  val correct_answer: String,
  val incorrect_answers: List<String>
)
data class CategoryResponse (
  val trivia_categories: List<TriviaCategory>
)
data class TriviaCategory (
  val id: Int,
  val name: String
)