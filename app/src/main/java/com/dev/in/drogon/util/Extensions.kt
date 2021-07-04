package com.dev.`in`.drogon.util

import android.view.inputmethod.EditorInfo
import android.widget.EditText

fun EditText.actionGo(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        return@setOnEditorActionListener when (actionId) {
            EditorInfo.IME_ACTION_GO -> {
                callback.invoke()
                true
            }
            else -> false
        }
    }
}
