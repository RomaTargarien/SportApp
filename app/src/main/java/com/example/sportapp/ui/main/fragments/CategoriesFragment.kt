package com.example.sportapp.ui.main.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportapp.R
import com.example.sportapp.adapters.CategoriesAdapter
import com.example.sportapp.databinding.FragmentCategoriesBinding
import com.example.sportapp.decorators.SpacesItemVerticalDecoration
import com.example.sportapp.models.ui.categories
import com.example.sportapp.other.ext.getIndexOfCategory
import com.example.sportapp.other.ext.getListOfTittles
import com.example.sportapp.ui.main.viewModels.CategoriesViewModel
import com.example.sportapp.ui.main.viewModels.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoriesAdapter: CategoriesAdapter
    private val viewModel by viewModels<CategoriesViewModel>()
    private var categoryTitles = emptyList<String>().toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater,container,false)
        categoriesAdapter = CategoriesAdapter()
        categoriesAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("category",it)
            }
            findNavController().navigate(R.id.action_categoriesFragment_to_selectedCategoryFragment,bundle)
        }
        viewModel.likedCategories.observeOn(AndroidSchedulers.mainThread()).subscribe({
            categoryTitles = it.toMutableList()
            for (category in categories) {
                category.isLiked = category.tittle in it
            }
            categoriesAdapter.differ.submitList(categories)
        },{})

        categoriesAdapter.setOnHeartClickListener {
            if (!(it in categoryTitles)) {
                categoryTitles.add(it)
            } else {
                categoryTitles.remove(it)
            }
            viewModel.updateLikedCategories.onNext(categoryTitles)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
            adapter = categoriesAdapter
            addItemDecoration(SpacesItemVerticalDecoration(10))
        }
        return binding.root
    }
}