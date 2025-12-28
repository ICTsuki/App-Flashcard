package com.example.flashcard.service

import com.example.flashcard.ApiResponse
import com.example.flashcard.ui.model.FlashcardModel
import retrofit2.http.GET

interface ApiService {
  @GET("api.php?amount=10")
  suspend fun getFlashcards(): ApiResponse
}