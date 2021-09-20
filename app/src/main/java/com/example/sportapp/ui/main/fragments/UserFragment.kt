package com.example.sportapp.ui.main.fragments

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportapp.R
import com.example.sportapp.adapters.HomeCategoriesAdapter
import com.example.sportapp.databinding.FragmentUserBinding
import com.example.sportapp.decorators.SpacesItemHorizontalDecoration
import com.example.sportapp.ui.main.dialogs.EmailVerifyingDialog
import com.example.sportapp.ui.main.dialogs.PasswordChangeDialog
import com.example.sportapp.ui.main.viewModels.UserFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.lang.Exception
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.auth.UserProfileChangeRequest
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.reactivex.rxjava3.disposables.CompositeDisposable


@AndroidEntryPoint
class UserFragmemt : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private lateinit var viewModel: UserFragmentViewModel
    private lateinit var categoriesUserAdapter: HomeCategoriesAdapter
    private val auth = FirebaseAuth.getInstance()
    private lateinit var disposes: CompositeDisposable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layoutChangeEmail.setOnClickListener {
            viewModel.goToChangeEmailScreen.onNext(Unit)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropContent = registerForActivityResult(cropActivityResultContract) {
            it?.let {
                viewModel.updateUserProfileImage.onNext(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(UserFragmentViewModel::class.java)
        disposes = CompositeDisposable()
        setUpRecyclerView()

        binding.ivEmailVerified.setImageResource(
            if (auth.currentUser!!.isEmailVerified) R.drawable.check
            else R.drawable.error
        )

        binding.ibSelectPhoto.setOnClickListener {
            cropContent.launch("image")
        }

        binding.layoutChangePassword.setOnClickListener {
            val passwordChangeDialog = PasswordChangeDialog()
            val manager = parentFragmentManager
            passwordChangeDialog.show(manager,"Dialog")
        }

        binding.layoutVerifyEmail.setOnClickListener {
            val emailVerifyingDialog = EmailVerifyingDialog()
            val manager = parentFragmentManager
            emailVerifyingDialog.show(manager,"Dilog2")
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.isLogOutShown.onNext(true)

        disposes.add(viewModel.userRealtimeUdaptes.observeOn(AndroidSchedulers.mainThread()).subscribe({
            binding.tvUserName.text = it.username
            Glide.with(requireContext()).load(Uri.parse(it.userProfileImageUri)).into(binding.userProfileImage)
            categoriesUserAdapter.differ.submitList(it.likedCategories)
        },{}))

        disposes.add(viewModel.isProgressBarVisible.observeOn(AndroidSchedulers.mainThread()).subscribe({
            binding.imageProgressBar.isVisible = it
            binding.userProfileImage.alpha = if (it) 0.7f else 1.0f
        },{}))
    }

    override fun onPause() {
        super.onPause()
        disposes.clear()
        viewModel.isLogOutShown.onNext(false)
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

    private lateinit var cropContent: ActivityResultLauncher<String>

    private val cropActivityResultContract = object : ActivityResultContract<String, Uri?>() {
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

        override fun createIntent(context: Context, input: String?): Intent {
            return CropImage.activity()
                .setAspectRatio(12,12)
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(requireContext())
        }

    }
}