package com.example.sportapp.screen.main.dialogs.email_verification

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import java.lang.IllegalArgumentException

class EmailVerifyingDialog : DialogFragment() {

    private val viewModel: EmailVerifyingViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Подтверждения почты")
                .setMessage("Вы уверены, что хотите подтвердить почту?")
                .setCancelable(true)
                .setPositiveButton("Да") { dialog, id ->
                    viewModel.emailVerify.onNext(Unit)
                }
                .setNegativeButton("Нет") { dilog,id ->

                }
            builder.create()

        } ?: throw IllegalArgumentException("Activity cannot be null")
    }
}