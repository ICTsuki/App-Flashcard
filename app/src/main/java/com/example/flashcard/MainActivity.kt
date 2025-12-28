package com.example.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcard.databinding.ActivityMainBinding
import com.example.flashcard.ui.flashcard.FlashcardAdapter
import com.example.flashcard.ui.flashcard.FlashcardView
import com.example.flashcard.ui.model.FlashcardModel

class MainActivity : ComponentActivity() {
  val sampleData1 = FlashcardModel(
    id = 1,
    question = "What is 1 + 1",
    answer = "2",
    options = listOf("1", "2", "3", "4")
  )
  val sampleData2 = FlashcardModel(
    id = 2,
    question = "What is 2 + 2",
    answer = "4",
    options = listOf("1", "2", "3", "4")
  )
  var sampleList: List<FlashcardModel> = listOf(sampleData1, sampleData2)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.cardRecyclerView.layoutManager = LinearLayoutManager(this)
    binding.cardRecyclerView.adapter = FlashcardAdapter(sampleList)
  }
}