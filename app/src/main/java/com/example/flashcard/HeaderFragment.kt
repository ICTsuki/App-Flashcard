package com.example.flashcard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.flashcard.api.TriviaCategory
import com.example.flashcard.data.local.DeckEntity
import com.example.flashcard.databinding.ViewHeaderBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HeaderFragment : Fragment() {

  private val viewModel: MainViewModel by activityViewModels()

  private var categoriesCache: List<TriviaCategory> = emptyList()
  private var decksCache: List<DeckEntity> = emptyList()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    val binding = ViewHeaderBinding.inflate(inflater, container, false)

    // ===== Online categories =====
    viewModel.fetchCategories()

    viewModel.categories.observe(viewLifecycleOwner) { list ->
      categoriesCache = list ?: emptyList()

      val names = mutableListOf("All Categories")
      names.addAll(categoriesCache.map { it.name ?: "Unknown" })

      val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
      binding.category.adapter = adapter
    }

    // ===== Score =====
    viewModel.score.observe(viewLifecycleOwner) { s ->
      val t = viewModel.totalAnswered.value ?: 0
      binding.headerScore.text = "Score: $s/$t"
    }
    viewModel.totalAnswered.observe(viewLifecycleOwner) { t ->
      val s = viewModel.score.value ?: 0
      binding.headerScore.text = "Score: $s/$t"
    }

    // ===== Generate (OpenTDB) =====
    binding.btnGenerate.setOnClickListener {
      val quantity = binding.quantity.text.toString().toIntOrNull() ?: 10
      val pos = binding.category.selectedItemPosition
      val categoryId = if (pos <= 0) null else categoriesCache.getOrNull(pos - 1)?.id
      viewModel.fetchFlashcards(quantity, categoryId)
    }

    // ===== Observe decks cache =====
    viewModel.myDecks.observe(viewLifecycleOwner) { decks ->
      decksCache = decks ?: emptyList()
    }

    // ===== My Decks =====
    binding.btnMyDecks.setOnClickListener {
      showDeckPicker(binding)
    }

    // ===== Create Deck =====
    binding.btnCreateDeck.setOnClickListener {
      startActivity(Intent(requireContext(), CreateDeckActivity::class.java))
    }

    return binding.root
  }

  private fun showDeckPicker(binding: ViewHeaderBinding) {
    if (decksCache.isEmpty()) {
      toast("Chưa có deck nào. Bấm Create Deck để tạo nhé.")
      return
    }

    val names = decksCache.map { it.name }.toTypedArray()

    // ✅ FIX: khai báo trước để dùng trong lambda
    var listDialog: AlertDialog? = null

    listDialog = AlertDialog.Builder(requireContext())
      .setTitle("My Decks")
      .setItems(names) { _, which ->
        val deck = decksCache[which]
        val limit = binding.quantity.text.toString().toIntOrNull()

        AlertDialog.Builder(requireContext())
          .setTitle(deck.name)
          .setItems(arrayOf("Load", "Delete")) { _, action ->
            when (action) {
              0 -> {
                viewModel.loadMyDeck(deck.deckId, limit = limit)
              }
              1 -> {
                AlertDialog.Builder(requireContext())
                  .setTitle("Xóa deck?")
                  .setMessage("Bạn chắc chắn muốn xóa \"${deck.name}\"? (sẽ xóa toàn bộ cards)")
                  .setPositiveButton("Xóa") { _, _ ->
                    viewModel.deleteMyDeck(deck.deckId)
                    toast("Đã xóa: ${deck.name}")

                    // ✅ refresh list: đóng dialog cũ và mở lại
                    listDialog?.dismiss()
                    lifecycleScope.launch {
                      delay(200)
                      showDeckPicker(binding)
                    }
                  }
                  .setNegativeButton("Hủy", null)
                  .show()
              }
            }
          }
          .show()
      }
      .create()

    listDialog.show()
  }

  private fun toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
  }
}
