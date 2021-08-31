package com.example.sportapp.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.sportapp.R
import com.example.sportapp.ui.main.viewModels.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Retrofit

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeFragmentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(HomeFragmentViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}