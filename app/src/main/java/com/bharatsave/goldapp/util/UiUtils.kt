package com.bharatsave.goldapp.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation


object UiUtils {

    /**
     * @param v           view that is to be expanded
     * @param useDuration boolean value used to specify whether to use the {@param duration} or not.
     * @param duration    integer value used to specify duration of animation
     */
    fun expandVertical(v: View, useDuration: Boolean, duration: Int) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targetHeight: Int = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val va = ValueAnimator.ofInt(1, targetHeight)
        va.addUpdateListener { animation: ValueAnimator ->
            v.layoutParams.height = animation.animatedValue as Int
            v.requestLayout()
        }
        va.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                v.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }

            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        })

        if (useDuration) {
            va.duration = duration.toLong()
        } else {
            // Expand speed of 1dp/ms
            va.duration = (targetHeight / v.context.resources.displayMetrics.density).toLong()
        }
        va.interpolator = DecelerateInterpolator()
        va.start()
    }

    /**
     * @param v           view on which has to be collapsed.
     * @param useDuration boolean value used to specify whether to use the {@param duration} or not.
     * @param duration    integer value used to set animation duration.
     */
    fun collapseVertical(v: View, useDuration: Boolean, duration: Int) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        if (useDuration) {
            a.duration = duration.toLong()
        } else {
            // Collapse speed of 1dp/ms
            a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
        }
        v.startAnimation(a)
    }
}