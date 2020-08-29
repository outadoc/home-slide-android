package fr.outadoc.homeslide.common.feature.overlay.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.ViewGroup
import fr.outadoc.homeslide.common.R
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

class ViewRotator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs) {

    var angle: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                angleChanged = true
                requestLayout()
                invalidate()
            }
        }

    private val rotateMatrix = Matrix()
    private val viewRectRotated = Rect()

    private val tempRectF1 = RectF()
    private val tempRectF2 = RectF()

    private val viewTouchPoint = FloatArray(2)
    private val childTouchPoint = FloatArray(2)

    private var angleChanged = true

    init {
        with(context.obtainStyledAttributes(attrs, R.styleable.ViewRotator)) {
            angle = getFloat(R.styleable.ViewRotator_android_rotation, 0f)
            recycle()
        }

        setWillNotDraw(false)
    }

    /**
     * Returns this layout's child or null if there is no any
     */
    val view: View?
        get() = if (childCount > 0) getChildAt(0) else null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        view?.let { child ->
            when {
                abs(angle.toInt() % 180) == 0 -> {
                    // Normal size
                    measureChild(child, widthMeasureSpec, heightMeasureSpec)
                    setMeasuredDimension(
                        resolveSize(child.measuredWidth, widthMeasureSpec),
                        resolveSize(child.measuredHeight, heightMeasureSpec)
                    )
                }

                abs(angle.toInt() % 180) == 90 -> {
                    // Invert height and width
                    measureChild(child, heightMeasureSpec, widthMeasureSpec)
                    setMeasuredDimension(
                        resolveSize(child.measuredHeight, widthMeasureSpec),
                        resolveSize(child.measuredWidth, heightMeasureSpec)
                    )
                }

                else -> {
                    val childWithMeasureSpec = makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    val childHeightMeasureSpec = makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

                    measureChild(child, childWithMeasureSpec, childHeightMeasureSpec)

                    val measuredWidth = ceil(
                        child.measuredWidth * abs(cos(circleAngle))
                            + child.measuredHeight * abs(sin(circleAngle))
                    ).toInt()

                    val measuredHeight = ceil(
                        child.measuredWidth * abs(sin(circleAngle))
                            + child.measuredHeight * abs(cos(circleAngle))
                    ).toInt()

                    setMeasuredDimension(
                        resolveSize(measuredWidth, widthMeasureSpec),
                        resolveSize(measuredHeight, heightMeasureSpec)
                    )
                }
            }
        } ?: super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val layoutWidth = right - left
        val layoutHeight = bottom - top

        if (angleChanged || changed) {
            val layoutRect = tempRectF1
            val layoutRectRotated = tempRectF2

            layoutRect[0f, 0f, layoutWidth.toFloat()] = layoutHeight.toFloat()

            rotateMatrix.setRotate(angle.toFloat(), layoutRect.centerX(), layoutRect.centerY())
            rotateMatrix.mapRect(layoutRectRotated, layoutRect)

            layoutRectRotated.round(viewRectRotated)

            angleChanged = false
        }

        view?.let { child ->
            val childLeft = (layoutWidth - child.measuredWidth) / 2
            val childTop = (layoutHeight - child.measuredHeight) / 2
            val childRight = childLeft + child.measuredWidth
            val childBottom = childTop + child.measuredHeight
            child.layout(childLeft, childTop, childRight, childBottom)
        }
    }

    override fun dispatchDraw(canvas: Canvas) = with(canvas) {
        save()
        rotate(-angle.toFloat(), width / 2f, height / 2f)
        super.dispatchDraw(this)
        restore()
    }

    override fun onDescendantInvalidated(child: View, target: View) {
        invalidate()
        super.onDescendantInvalidated(child, target)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        viewTouchPoint[0] = event.x
        viewTouchPoint[1] = event.y

        rotateMatrix.mapPoints(childTouchPoint, viewTouchPoint)
        event.setLocation(childTouchPoint[0], childTouchPoint[1])
        val result = super.dispatchTouchEvent(event)
        event.setLocation(viewTouchPoint[0], viewTouchPoint[1])
        return result
    }

    /**
     * Circle angle, from 0 to TAU
     */
    private val circleAngle: Double
        get() = TAU * angle / 360

    companion object {
        private const val TAU = 2 * PI
    }
}
