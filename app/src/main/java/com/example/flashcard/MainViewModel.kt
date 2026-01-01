package com.example.flashcard

import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard.api.RetrofitClient
import com.example.flashcard.api.TriviaCategory
import com.example.flashcard.ui.model.FlashcardModel
import kotlinx.coroutines.launch

val sampleData1 = FlashcardModel(
  id = 1,
  question = "What is 1 + 1",
  answer = "2",
  options = listOf("1", "2", "3", "4")
)
val sampleData2 = FlashcardModel(
  id = 2,
  question = "Question 2",
  answer = "Answer",
  options = listOf("Option 1", "Option 2", "Answer", "Option 4")
)
class MainViewModel : ViewModel() {
  private val _flashcardList = MutableLiveData<List<FlashcardModel>>()
  private val _categories = MutableLiveData<List<TriviaCategory>>()
  val flashcardList: LiveData<List<FlashcardModel>> get() = _flashcardList
  val categories: LiveData<List<TriviaCategory>> get() = _categories

  fun flipCard(position: Int) {
    val list = _flashcardList.value?.toMutableList() ?: return
    list[position].isFlipped = !list[position].isFlipped
    _flashcardList.value = list
  }
  fun fetchCategories() {
    viewModelScope.launch {
      try {
        val response = RetrofitClient.instance.getCategories()
        val categories = response.trivia_categories.mapIndexed { index, category ->
          TriviaCategory(
            id = index,
            name = category.name
          )
        }
        _categories.value = categories
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }
  fun fetchFlashcards(amount: Int = 10, category: Int? = null) {
    fetchCategories()
    viewModelScope.launch {
      try {
        val response = RetrofitClient.instance.getFlashcards(amount, category)

        val flashcards = response.results.mapIndexed { index, flashcard ->
          FlashcardModel(
            id = index,
            question = decodeString(flashcard.question),
            answer = decodeString(flashcard.correct_answer),
            options = (flashcard.incorrect_answers + flashcard.correct_answer).map { decodeString(it) }.shuffled()
          )}
        _flashcardList.value = flashcards
      } catch (e: Exception) {
        e.printStackTrace()
        println("Lỗi khi tải dữ liệu: ${e.message}")
        _flashcardList.value = listOf(sampleData1, sampleData2)
      }
    }
  }

  fun decodeString(str: String): String {
    return HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
  }
}