package com.example.flashcard

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcard.data.local.NewCardInput
import com.example.flashcard.databinding.ActivityCreateDeckBinding

class CreateDeckActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateDeckBinding
    private val viewModel: MainViewModel by viewModels()

    private val cards = mutableListOf<NewCardInput>()
    private lateinit var adapter: DraftCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateDeckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DraftCardAdapter(cards) { index ->
            cards.removeAt(index)
            adapter.notifyItemRemoved(index)
            updateCount()
        }

        binding.rvDraftCards.layoutManager = LinearLayoutManager(this)
        binding.rvDraftCards.adapter = adapter

        binding.btnAddCard.setOnClickListener {
            val q = binding.etQuestion.text?.toString()?.trim().orEmpty()
            val c = binding.etCorrect.text?.toString()?.trim().orEmpty()
            val w1 = binding.etWrong1.text?.toString()?.trim().orEmpty()
            val w2 = binding.etWrong2.text?.toString()?.trim().orEmpty()
            val w3 = binding.etWrong3.text?.toString()?.trim().orEmpty()

            if (q.isBlank() || c.isBlank() || w1.isBlank() || w2.isBlank() || w3.isBlank()) {
                toast("Nhập đủ Question + 1 Correct + 3 Wrong nhé")
                return@setOnClickListener
            }

            val item = NewCardInput(
                question = q,
                correctAnswer = c,
                wrong1 = w1,
                wrong2 = w2,
                wrong3 = w3
            )
            cards.add(item)
            adapter.notifyItemInserted(cards.lastIndex)
            clearInputs()
            updateCount()
        }

        binding.btnSaveDeck.setOnClickListener {
            val deckName = binding.etDeckName.text?.toString()?.trim().orEmpty()
            if (deckName.isBlank()) {
                toast("Deck name không được trống")
                return@setOnClickListener
            }
            if (cards.isEmpty()) {
                toast("Bạn chưa thêm card nào")
                return@setOnClickListener
            }

            viewModel.createMyDeck(deckName, cards.toList())
            toast("Đã lưu deck: $deckName")
            finish()
        }

        updateCount()
    }

    private fun updateCount() {
        binding.tvCount.text = "Cards: ${cards.size}"
    }

    private fun clearInputs() {
        binding.etQuestion.setText("")
        binding.etCorrect.setText("")
        binding.etWrong1.setText("")
        binding.etWrong2.setText("")
        binding.etWrong3.setText("")
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
