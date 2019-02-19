package com.mofe.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Created by ${cosmic} on 2/14/19.
 */

class CircularProgressBar : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val progressPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }
    private val backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val rect = RectF()
    private val startAngle = -90f
    private val maxAngle = 360f
    private var maxProgress = 0f

    private var diameter = 0f
    private var angle = 0f

    override fun onDraw(canvas: Canvas) {
        drawCircle(maxAngle, canvas, backgroundPaint)
        drawCircle(angle, canvas, progressPaint)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        diameter = Math.min(width, height).toFloat()
        updateRect()
    }

    private fun updateRect() {
        val strokeWidth = backgroundPaint.strokeWidth
        rect.set(strokeWidth, strokeWidth, diameter - strokeWidth, diameter - strokeWidth)
    }

    private fun drawCircle(angle: Float, canvas: Canvas, paint: Paint) {
        canvas.drawArc(rect, startAngle, angle, false, paint)
    }

    private fun calculateAngle(progress: Float) = maxAngle / maxProgress * progress

    fun setMaxProgress(@android.support.annotation.FloatRange(from = 0.0, to = 100.0) Maxprogress: Float) {
        maxProgress = Maxprogress
        invalidate()
    }

    fun setProgress(@android.support.annotation.FloatRange(from = 0.0, to = 100.0) progress: Float) {
        angle = calculateAngle(progress)
        invalidate()
    }

    fun setProgressColor(color: Int) {
        progressPaint.color = color
        invalidate()
    }

    fun setProgressBackgroundColor(color: Int) {
        backgroundPaint.color = color
        invalidate()
    }

    fun getAngle(): Int{
        return angle.toInt()
    }

    fun setProgressWidth(width: Float) {
        progressPaint.strokeWidth = width
        backgroundPaint.strokeWidth = width
        updateRect()
        invalidate()
    }

    fun setRounded(rounded: Boolean) {
        progressPaint.strokeCap = if (rounded) Paint.Cap.ROUND else Paint.Cap.BUTT
        invalidate()
    }
}