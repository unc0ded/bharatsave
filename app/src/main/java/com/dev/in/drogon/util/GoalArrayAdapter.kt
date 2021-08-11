package com.dev.`in`.drogon.util

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.dev.`in`.drogon.R
import com.google.android.material.textview.MaterialTextView
import java.lang.ClassCastException
import java.lang.IllegalStateException

class GoalArrayAdapter(
    context: Context,
    private val resource: Int,
    private val objects: MutableList<String>
) :
    ArrayAdapter<String>(context, resource, objects.apply { add("Create a goal") }) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val textView: MaterialTextView
        val view = convertView ?: inflater.inflate(resource, parent, false)
        try {
            textView = view as MaterialTextView
        } catch (e: ClassCastException) {
            throw IllegalStateException(
                "ArrayAdapter requires the resource ID to be a TextView", e
            )
        }
        textView.text = getItem(position)
        if (position == objects.lastIndex) {
            textView.background =
                ContextCompat.getDrawable(context, R.drawable.dropdown_custom_ripple)
            textView.setTextColor(context.getThemeColorFromAttr(R.attr.colorSurface))
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_forward_black_24dp,
                0
            )
        } else {
            textView.background = with(TypedValue()) {
                context.theme.resolveAttribute(R.attr.listChoiceBackgroundIndicator, this, true)
                ContextCompat.getDrawable(context, resourceId)
            }
            textView.setTextColor(context.getThemeColorFromAttr(R.attr.colorOnBackground))
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }
        return textView
    }
}