package com.bharatsave.goldapp.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.os.SystemClock
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.TouchDelegate
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnLayout
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

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
    connector: String,
    secondWord: String,
    @FontRes secondFont: Int
) {
    val spannable = SpannableString(firstWord + connector + secondWord)
    spannable.apply {
        setSpan(
            CustomTypefaceSpan(ResourcesCompat.getFont(context, firstFont)!!),
            0,
            firstWord.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        setSpan(
            CustomTypefaceSpan(ResourcesCompat.getFont(context, secondFont)!!),
            firstWord.length + connector.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        text = this
    }
}

fun TextView.setMultipleColorSpanString(
    firstWord: String,
    @ColorInt firstColor: Int,
    connector: String,
    secondWord: String,
    @ColorInt secondColor: Int
) {
    val spannable = SpannableString(firstWord + connector + secondWord)
    spannable.apply {
        setSpan(
            ForegroundColorSpan(firstColor),
            0,
            firstWord.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        setSpan(
            ForegroundColorSpan(secondColor),
            firstWord.length + connector.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        text = this
    }
}

fun TextView.setCustomSpanString(
    firstWord: String,
    @FontRes firstFont: Int,
    @ColorInt firstColor: Int,
    connector: String,
    secondWord: String,
    @FontRes secondFont: Int,
    @ColorInt secondColor: Int
) {
    val spannable = SpannableString(firstWord + connector + secondWord)
    spannable.apply {
        setSpan(
            CustomTypefaceSpan(ResourcesCompat.getFont(context, firstFont)!!),
            0,
            firstWord.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        setSpan(
            ForegroundColorSpan(firstColor),
            0,
            firstWord.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        setSpan(
            CustomTypefaceSpan(ResourcesCompat.getFont(context, secondFont)!!),
            firstWord.length + connector.length,
            this.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        setSpan(
            ForegroundColorSpan(secondColor),
            firstWord.length + connector.length,
            this.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        text = this
    }

}

fun View.showSoftKeyboard() {
    if (requestFocus()) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun View.increaseHitArea(sizeDp: Float) {
    val increasedArea = sizeDp.toPx.toInt()
    (parent as View).doOnLayout {
        val rect = Rect()
        getHitRect(rect)
        rect.top -= increasedArea
        rect.left -= increasedArea
        rect.bottom += increasedArea
        rect.right += increasedArea
        it.touchDelegate = TouchDelegate(rect, this)
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

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = doOnTextChanged { text, _, _, _ -> trySend(text) }
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

fun stringHashMapOf(vararg pairs: Pair<String, String>) = StringHashMap(pairs.size).apply { putAll(pairs) }