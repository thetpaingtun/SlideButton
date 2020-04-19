package me.thet.slidebutton

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.*
import androidx.core.content.ContextCompat
import java.lang.Math.pow

/**
 *
 * Created by thet on 8/4/2020.
 *
 */
class SlideButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mDrawableArrow: Drawable?
    private var drawableSize: Float

    private var containerBorderRadius: Float = 0f
    private var innerBorderRadius: Float = 0f
    private var MARGIN_BETWEEN_OUTER_INNER = context.dp(6f)

    private val path: Path
    private val paint: Paint
    private val containerPaint: Paint

    private val vc: ViewConfiguration
    private val touchSlop: Int

    private var outContainerPos = 0f

    private var mCurrentCx: Float = 0f
        set(value) {
            field = value
            mDragPercent = (((field - mCxStart) / (mCxEnd - mCxStart)) * 100).toInt()
        }
    private var mInitCy: Float = 0f

    //the range that inner circle can be dragged
    private var mCxStart: Float = 0.0f
    private var mCxEnd: Float = 0f

    private var mDragPercent = 0


    private val DEFAULT_HEIGHT = context.dp(65f).toInt()
    private val DEFAULT_WIDTH = context.dp(300f).toInt()

    private var isDragComplete = false


    private var lastX = 0f


    init {
        path = Path()
        paint = Paint().apply {
            strokeWidth = 6f
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
            strokeJoin = Paint.Join.ROUND
        }

        containerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                style = Paint.Style.FILL
            }


        drawableSize = context.dp(20f)

        vc = ViewConfiguration.get(context)
        touchSlop = vc.scaledTouchSlop


        mDrawableArrow = ContextCompat.getDrawable(context, R.drawable.ic_arrows)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = resolveSize(DEFAULT_HEIGHT, heightMeasureSpec)
        val width = resolveSize(DEFAULT_WIDTH, widthMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return

        canvas.drawRoundRect(
            0f + outContainerPos,
            0f,
            width.toFloat() - outContainerPos,
            height.toFloat(),
            containerBorderRadius,
            containerBorderRadius,
            containerPaint
        )


        canvas.drawCircle(mCurrentCx, mInitCy, innerBorderRadius, paint)


        val left = (mCurrentCx - (drawableSize / 2)).toInt()
        val top = (mInitCy - (drawableSize / 2)).toInt()
        val right = left + drawableSize.toInt()
        val bottom = top + drawableSize.toInt()

        mDrawableArrow?.setBounds(
            left,
            top,
            right,
            bottom
        )
        if (innerBorderRadius != 0f) {
            mDrawableArrow?.draw(canvas)
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        containerBorderRadius = (height.toFloat() / 2)

        innerBorderRadius = containerBorderRadius - MARGIN_BETWEEN_OUTER_INNER


        mCurrentCx = innerBorderRadius + MARGIN_BETWEEN_OUTER_INNER
        mInitCy = h / 2f

        mCxStart = mCurrentCx
        mCxEnd = w - MARGIN_BETWEEN_OUTER_INNER - innerBorderRadius
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val eventX = event.x
            val eventY = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = eventX
                    if (!isDragComplete && isInsideCircle(
                            mCurrentCx,
                            mInitCy,
                            eventX,
                            eventY,
                            innerBorderRadius
                        )
                    ) {
                        return true
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val diffX = eventX - lastX
                    lastX = eventX
                    movePosition(diffX)
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    onActionUp()
                    return true
                }
                else -> return false
            }
        }
        return super.onTouchEvent(event)
    }

    private fun onActionUp() {
        if (mDragPercent < 100) {
            rollBackToStartPosition()
        } else {
            onDragCompleted()
        }
    }

    private fun onDragCompleted() {
        isDragComplete = true

        val innerCircleAnimator = ValueAnimator.ofFloat(innerBorderRadius, 0f)

        innerCircleAnimator.apply {
            addUpdateListener {
                innerBorderRadius = it.animatedValue as Float
                invalidate()
            }
            startDelay = 100
            duration = 100
        }


        val collapseAnimator = ValueAnimator.ofFloat(0f, (width - height) / 2f)
        collapseAnimator.addUpdateListener {
            outContainerPos = it.animatedValue as Float
            invalidate()
        }

        collapseAnimator.apply {
            duration = 400
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(innerCircleAnimator, collapseAnimator)
        animatorSet.start()


    }

    private fun rollBackToStartPosition() {
        val animator = ValueAnimator.ofFloat(mCurrentCx, mCxStart)

        animator.addUpdateListener {
            mCurrentCx = it.animatedValue as Float
            invalidate()
        }

        animator.duration = 300
        animator.start()
    }

    private fun movePosition(diff: Float) {
        val newPos = mCurrentCx + diff
        mCurrentCx += diff
        mCurrentCx =
            if (newPos < mCxStart) {
                mCxStart
            } else if (newPos > mCxEnd) {
                mCxEnd
            } else {
                newPos
            }
    }

    private fun isInsideCircle(
        circleX: Float,
        circleY: Float,
        touchX: Float,
        touchY: Float,
        radius: Float
    ): Boolean {

        return Math.sqrt(
            pow(
                (touchX - circleX).toDouble(),
                2.0
            ) + pow((touchY - circleY).toDouble(), 2.0)
        ) < radius
    }
}