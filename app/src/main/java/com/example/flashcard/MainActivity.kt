package com.example.flashcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcard.databinding.ActivityMainBinding
import com.example.flashcard.ui.flashcard.FlashcardAdapter

class MainActivity : ComponentActivity() {
  private val mainViewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.cardRecyclerView.layoutManager = LinearLayoutManager(this)

    val flashcardAdapter = FlashcardAdapter(emptyList()) { position ->
      mainViewModel.flipCard(position)
    }
    binding.cardRecyclerView.adapter = flashcardAdapter
    mainViewModel.flashcardList.observe(this) { list ->
      list?.let {
        flashcardAdapter.updateData(it)
      }
    }
  }
}