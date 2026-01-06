package com.example.flashcard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcard.data.local.NewCardInput
import com.example.flashcard.databinding.ItemDraftCardBinding

class DraftCardAdapter(
    private val data: List<NewCardInput>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<DraftCardAdapter.VH>() {

    inner class VH(val b: ItemDraftCardBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDraftCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = data[position]
        holder.b.tvQuestion.text = item.question
        holder.b.tvAnswer.text = "Ans: ${item.correctAnswer}"
        holder.b.btnDelete.setOnClickListener { onDelete(position) }
    }

    override fun getItemCount(): Int = data.size
}
