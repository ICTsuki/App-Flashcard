package com.example.flashcard.ui.flashcard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flashcard.databinding.FlashcardViewBinding
import com.example.flashcard.ui.model.FlashcardModel

class FlashcardView @JvmOverloads constructor
  (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {
  var flip: Boolean = false
  private val binding = FlashcardViewBinding.inflate(LayoutInflater.from(context), this, true)

  init {
    binding.cardContainer.setOnClickListener {
      flip = !flip
      binding.cardFront.visibility = if (flip) GONE else VISIBLE
      binding.cardBack.visibility = if (flip) VISIBLE else GONE
    }
  }
  val sampleData = FlashcardModel(
    id = 1,
    question = "What is 1 + 1",
    answer = "2",
    options = listOf("1", "2", "3", "4")
  )

  fun bind(model: FlashcardModel = sampleData){
    binding.flashcardData = model
    flip = false
    binding.cardFront.visibility = VISIBLE
    binding.cardBack.visibility = GONE

    binding.executePendingBindings()
  }

}