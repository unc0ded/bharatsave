package com.dev.`in`.drogon.util

import android.content.Context
import android.os.SystemClock
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.dev.`in`.drogon.R
import com.google.android.material.button.MaterialButton

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

fun View.clickWithThrottle(throttleTimeMillis: Long = 600L, onClick: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTimeMillis: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTimeMillis < throttleTimeMillis) return
            else onClick()

            lastClickTimeMillis = SystemClock.elapsedRealtime()
        }
    })
}

fun MaterialButton.generateRandomString(length: Int) {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    val randomString = (1..length)
        .map { allowedChars.random() }
        .joinToString("")
    text = randomString
}

fun TextView.setCustomTypefaceSpanString(firstWord: String, secondWord: String) {
    val spannable = SpannableString(firstWord + secondWord)
    spannable.setSpan(
        CustomTypefaceSpan(ResourcesCompat.getFont(context, R.font.eina01_light)!!),
        0,
        firstWord.length,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    )
    spannable.setSpan(
        CustomTypefaceSpan(ResourcesCompat.getFont(context, R.font.eina01_semi_bold)!!),
        firstWord.length,
        firstWord.length + secondWord.length,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    )
    text = spannable
}

fun View.showSoftKeyboard() {
    if (requestFocus()) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}