package com.example.flashcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.flashcard.api.TriviaCategory
import com.example.flashcard.databinding.ViewHeaderBinding

class   HeaderFragment : Fragment() {
  private val viewModel: MainViewModel by activityViewModels()
  private var categoriesCache: List<TriviaCategory> = emptyList()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding = ViewHeaderBinding.inflate(inflater, container, false)

    // Set click 1 lần, KHÔNG đặt trong observe
    binding.btnGenerate.setOnClickListener {
      val quantity = binding.quantity.text.toString().toIntOrNull() ?: 10
      val pos = binding.category.selectedItemPosition

      // pos = 0 là "All Categories"
      val categoryId = if (pos <= 0) null else categoriesCache.getOrNull(pos - 1)?.id

      viewModel.fetchFlashcards(quantity, categoryId)
    }

    viewModel.categories.observe(viewLifecycleOwner) { categoriesList ->
      categoriesCache = categoriesList ?: emptyList()

      val names = mutableListOf("All Categories")
      names.addAll(categoriesCache.map { it.name })

      val adapter = ArrayAdapter(
        requireContext(),
        android.R.layout.simple_spinner_item,
        names
      )
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
      binding.category.adapter = adapter
    }

    viewModel.score.observe(viewLifecycleOwner) { s ->
      val t = viewModel.totalAnswered.value ?: 0
      binding.headerScore.text = "Score: $s/$t"
    }
    viewModel.totalAnswered.observe(viewLifecycleOwner) { t ->
      val s = viewModel.score.value ?: 0
      binding.headerScore.text = "Score: $s/$t"
    }

    return binding.root
  }
}
