package com.example.flashcard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcard.databinding.ActivityMainBinding
import com.example.flashcard.ui.flashcard.FlashcardAdapter

class MainActivity : AppCompatActivity() {
  private val mainViewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // ✅ Fix: chừa chỗ cho status bar + nav bar (giữ luôn padding 8dp sẵn có)
    val root = binding.root
    val pl = root.paddingLeft
    val pt = root.paddingTop
    val pr = root.paddingRight
    val pb = root.paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
      val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(
        pl + bars.left,
        pt + bars.top,
        pr + bars.right,
        pb + bars.bottom
      )
      insets
    }

    binding.cardRecyclerView.layoutManager = LinearLayoutManager(this)
    binding.lifecycleOwner = this

    // NEW: truyền đủ 3 tham số cho adapter
    val flashcardAdapter = FlashcardAdapter(
      data = emptyList(),
      onCardClick = { position ->
        mainViewModel.flipCard(position)
      },
      onOptionClick = { position, optionIndex ->
        mainViewModel.selectOption(position, optionIndex)
      }
    )

    binding.cardRecyclerView.adapter = flashcardAdapter

    mainViewModel.flashcardList.observe(this) { list ->
      flashcardAdapter.updateData(list ?: emptyList())
    }
  }
}
