package com.example.sportapp.ui.main.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.sportapp.ui.main.dialogs.dialogViewModels.EmailVerifyingViewModel
import com.example.sportapp.ui.main.dialogs.dialogViewModels.PasswordChangeDialogViewModel
import java.lang.IllegalArgumentException

class PasswordChangeDialog : DialogFragment() {

    private val viewModel: PasswordChangeDialogViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Смена пароля")
                .setMessage("Вы уверены, что хотите сменить пароль? Приложение выйдет на экран регситрации")
                .setCancelable(true)
                .setPositiveButton("Да") { dialog, id ->
                    viewModel.changePassword.onNext(Unit)
                }
                .setNegativeButton("Нет") { dilog,id ->

                }
            builder.create()

        } ?: throw IllegalArgumentException("Activity cannot be null")
    }

}