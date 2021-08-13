package com.bharatsave.goldapp.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.SystemClock
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors

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

fun TextView.setCustomTypefaceSpanString(
    firstWord: String,
    @FontRes firstFont: Int,
    secondWord: String,
    @FontRes secondFont: Int
) {
    val spannable = SpannableString(firstWord + secondWord)
    spannable.setSpan(
        CustomTypefaceSpan(ResourcesCompat.getFont(context, firstFont)!!),
        0,
        firstWord.length,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    )
    spannable.setSpan(
        CustomTypefaceSpan(ResourcesCompat.getFont(context, secondFont)!!),
        firstWord.length,
        firstWord.length + secondWord.length,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    )
    text = spannable
}

fun TextView.setMultipleColorSpanString(
    firstWord: String,
    @ColorInt firstColor: Int,
    secondWord: String,
    @ColorInt secondColor: Int
) {
    val spannable = SpannableString(firstWord + "\n" + secondWord)
    spannable.setSpan(
        ForegroundColorSpan(firstColor),
        0,
        firstWord.length,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    )
    spannable.setSpan(
        ForegroundColorSpan(secondColor),
        firstWord.length + 1,
        spannable.length,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE
    )
    text = spannable
}

fun View.showSoftKeyboard() {
    if (requestFocus()) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

@ColorInt
fun Context.getThemeColorFromAttr(@AttrRes colorRef: Int): Int {
    return MaterialColors.getColor(this, colorRef, Color.MAGENTA)
}