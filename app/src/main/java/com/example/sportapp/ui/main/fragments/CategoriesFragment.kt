package com.example.sportapp.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportapp.R
import com.example.sportapp.adapters.CategoriesAdapter
import com.example.sportapp.databinding.FragmentCategoriesBinding
import com.example.sportapp.decorators.SpacesItemVerticalDecoration
import com.example.sportapp.models.ui.categories
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater,container,false)
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
            adapter = CategoriesAdapter(categories)
            addItemDecoration(SpacesItemVerticalDecoration(10))
        }
        return binding.root
    }
}