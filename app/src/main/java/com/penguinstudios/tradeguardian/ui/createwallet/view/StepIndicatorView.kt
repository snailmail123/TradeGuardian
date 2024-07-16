package com.penguinstudios.tradeguardian.ui.createwallet.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.penguinstudios.tradeguardian.R

class StepIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    companion object {
        private const val NUMBER_OF_STEPS = 3
        private const val STEP_RADIUS = 25f
        private const val INDICATOR_TEXT_SIZE_SP = 10f
        private const val LABEL_TEXT_SIZE_SP = 10f
        private const val CIRCLE_STROKE_WIDTH = 4f
        private const val LABEL_OFFSET_Y = 60f
        private val LABELS =
            arrayOf("Create password", "Secure wallet", "Confirm Secret\nRecovery Phrase")
        private const val WHITE_COLOR = Color.WHITE
        private val PURPLE_COLOR = Color.parseColor("#BB86FC")
        private val GREY_COLOR = Color.parseColor("#808080")
    }

    private val circleIndicatorPaint = createPaint(Paint.Style.STROKE, CIRCLE_STROKE_WIDTH)
    private val textPaint = createTextPaint(WHITE_COLOR, INDICATOR_TEXT_SIZE_SP)
    private val labelPaint = createTextPaint(Color.BLACK, LABEL_TEXT_SIZE_SP)
    private var activeStep = 0
    private var stepSpacing: Float = 0f

    init {
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
        labelPaint.textAlign = Paint.Align.CENTER
        labelPaint.typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
    }

    fun setActiveStep(step: Int) {
        activeStep = step
        invalidate()
    }

    private fun createPaint(style: Paint.Style, strokeWidth: Float): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.style = style
            this.strokeWidth = strokeWidth
        }
    }

    private fun createTextPaint(color: Int, textSizeSp: Float): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            this.textSize = spToPx(textSizeSp, context)
        }
    }

    private fun spToPx(sp: Float, context: Context): Float {
        return sp * context.resources.displayMetrics.scaledDensity
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val desiredHeight = (2 * STEP_RADIUS).toInt() + paddingTop + paddingBottom
        stepSpacing = (width - (2 * STEP_RADIUS * NUMBER_OF_STEPS)) / NUMBER_OF_STEPS.toFloat()
        setMeasuredDimension(width, desiredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var x = STEP_RADIUS + (stepSpacing / 2) + paddingLeft

        for (i in 0 until NUMBER_OF_STEPS) {
            when {
                // Completed steps
                i < activeStep -> {
                    circleIndicatorPaint.color = PURPLE_COLOR
                    circleIndicatorPaint.style = Paint.Style.FILL_AND_STROKE
                    labelPaint.color = PURPLE_COLOR
                    textPaint.color = WHITE_COLOR
                }

                // The current active step
                i == activeStep -> {
                    circleIndicatorPaint.color = PURPLE_COLOR
                    circleIndicatorPaint.style = Paint.Style.STROKE
                    labelPaint.color = PURPLE_COLOR
                    textPaint.color = GREY_COLOR
                }

                // Future steps
                else -> {
                    circleIndicatorPaint.color = GREY_COLOR
                    circleIndicatorPaint.style = Paint.Style.STROKE
                    labelPaint.color = GREY_COLOR
                    textPaint.color = GREY_COLOR
                }
            }

            // Draw circle for the step
            val circleCenterX = x
            canvas.drawCircle(
                circleCenterX,
                STEP_RADIUS + paddingTop,
                STEP_RADIUS,
                circleIndicatorPaint
            )

            // Draw text (step number) inside the circle
            val textY = STEP_RADIUS + paddingTop - ((textPaint.descent() + textPaint.ascent()) / 2)
            canvas.drawText((i + 1).toString(), circleCenterX, textY, textPaint)

            // Draw line to the next step if it's not the last step
            if (i < NUMBER_OF_STEPS - 1) {
                circleIndicatorPaint.style = Paint.Style.STROKE // Lines are always hollow
                circleIndicatorPaint.color =
                    if (i < activeStep) PURPLE_COLOR else GREY_COLOR
                val nextCircleCenterX = x + 2 * STEP_RADIUS + stepSpacing
                canvas.drawLine(
                    circleCenterX + STEP_RADIUS,
                    STEP_RADIUS + paddingTop,
                    nextCircleCenterX - STEP_RADIUS,
                    STEP_RADIUS + paddingTop,
                    circleIndicatorPaint
                )
            }

            // Move to the next step position
            x += (2 * STEP_RADIUS) + stepSpacing

            // Draw multi-line text (label) below the circle
            val label = LABELS[i]
            val lines = label.split("\n")
            var labelY =
                (2 * STEP_RADIUS) + paddingTop + LABEL_OFFSET_Y // Start position for the label

            for (line in lines) {
                canvas.drawText(line, circleCenterX, labelY, labelPaint)
                labelY += labelPaint.descent() - labelPaint.ascent() // Move to the next line
            }
        }
    }
}
