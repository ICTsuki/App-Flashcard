package com.example.flashcard.ui.flashcard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcard.R
import com.example.flashcard.databinding.ItemFlashcardBinding
import com.example.flashcard.ui.model.FlashcardModel

class FlashcardAdapter(
  var data: List<FlashcardModel>,
  private val onCardClick: (Int) -> Unit,
  private val onOptionClick: (Int, Int) -> Unit
) : RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>() {

  class FlashcardViewHolder(val binding: ItemFlashcardBinding) :
    RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
    val layout = LayoutInflater.from(parent.context)
    val binding = ItemFlashcardBinding.inflate(layout, parent, false)
    return FlashcardViewHolder(binding)
  }

  override fun getItemCount(): Int = data.size

  override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
    val item = data[position]
    val b = holder.binding

    b.flashcardData = item
    b.lifecycleOwner = holder.itemView.context as? LifecycleOwner

    // Click card để flip (giữ logic cũ)
    b.cardContainer.setOnClickListener {
      onCardClick(position)
    }

    // Click chọn đáp án (NEW)
    val canChoose = (item.selectedOptionIndex == null)

    b.cardOption1.isEnabled = canChoose
    b.cardOption2.isEnabled = canChoose
    b.cardOption3.isEnabled = canChoose
    b.cardOption4.isEnabled = canChoose

    b.cardOption1.setOnClickListener { onOptionClick(position, 0) }
    b.cardOption2.setOnClickListener { onOptionClick(position, 1) }
    b.cardOption3.setOnClickListener { onOptionClick(position, 2) }
    b.cardOption4.setOnClickListener { onOptionClick(position, 3) }

    // Tô màu đúng/sai theo state (NEW)
    applyOptionStyle(holder, item)

    b.executePendingBindings()
  }

  fun updateData(newData: List<FlashcardModel>) {
    data = newData
    notifyDataSetChanged()
  }

  private fun applyOptionStyle(holder: FlashcardViewHolder, item: FlashcardModel) {
    val b = holder.binding
    val views = listOf(b.cardOption1, b.cardOption2, b.cardOption3, b.cardOption4)

    // reset về mặc định
    views.forEach { it.setBackgroundResource(R.drawable.option_default) }

    val selected = item.selectedOptionIndex
    val correctIndex = item.options.indexOf(item.answer)

    // Nếu chưa trả lời thì thôi
    if (selected == null) return

    // tô đáp án đúng
    if (correctIndex in 0..3) {
      views[correctIndex].setBackgroundResource(R.drawable.option_correct)
    }

    // tô đáp án đã chọn (đúng thì correct, sai thì wrong)
    if (selected in 0..3) {
      val isCorrect = (selected == correctIndex)
      views[selected].setBackgroundResource(
        if (isCorrect) R.drawable.option_correct else R.drawable.option_wrong
      )
    }
  }
}
