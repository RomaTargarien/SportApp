package com.example.sportapp.ui.main.fragments

import android.content.ClipData
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.example.sportapp.R
import com.example.sportapp.adapters.HomeCategoriesAdapter
import com.example.sportapp.adapters.MaterialsAdapter
import com.example.sportapp.background.NewsWorker

import com.example.sportapp.databinding.FragmentHomeBinding
import com.example.sportapp.decorators.SpacesItemHorizontalDecoration
import com.example.sportapp.decorators.SpacesItemVerticalDecoration
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.other.Constants.OFFSET
import com.example.sportapp.other.ext.forEachChildView
import com.example.sportapp.other.states.DbState
import com.example.sportapp.other.states.ListState
import com.example.sportapp.ui.main.viewModels.HomeFragmentViewModel
import com.example.sportapp.ui.main.viewModels.SelectedCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var materialsAdapter: MaterialsAdapter
    private lateinit var categoriesHomeAdapter: HomeCategoriesAdapter
    private var likedCategories = emptyList<String>().toMutableList()
    private var offset = 0
    private var isLastItems = false
    private var viewVisible = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(HomeFragmentViewModel::class.java)
        setUpRefreshing()
        setUpRecyclerView()
        enablePagination(binding.scroller)

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
            likedCategories.remove(it)
            viewModel.updateLikedCategories.onNext(likedCategories)
        }

        //Displaying liked categories
        viewModel.likedCategories.observeOn(AndroidSchedulers.mainThread()).subscribe({
           likedCategories = it.toMutableList()
           categoriesHomeAdapter.differ.submitList(it)
        },{})

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
        viewModel.smoothScrollToFirstPosition
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.rvNews.smoothScrollToPosition(0)
            },{})

        //Passing event to load data + changing the offset
        viewModel.getDataWithOffset.onNext(ListState.Fulled(offset)).also {
            offset += OFFSET
        }

        //Changing the offset after refreshing
        viewModel.changeTheOffset.observeOn(AndroidSchedulers.mainThread()).subscribe({
            offset = OFFSET
        },{})

        //Check if we habe load all items
        viewModel.isLastItems.observeOn(AndroidSchedulers.mainThread()).subscribe({
            isLastItems = it
        },{})

        //Passing items to the adapter
        viewModel.materials.observeOn(AndroidSchedulers.mainThread()).subscribe({
           materialsAdapter.differ.submitList(it)

        },{})

        return binding.root
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
                Log.d("TAG","paginate")
                viewModel.getDataWithOffset.onNext(ListState.Fulled(offset))
                offset += OFFSET
            }
        }
    }
}