package com.example.flashcard

import android.app.Application
import androidx.core.text.HtmlCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.flashcard.api.RetrofitClient
import com.example.flashcard.api.TriviaCategory
import com.example.flashcard.data.local.AppDatabase
import com.example.flashcard.data.local.LocalDeckRepository
import com.example.flashcard.data.local.NewCardInput
import com.example.flashcard.data.local.DeckEntity
import com.example.flashcard.ui.model.FlashcardModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private var currentLoadedDeckId: Long? = null

  // ===== Room (My Decks) =====
  private val deckRepo: LocalDeckRepository =
    LocalDeckRepository(AppDatabase.getInstance(application).deckDao())

  val myDecks: LiveData<List<DeckEntity>> = deckRepo.observeDecks().asLiveData()

  fun createMyDeck(name: String, cards: List<NewCardInput>) {
    viewModelScope.launch(Dispatchers.IO) {
      deckRepo.createDeck(name, cards)
    }
  }

  fun loadMyDeck(deckId: Long, limit: Int? = null, shuffle: Boolean = true) {
    currentLoadedDeckId = deckId

    viewModelScope.launch(Dispatchers.IO) {
      val cards = deckRepo.getCards(deckId)
      val picked = if (limit != null && limit > 0) cards.take(limit) else cards

      val list = picked.mapIndexed { index, c ->
        val options = mutableListOf(
          c.correctAnswer, c.wrong1, c.wrong2, c.wrong3
        )
        if (shuffle) options.shuffle()

        FlashcardModel(
          id = index + 1,
          question = c.question,
          answer = c.correctAnswer,
          options = options
        )
      }.toMutableList()

      if (shuffle) list.shuffle()

      resetScore()
      _flashcardList.postValue(list)
    }
  }

  // ===== LiveData chính =====
  private val _flashcardList = MutableLiveData<List<FlashcardModel>>(emptyList())
  val flashcardList: LiveData<List<FlashcardModel>> = _flashcardList

  private val _score = MutableLiveData(0)
  val score: LiveData<Int> = _score

  private val _totalAnswered = MutableLiveData(0)
  val totalAnswered: LiveData<Int> = _totalAnswered

  private val _categories = MutableLiveData<List<TriviaCategory>>(emptyList())
  val categories: LiveData<List<TriviaCategory>> = _categories

  // ===== Sample fallback =====
  private val sampleData1 = FlashcardModel(
    id = 1,
    question = "1 + 1 = ?",
    answer = "2",
    options = listOf("1", "2", "3", "4")
  )
  private val sampleData2 = FlashcardModel(
    id = 2,
    question = "Capital of Viet Nam?",
    answer = "Hanoi",
    options = listOf("Ho Chi Minh", "Da Nang", "Hanoi", "Hue")
  )

  // ===== API: categories =====
  fun fetchCategories() {
    viewModelScope.launch {
      try {
        val res = RetrofitClient.instance.getCategories()
        _categories.value = res.trivia_categories
      } catch (e: Exception) {
        _categories.value = emptyList()
      }
    }
  }


  fun resetScore() {
    _score.postValue(0)
    _totalAnswered.postValue(0)
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

    if (card.selectedOptionIndex != null) return
    if (optionIndex !in card.options.indices) return

    card.selectedOptionIndex = optionIndex
    val picked = card.options[optionIndex]
    val correct = (picked == card.answer)
    card.isCorrect = correct
    card.isFlipped = true

    _totalAnswered.value = (_totalAnswered.value ?: 0) + 1
    if (correct) _score.value = (_score.value ?: 0) + 1

    _flashcardList.value = current
  }

  // ===== API: flashcards =====
  fun fetchFlashcards(amount: Int, categoryId: Int?) {

    currentLoadedDeckId = null

    viewModelScope.launch {
      try {
        val response = RetrofitClient.instance.getFlashcards(
          amount = amount,
          category = categoryId
        )

        val list = response.results.mapIndexed { index, q ->
          val question = decodeString(q.question)
          val correct = decodeString(q.correct_answer)
          val incorrect = q.incorrect_answers.map { s -> decodeString(s) }

          val options = (incorrect + correct).shuffled()
          FlashcardModel(
            id = index + 1,
            question = question,
            answer = correct,
            options = options
          )
        }

        resetScore()
        _flashcardList.value = list
      } catch (e: Exception) {
        println("Lỗi khi tải dữ liệu: ${e.message}")
        resetScore()
        _flashcardList.value = listOf(sampleData1, sampleData2)
      }
    }
  }


  private fun decodeString(str: String): String {
    return HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
  }
  fun deleteMyDeck(deckId: Long) {
    viewModelScope.launch(Dispatchers.IO) {
      deckRepo.deleteDeck(deckId)

      // ✅ Nếu đang hiển thị deck này thì clear UI ngay
      if (currentLoadedDeckId == deckId) {
        currentLoadedDeckId = null
        resetScore()
        _flashcardList.postValue(emptyList())
      }
    }
  }
}
