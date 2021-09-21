package com.example.sportapp.ui.main.fragments

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.example.sportapp.R
import com.example.sportapp.adapters.HomeCategoriesAdapter
import com.example.sportapp.adapters.MaterialsAdapter
import com.example.sportapp.databinding.FragmentHomeBinding
import com.example.sportapp.decorators.SpacesItemHorizontalDecoration
import com.example.sportapp.decorators.SpacesItemVerticalDecoration
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.other.Constants.OFFSET
import com.example.sportapp.other.snackbar
import com.example.sportapp.other.states.DbState
import com.example.sportapp.other.states.ListState
import com.example.sportapp.ui.main.viewModels.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var materialsAdapter: MaterialsAdapter
    private lateinit var categoriesHomeAdapter: HomeCategoriesAdapter
    private lateinit var disposes: CompositeDisposable
    private val viewModel: HomeFragmentViewModel by activityViewModels()
    private var offset = 0
    private var isLastItems = false
    private var viewVisible = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        disposes = CompositeDisposable()
        setUpRefreshing()
        setUpRecyclerView()
        enablePagination(binding.scroller)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.materials.onNext(emptyList<Item>().toMutableList())

        //goToSelectedItemScreen
        materialsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply { putString("link",it.link) }
            viewModel.goToSelectedItemScreen.onNext(bundle)
        }

        //goToSelectedCatgoryScreen
        categoriesHomeAdapter.setOnItemClickListener {
            val bundle = Bundle().apply { putString("category",it) }
            viewModel.goToSelectedCategoryScreen.onNext(bundle)
        }

        //Deleting selected category
        categoriesHomeAdapter.setOnDeleteButtonClickListener {
            viewModel.updateLikedCategories.onNext(it)
        }

        //Displaying liked categories
        disposes.add(viewModel.likedCategories.observeOn(AndroidSchedulers.mainThread()).subscribe({
            categoriesHomeAdapter.differ.submitList(it)
        },{}))

        //OpenCategories
        binding.ibLike.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.container)
            viewVisible = !viewVisible
            binding.rvCategories.isVisible = viewVisible
        }

        //refreshing
        binding.itemsToRefresh.setOnRefreshListener {
            viewModel.refresh.onNext(DbState.Fulled())
            binding.itemsToRefresh.isRefreshing = false
        }

        //smoothRecyclerToFirstPosition
        disposes.add(viewModel.smoothScrollToFirstPosition
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.scroller.smoothScrollTo(0,0,2000)
            },{}))

        //Passing event to load data + changing the offset
        viewModel.getDataWithOffset.onNext(ListState.Fulled(offset)).also {
            offset += OFFSET
        }

        //Changing the offset after refreshing
        disposes.add(viewModel.changeTheOffset.observeOn(AndroidSchedulers.mainThread()).subscribe({
            offset = OFFSET
        },{}))

        //Check if we habe load all items
        disposes.add(viewModel.isLastItems.observeOn(AndroidSchedulers.mainThread()).subscribe({
            isLastItems = it
        },{}))

        //Passing items to the adapter
        disposes.add(viewModel.materials.observeOn(AndroidSchedulers.mainThread()).subscribe({
            materialsAdapter.differ.submitList(it)
        },{}))

        disposes.add(viewModel.resultMessage.observeOn(AndroidSchedulers.mainThread()).subscribe({
            this.snackbar(it,true)
        },{}))
    }

    override fun onPause() {
        super.onPause()
        disposes.clear()
        offset = 0
    }

    private fun setUpRecyclerView() {
        materialsAdapter = MaterialsAdapter()
        binding.rvNews.apply {
            adapter = materialsAdapter
            layoutManager = LinearLayoutManager(this@HomeFragment.requireContext(),LinearLayoutManager.VERTICAL,false)
            ViewCompat.setNestedScrollingEnabled(this,true)
            addItemDecoration(SpacesItemVerticalDecoration(25))
        }

        categoriesHomeAdapter = HomeCategoriesAdapter()
        binding.rvCategories.apply {
            adapter = categoriesHomeAdapter
            layoutManager = LinearLayoutManager(this@HomeFragment.requireContext(),LinearLayoutManager.HORIZONTAL,false)
            addItemDecoration(SpacesItemHorizontalDecoration(20))
        }
    }

    private fun setUpRefreshing() {
        binding.itemsToRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this.requireContext(),R.color.design_default_color_primary))
        binding.itemsToRefresh.setColorSchemeColors(Color.WHITE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun enablePagination(nestedScrollView: NestedScrollView) {
        nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            v as NestedScrollView
            val container = v.getChildAt(0) as ConstraintLayout
            val recyler = container.getChildAt(2)
            if (recyler.bottom - (v.height + scrollY) == 0) {
                viewModel.getDataWithOffset.onNext(ListState.Fulled(offset))
                offset += OFFSET
            }
        }
    }


}