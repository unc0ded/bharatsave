package com.dev.`in`.drogon.custom_widget.story

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.Transformation
import android.widget.FrameLayout
import com.dev.`in`.drogon.R

class PausableProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val duration: Long = 3200L

    private var primaryProgressView: View
    private var progressBackgroundView: View

    private var primaryProgressColor: Int = Color.parseColor("#FFFFFF")
    private var backgroundProgressColor: Int = Color.parseColor("#3B3941")

    private var animation: PausableScaleAnimation? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.pausable_progress_view, this)
        primaryProgressView = findViewById(R.id.progress_primary)
        progressBackgroundView = findViewById(R.id.progress_background)

        primaryProgressView.setBackgroundColor(primaryProgressColor)
        progressBackgroundView.setBackgroundColor(backgroundProgressColor)
    }

    interface ProgressCallback {
        fun onStartProgress()
        fun onFinishProgress()
    }
    private var callback: ProgressCallback? = null

    fun startProgress() {
        progressBackgroundView.setBackgroundColor(backgroundProgressColor)

        animation = PausableScaleAnimation(
            0f,
            1f,
            1f,
            1f,
            Animation.RELATIVE_TO_PARENT,
            0f,
            Animation.RELATIVE_TO_SELF,
            0f
        )
        animation?.duration = duration
        animation?.interpolator = LinearInterpolator()
        animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                primaryProgressView.visibility = VISIBLE
                callback?.onStartProgress()
            }

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                finishProgress(true)
            }
        })
        animation?.fillAfter = true
        primaryProgressView.startAnimation(animation)
    }

    fun finishProgress(completed: Boolean) {
        if (completed) {
            progressBackgroundView.setBackgroundColor(primaryProgressColor)
            progressBackgroundView.visibility = View.VISIBLE
            callback?.onFinishProgress()
        }
        animation?.let {
            it.setAnimationListener(null)
            it.cancel()
        }
    }

    fun pauseProgress() {
        animation?.pause()
    }

    fun resumeProgress() {
        animation?.resume()
    }

    fun clear() {
        animation?.setAnimationListener(null)
        animation?.cancel()
        animation = null
    }

    fun setCallback(callback: ProgressCallback) {
        this.callback = callback
    }

    private class PausableScaleAnimation(
        fromX: Float,
        toX: Float,
        fromY: Float,
        toY: Float,
        pivotXType: Int,
        pivotXValue: Float,
        pivotYType: Int,
        pivotYValue: Float
    ) :
        ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue) {

        private var elapsedAtPause: Long = 0
        private var paused = false

        override fun getTransformation(
            currentTime: Long,
            outTransformation: Transformation,
            scale: Float
        ): Boolean {
            if (paused && elapsedAtPause == 0L) {
                elapsedAtPause = currentTime - startTime
            }
            if (paused) {
                startTime = currentTime - elapsedAtPause
            }
            return super.getTransformation(currentTime, outTransformation, scale)
        }

        fun pause() {
            if (paused) {
                return
            }
            elapsedAtPause = 0
            paused = true
        }

        fun resume() {
            paused = false
        }
    }
}