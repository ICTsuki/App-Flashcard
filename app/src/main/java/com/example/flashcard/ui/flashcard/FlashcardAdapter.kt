package com.example.flashcard.ui.flashcard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcard.R
import com.example.flashcard.ui.model.FlashcardModel

class FlashcardAdapter(val data : List<FlashcardModel>) : RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>() {
  class FlashcardViewHolder(val flashcardView: FlashcardView) : RecyclerView.ViewHolder(flashcardView)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
    val layout = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_flashcard, parent, false)
    return FlashcardViewHolder(layout as FlashcardView)
  }

  override fun getItemCount(): Int = data.size


  override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
    holder.flashcardView.bind(data[position])
  }
}