package com.example.sportapp.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.sportapp.databinding.FragmentEmailChangeBinding
import com.example.sportapp.other.snackbar
import com.example.sportapp.other.states.Resource
import com.example.sportapp.other.textInputBehavior
import com.example.sportapp.ui.main.viewModels.EmailChangeViewModel
import com.example.sportapp.ui.main.viewModels.HomeFragmentViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import render.animations.Attention
import render.animations.Render
import render.animations.Slide


class EmailChangeFragment : Fragment() {

    private lateinit var binding: FragmentEmailChangeBinding
    private lateinit var emailDispose: CompositeDisposable
    private lateinit var passwordDispose: CompositeDisposable
    private lateinit var disposes: CompositeDisposable
    private lateinit var render: Render
    private val viewModel: EmailChangeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvNext.setOnClickListener {
            viewModel.reauthenticate.onNext(Unit)
        }

        binding.tvEnd.setOnClickListener {
            viewModel.emailChahge.onNext(Unit)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmailChangeBinding.inflate(inflater,container,false)
        disposes = CompositeDisposable()
        render = Render(requireContext())
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val outLeftList = listOf<View>(binding.tvNext,binding.textPassword,binding.textInputConfirmPassword)
        val inRightList = listOf<View>(binding.tvEnd,binding.textEmail,binding.textInputNewEmail)

        //editText password
        passwordDispose = binding.textInputNewEmail.textInputBehavior(
            subjectInput = viewModel._newEmail,
            subjectOutput = viewModel.newEmail
        )

        //editText email
        emailDispose = binding.textInputConfirmPassword.textInputBehavior(
            subjectInput = viewModel._password,
            subjectOutput = viewModel.password
        )

        //Next text
        disposes.add(viewModel.tvNextEnabled.observeOn(AndroidSchedulers.mainThread()).subscribe({
            binding.tvNext.isEnabled = it
            binding.tvNext.alpha = if (it) 1.0f else 0.6f
        },{}))

        //End text
        disposes.add(viewModel.tvEndEnabled.observeOn(AndroidSchedulers.mainThread()).subscribe({
            binding.tvEnd.isEnabled = it
            binding.tvEnd.alpha = if (it) 1.0f else 0.6f
        },{}))

        //progressBar visibility
        disposes.add(viewModel.isProgressBarVisible.observeOn(AndroidSchedulers.mainThread()).subscribe({
            binding.progressScreen.isVisible = it
        },{}))

        //Reauthentication State
        disposes.add(viewModel.reauthenticationState.observeOn(AndroidSchedulers.mainThread()).subscribe({
            when (it) {
                is Resource.Success -> {
                    startAnimation(outLeftList,inRightList)
                }
                is Resource.Error -> {
                    shake(binding.textInputConfirmPassword)
                    it.message?.let {
                        snackbar(it)
                    }
                }
            }
        },{}))

        //Error email changing
        disposes.add(viewModel.errorMessage.subscribe({
            shake(binding.textInputNewEmail)
            snackbar(it)
        },{}))
    }

    override fun onPause() {
        super.onPause()
        disposes.clear()
    }

    private fun startAnimation(outLeftList: List<View>,inRightList: List<View>) {
        for (view in outLeftList) {
            render.setAnimation(Slide().OutLeft(view))
            render.setDuration(800)
            render.start()
        }
        for (view in inRightList) {
            view.isVisible = true
            render.setAnimation(Slide().InRight(view))
            render.setDuration(1200)
            render.start()
        }
    }

    private fun shake(view: View) {
        render.setAnimation(Attention().Tada(view))
        render.setDuration(400)
        render.start()
    }
}