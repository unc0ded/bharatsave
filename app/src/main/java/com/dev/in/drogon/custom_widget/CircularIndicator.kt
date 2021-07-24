package com.dev.`in`.drogon.custom_widget


import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*


class CircularIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private var centerX = 0f
    private var centerY = 0f

    private var progressPaint: Paint
    // default colors
    private var progressColor = Color.parseColor("#7CC769")

    // default number of levels -> 3
    private var levelProgress = ArrayList(listOf(0f, 0f, 0f))
    private var numberOfLevels = 3
    private val animationDuration = 2000
    private val animationDelay = 1200
    var progressRectF: RectF

    init {
        progressPaint = Paint().apply {
            this.isAntiAlias = true
            this.color = progressColor
            this.style = Paint.Style.STROKE
            this.strokeCap = Paint.Cap.ROUND
        }
        setLayerType(LAYER_TYPE_SOFTWARE, progressPaint)

        progressRectF = RectF()

        // this is temporary, just for testing!
        setNumberOfLevels(1)
        setProgress(1, 0.7f)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        centerX = width / 2.0f
        centerY = height / 2.0f
    }

    private val progressIndicatorRadius: Float
        get() {
            val factor = 81.43f / 90.0f
            return height * factor
        }
    private val progressIndicatorStrokeWidth: Float
        get() {
            // Originally 7.5f / 90f, lowered to have thinner stroke
            val factor = 2.5f / 90f
            return height * factor
        }

    /*
        - The sweep angle of each arc is dependent on the total number of levels
        - The gap between any two arcs will be of 20degrees, since 10degree is deducted from each
          end of the all the arcs.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        progressPaint.strokeWidth = progressIndicatorStrokeWidth

        progressRectF.set(
            0f + progressIndicatorStrokeWidth,
            0f + progressIndicatorStrokeWidth,
            width - progressIndicatorStrokeWidth,
            height - progressIndicatorStrokeWidth)

        val sweepAngle = 360 / (numberOfLevels * 1.0f)

        var startingAngle = 270f
        for (i in 0 until numberOfLevels) {
            canvas.drawArc(
                progressRectF,
                startingAngle % 360f,
                sweepAngle * levelProgress[i],
                false,
                progressPaint)
            startingAngle += 20f + sweepAngle
        }
    }

    private fun getAnimator(level: Int, progress: Float): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0f, progress)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = animationDuration.toLong()
        valueAnimator.startDelay = (level * animationDelay).toLong()
        valueAnimator.addUpdateListener { animator: ValueAnimator ->
            levelProgress[level] = animator.animatedValue as Float
            invalidate()
        }
        return valueAnimator
    }

    fun setProgress(level: Int, progress: Float) {
        var level = level
        if (level > numberOfLevels || level < 1) {
            return
        }
        // to match the zero indexing
        --level
        val valueAnimators = ArrayList<ValueAnimator>()
        for (i in 0 until level) {
            valueAnimators.add(getAnimator(i, 1f))
        }
        valueAnimators.add(getAnimator(level, progress))
        executeAnimation(valueAnimators)
    }

    private fun executeAnimation(valueAnimators: ArrayList<ValueAnimator>) {
        for (animator in valueAnimators) {
            animator.start()
        }
    }

    fun getProgressColor(): Int {
        return progressColor
    }

    fun setProgressColor(progressColor: Int) {
        this.progressColor = progressColor
        invalidate()
    }

    fun setNumberOfLevels(n: Int) {
        numberOfLevels = n
        // by default, progress of all the levels will be zero
        levelProgress = ArrayList(Collections.nCopies(n, 0f))
        invalidate()
    }
}