package com.example.sportapp.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportapp.R
import com.example.sportapp.adapters.HomeCategoriesAdapter
import com.example.sportapp.databinding.FragmentUserBinding
import com.example.sportapp.decorators.SpacesItemHorizontalDecoration
import com.example.sportapp.ui.main.dialogs.EmailChangeDialog
import com.example.sportapp.ui.main.dialogs.EmailVerifyingDialog
import com.example.sportapp.ui.main.dialogs.PasswordChangeDialog
import com.example.sportapp.ui.main.viewModels.UserFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

@AndroidEntryPoint
class UserFragmemt : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private lateinit var viewModel: UserFragmentViewModel
    private lateinit var categoriesUserAdapter: HomeCategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(UserFragmentViewModel::class.java)
        setUpRecyclerView()
        viewModel.userRealtimeUdaptes.observeOn(AndroidSchedulers.mainThread()).subscribe({
            binding.tvUserName.text = it.username
            categoriesUserAdapter.differ.submitList(it.likedCategories)
        },{})
        binding.bnChangePassword.setOnClickListener {
            val passwordChangeDialog = PasswordChangeDialog()
            val manager = parentFragmentManager
            passwordChangeDialog.show(manager,"Dialog")
        }
        binding.tvLogOut.setOnClickListener {
            viewModel.logOut.onNext(Unit)
        }
        binding.bnChangeEmail.setOnClickListener {
            val emailChangeDialog = EmailChangeDialog()
            val manager = parentFragmentManager
            emailChangeDialog.show(manager,"Dialog2")
        }
        binding.bnVerifyEmail.setOnClickListener {
            val emailVerifyingDialog = EmailVerifyingDialog()
            val manager = parentFragmentManager
            emailVerifyingDialog.show(manager,"Dilog3")
        }
        return binding.root
    }

    private fun setUpRecyclerView() {
        categoriesUserAdapter = HomeCategoriesAdapter()
        binding.rvCategoriesUser.apply {
            adapter = categoriesUserAdapter
            layoutManager = LinearLayoutManager(this@UserFragmemt.requireContext(),
                LinearLayoutManager.HORIZONTAL,false)
            addItemDecoration(SpacesItemHorizontalDecoration(20))
        }
    }
}