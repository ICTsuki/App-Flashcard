package com.example.flashcard.ui.flashcard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcard.databinding.ItemFlashcardBinding
import com.example.flashcard.ui.model.FlashcardModel

class FlashcardAdapter(var data : List<FlashcardModel>, val onCardClick: (Int) -> Unit)
  : RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>() {
  class FlashcardViewHolder(val binding: ItemFlashcardBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
    val layout = LayoutInflater.from(parent.context)
    val binding = ItemFlashcardBinding.inflate(layout, parent, false)
    return FlashcardViewHolder(binding)
  }

  override fun getItemCount(): Int = data.size

  override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
    val item = data[position]
    holder.binding.flashcardData = item

    holder.binding.cardContainer.setOnClickListener {
      onCardClick(position)
    }
    holder.binding.executePendingBindings()
  }

  fun updateData(newData: List<FlashcardModel>) {
    this.data = newData
    notifyDataSetChanged()
  }
}