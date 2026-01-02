package com.example.flashcard.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
  @GET("api.php")
  suspend fun getFlashcards(
    @Query("amount") amount: Int,
    @Query("category") category: Int?,
    @Query("type") type: String = "multiple"
  ): ApiResponse
  @GET("api_category.php")
  suspend fun getCategories(): CategoryResponse
}

