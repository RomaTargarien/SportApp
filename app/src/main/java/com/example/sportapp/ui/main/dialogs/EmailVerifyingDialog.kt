package com.example.sportapp.ui.main.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.sportapp.ui.main.dialogs.dialogViewModels.EmailVerifyingViewModel
import com.example.sportapp.ui.main.dialogs.dialogViewModels.PasswordChangeDialogViewModel
import java.lang.IllegalArgumentException

class EmailVerifyingDialog : DialogFragment() {

    private lateinit var emailVerifyingViewModel: EmailVerifyingViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        emailVerifyingViewModel = ViewModelProvider(requireActivity()).get(EmailVerifyingViewModel::class.java)
        return context?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Подтверждения почты")
                .setMessage("Вы уверены, что хотите подтвердить почту? Приложение выйдет на экран регситрации")
                .setCancelable(true)
                .setPositiveButton("Да") { dialog, id ->
                    emailVerifyingViewModel.emailVerify.onNext(Unit)
                }
                .setNegativeButton("Нет") { dilog,id ->

                }
            builder.create()

        } ?: throw IllegalArgumentException("Activity cannot be null")
    }
}