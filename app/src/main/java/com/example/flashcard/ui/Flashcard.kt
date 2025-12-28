package com.example.flashcard.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flashcard.databinding.FlashcardBinding
import com.example.flashcard.ui.model.FlashcardModel

class Flashcard(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {
  var flip: Boolean = false

  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  val sampleData = FlashcardModel(
    id = 1,
    question = "What is 1 + 1",
    answer = "2",
    options = listOf("1", "2", "3", "4")
  )

  fun bind(model: FlashcardModel = sampleData){
    val binding: FlashcardBinding = FlashcardBinding.inflate(LayoutInflater.from(context), this, true)
    binding.flashcardData = model

    binding.cardContainer.setOnClickListener {
      flip = !flip
      binding.cardFront.visibility = if (flip) GONE else VISIBLE
      binding.cardBack.visibility = if (flip) VISIBLE else GONE
    }
  }

}