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

class MainViewModel : ViewModel() {

  // ===== LiveData chính =====
  private val _flashcardList = MutableLiveData<List<FlashcardModel>>(emptyList())
  val flashcardList: LiveData<List<FlashcardModel>> get() = _flashcardList

  private val _categories = MutableLiveData<List<TriviaCategory>>(emptyList())
  val categories: LiveData<List<TriviaCategory>> get() = _categories

  // ===== Score (đặt trong ViewModel, không đặt global) =====
  private val _score = MutableLiveData(0)
  val score: LiveData<Int> get() = _score

  private val _totalAnswered = MutableLiveData(0)
  val totalAnswered: LiveData<Int> get() = _totalAnswered

  init {
    fetchCategories()
  }

  // ===== Sample data fallback =====
  companion object {
    private val sampleData1 = FlashcardModel(
      id = 1,
      question = "What is 1 + 1",
      answer = "2",
      options = listOf("1", "2", "3", "4")
    )

    private val sampleData2 = FlashcardModel(
      id = 2,
      question = "Question 2",
      answer = "Answer",
      options = listOf("Option 1", "Option 2", "Answer", "Option 4")
    )
  }

  // ===== UI actions =====
  fun flipCard(position: Int) {
    val list = _flashcardList.value?.toMutableList() ?: return
    if (position !in list.indices) return

    list[position].isFlipped = !list[position].isFlipped
    _flashcardList.value = list
  }

  fun selectOption(position: Int, optionIndex: Int) {
    val current = _flashcardList.value?.toMutableList() ?: return
    val card = current.getOrNull(position) ?: return

    // Nếu đã trả lời rồi thì không cho chọn lại
    if (card.selectedOptionIndex != null) return

    // Check optionIndex hợp lệ
    if (optionIndex !in card.options.indices) return

    card.selectedOptionIndex = optionIndex
    val picked = card.options[optionIndex]
    val correct = (picked == card.answer)
    card.isCorrect = correct

    // Auto flip để hiện đáp án
    card.isFlipped = true

    // Update score
    _totalAnswered.value = (_totalAnswered.value ?: 0) + 1
    if (correct) _score.value = (_score.value ?: 0) + 1

    _flashcardList.value = current
  }

  fun resetScore() {
    _score.value = 0
    _totalAnswered.value = 0
  }

  // ===== Network =====
  fun fetchCategories() {
    viewModelScope.launch {
      try {
        val response = RetrofitClient.instance.getCategories()
        _categories.value = response.trivia_categories
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  fun fetchFlashcards(amount: Int = 10, category: Int? = null) {
    viewModelScope.launch {
      try {
        // Reset score mỗi lần generate bộ câu hỏi mới
        resetScore()

        val response = RetrofitClient.instance.getFlashcards(amount, category)

        val flashcards = response.results.mapIndexed { index, flashcard ->
          FlashcardModel(
            id = index,
            question = decodeString(flashcard.question),
            answer = decodeString(flashcard.correct_answer),
            options = (flashcard.incorrect_answers + flashcard.correct_answer)
              .map { decodeString(it) }
              .shuffled()
          )
        }

        _flashcardList.value = flashcards
      } catch (e: Exception) {
        e.printStackTrace()
        println("Lỗi khi tải dữ liệu: ${e.message}")

        // Fallback
        resetScore()
        _flashcardList.value = listOf(sampleData1, sampleData2)
      }
    }
  }

  // ===== Utils =====
  private fun decodeString(str: String): String {
    return HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
  }
}
