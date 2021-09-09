package com.example.sportapp.ui.main.fragments

import android.content.ClipData
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportapp.R
import com.example.sportapp.adapters.MaterialsAdapter
import com.example.sportapp.databinding.FragmentHomeBinding
import com.example.sportapp.decorators.SpacesItemVerticalDecoration
import com.example.sportapp.models.rss.materials.Item
import com.example.sportapp.other.Constants.OFFSET
import com.example.sportapp.other.states.DbState
import com.example.sportapp.other.states.ListState
import com.example.sportapp.ui.main.viewModels.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import retrofit2.Retrofit

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeFragmentViewModel>()
    private lateinit var materialsAdapter: MaterialsAdapter
    private var offset = 0
    private var isLoading = false
    private var isLastItems = false
    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        setUpRefreshing()
        setUpRecyclerView()

        materialsAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("link",it.link)
            }
            Log.d("TAG","pressed")
            findNavController().navigate(
                R.id.action_homeFragment_to_itemFragment,
                bundle
            )
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


    //Pagination
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLast = !isLoading && !isLastItems
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginnig = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= 10
            val shouldPaginate = isNotLoadingAndNotLast && isAtLastItem && isNotAtBeginnig
                    && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getDataWithOffset.onNext(ListState.Fulled(offset))
                offset += OFFSET
                isScrolling = false
            } else {
                binding.rvNews.setPadding(0,0,0,0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    //RecyclerView
    private fun setUpRecyclerView() {
        materialsAdapter = MaterialsAdapter()

        binding.rvNews.apply {
            adapter = materialsAdapter
            layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
            addOnScrollListener(this@HomeFragment.scrollListener)
            addItemDecoration(SpacesItemVerticalDecoration(25))
        }
    }

    //PullToRefresh
    private fun setUpRefreshing() {
        binding.itemsToRefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this.requireContext(),R.color.design_default_color_primary))
        binding.itemsToRefresh.setColorSchemeColors(Color.WHITE)
    }
}