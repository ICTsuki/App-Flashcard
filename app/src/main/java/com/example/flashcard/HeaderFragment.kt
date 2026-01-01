package com.example.flashcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.flashcard.databinding.ViewHeaderBinding

class HeaderFragment : Fragment() {
  private val viewModel : MainViewModel by viewModels()
  override fun onCreateView( inflater: LayoutInflater,
   container: ViewGroup?,
   savedInstanceState: Bundle?
  ): View {
    val binding = ViewHeaderBinding.inflate(inflater, container, false)
    val categoriesList = viewModel.categories.value ?: emptyList()
    val categoryName = categoriesList.map{ it.name }
    val adapter = ArrayAdapter(requireContext(),
      android.R.layout.simple_spinner_item,
      categoryName)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    binding.category.adapter = adapter

    binding.btnGenerate.setOnClickListener {
      val quantity = binding.quantity.text.toString().toIntOrNull() ?: 10
      val position = binding.category.selectedItemPosition
      val categoryId = if (position >= 0 && position < categoriesList.size) {
        categoriesList[position].id
      } else null

      viewModel.fetchFlashcards(quantity, categoryId)
    }

    return binding.root
  }

}