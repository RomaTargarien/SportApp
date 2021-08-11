package com.example.sportapp.other

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject

fun TextInputLayout.observe(): Observable<String> {
    return Observable.create { emitter: ObservableEmitter<String> ->
        val watcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                this@observe.setErrorEnabled(false)
            }

            override fun afterTextChanged(editable: Editable) {
                emitter.onNext(editable.toString())
            }

        }
        emitter.setCancellable { this.editText?.removeTextChangedListener(watcher) }
        this.editText?.addTextChangedListener(watcher)
    }
}

fun TextInputLayout.enableError(result: Resource<String>) {
    when (result) {
        is Resource.Error -> {
            this.setErrorEnabled(true)
            this.error = result.message
        }
        is Resource.Success -> {
            this.setErrorEnabled(false)
        }
    }
}

fun TextInputLayout.textInputBehavior(
    subjectInput: BehaviorSubject<String>,
    subjectOutput: BehaviorSubject<Resource<String>>
): Pair<Disposable,Disposable> {
    this.observe().distinctUntilChanged()
        .subscribe(subjectInput)

    val subsription1 = subjectOutput.subscribe({
        this.enableError(it)
    },{})

    val subsription2 = subjectInput.subscribe( {
        this.editText?.setText(it)
        this.editText?.setSelection(it.length)
    },{},{})

    return Pair(subsription1,subsription2)
}