package com.example.sportapp.ui.main.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportapp.R
import com.example.sportapp.adapters.CategoriesAdapter
import com.example.sportapp.databinding.FragmentCategoriesBinding
import com.example.sportapp.decorators.SpacesItemVerticalDecoration
import com.example.sportapp.models.ui.categories
import com.example.sportapp.other.ext.getIndexOfCategory
import com.example.sportapp.other.ext.getListOfTittles
import com.example.sportapp.other.snackbar
import com.example.sportapp.other.states.Resource
import com.example.sportapp.ui.main.viewModels.CategoriesViewModel
import com.example.sportapp.ui.main.viewModels.EmailChangeViewModel
import com.example.sportapp.ui.main.viewModels.HomeFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var disposes: CompositeDisposable
    private val viewModel: CategoriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater,container,false)
        disposes = CompositeDisposable()
        setUpRecyclerView()

        //onItemClickListener
        categoriesAdapter.setOnItemClickListener {
            val bundle = Bundle().apply { putString("category",it) }
            viewModel.goToSelectedCategoryScreen.onNext(bundle)
        }

        //onHeartClickListener
        categoriesAdapter.setOnHeartClickListener {
            when (it) {
                is Resource.Success -> {
                    viewModel.updateLikedCategories.onNext(it.data)
                }
                is Resource.Error -> {
                    snackbar(it.message!!,true)
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        //Displaying liked categories
        disposes.add(viewModel.likedCategories.observeOn(AndroidSchedulers.mainThread()).subscribe({
            for (category in categories) {
                category.isLiked = category.tittle in it
            }
            categoriesAdapter.differ.submitList(categories)
        },{}))
    }

    override fun onPause() {
        super.onPause()
        disposes.clear()
    }

    private fun setUpRecyclerView() {
        categoriesAdapter = CategoriesAdapter()
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(this@CategoriesFragment.requireContext(),2)
            adapter = categoriesAdapter
            addItemDecoration(SpacesItemVerticalDecoration(20))
        }
    }


}