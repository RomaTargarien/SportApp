package com.example.sportapp.screen.auth.forgot_password

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.sportapp.R
import com.example.sportapp.databinding.FragmentForgotPasswordBinding
import com.example.sportapp.other.progressScreen
import com.example.sportapp.other.textInputBehavior2
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private lateinit var emailDispose: CompositeDisposable
    private lateinit var animation: Animation
    private lateinit var disposes: CompositeDisposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentForgotPasswordBinding.inflate(layoutInflater, container, false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        animation = AnimationUtils.loadAnimation(this.requireContext(), R.anim.anim)
        disposes = CompositeDisposable()
        binding.bnForgotPassword.setOnClickListener {
            viewModel.buttonResetPassword.onNext(Unit)
        }
    }

    override fun onResume() {
        super.onResume()

        //editTextForgotPasswordEmail
        emailDispose = binding.textInputEmailForgotPassword.textInputBehavior2(
            subjectInput = viewModel._emailReset,
            subjectOutput = viewModel.emailReset,
        )

        //buttonResetPassword
        disposes.add(viewModel.resetPasswordButtonEnabled.subscribe({
            binding.bnForgotPassword.alpha = if (it) 1f else 0.7f
            binding.bnForgotPassword.isEnabled = it
        }, {}))

        //progressScreen
        disposes.add(
            viewModel.forgotPasswordScreenBehavior.observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    this.progressScreen(
                        binding.progresScreen,
                        it,
                        animation,
                        binding.forgotPasswordLayout,
                        320
                    )
                }, {})
        )
    }

    override fun onPause() {
        super.onPause()
        disposes.clear()
    }
}