package me.thet.slidebutton

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.*
import android.view.animation.Animation
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

    private var mPrimaryColorLight: Int = 0
    private var mPrimaryColor: Int = 0
    private var mColorOnPrimary: Int = 0

    private var MARGIN_BETWEEN_CONTAINER_CURSOR = context.dp(6f)
    private var MARGIN_BETWEEN_CURSOR_SHADOW = context.dp(10f)

    private val DEFAULT_HEIGHT = context.dp(65f).toInt()
    private val DEFAULT_WIDTH = context.dp(300f).toInt()


    private var mProgressSweepAngle: Float = 240f
    private var mProgressStartAngle: Float = 0f
    private var mProgressAlpha: Int = 0
    private lateinit var mProgressRect: RectF
    private var mProgressDrawableSize: Float

    private var mCursorCircleBorderRadius: Float = 0f


    private var mDrawableCursor: Drawable?
    private var mCursorDrawableSize: Float


    private var mContainerBorderRadius: Float = 0f
    private var mContainerPos = 0f


    private val mCursorPaint: Paint
    private val mContainerPaint: Paint
    private val mProgressPaint: Paint
    private val mLabelPaint: Paint
    private val mShadowPaint: Paint

    private val vc: ViewConfiguration
    private val touchSlop: Int


    private var drawShadow: Boolean = false


    private var mCurrentCursorX: Float = 0f
        set(value) {
            field = value
            mDragPercent = (((field - mCursorStartX) / (mCursorEndX - mCursorStartX)) * 100).toInt()
            if (mDragPercent > 100 || mDragPercent < 0) {
                mDragPercent = 0
            }
        }
    private var mCursorY: Float = 0f

    //the range that cursor circle can be dragged
    private var mCursorStartX: Float = 0.0f
    private var mCursorEndX: Float = 0f

    private var mDragPercent = 0


    private var isDragComplete = false
    private var lastX = 0f


    private var mLabel = "Order Collected"
    private var mLabelStartX: Float = 0f
    private var mLabelStartY: Float = 0f
    private var mLabelSize: Float

    var onDragListener: OnDragListener? = null

    interface OnDragListener {
        fun onDragCompleted()
    }

    init {

        mCursorDrawableSize = context.dp(20f)
        mProgressDrawableSize = context.dp(24f)


        mDrawableCursor = ContextCompat.getDrawable(context, R.drawable.ic_arrows)


        vc = ViewConfiguration.get(context)
        touchSlop = vc.scaledTouchSlop


        mLabelSize = context.sp(14f)

        retrieveAttrs(attrs)

        mDrawableCursor?.setTint(mPrimaryColor)


        mCursorPaint = Paint().apply {
            strokeWidth = 6f
            color = mColorOnPrimary
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
            strokeJoin = Paint.Join.ROUND
        }

        mContainerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = mPrimaryColor
                style = Paint.Style.FILL
            }

        mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = mColorOnPrimary
                strokeWidth = context.dp(4f)
                style = Paint.Style.STROKE
            }

        mLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = mLabelSize
            color = mColorOnPrimary
        }

        mShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mPrimaryColor
            style = Paint.Style.FILL
        }

    }

    private fun retrieveAttrs(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.SlideButtonView, 0, 0).apply {
            try {
                mDrawableCursor =
                    getDrawable(R.styleable.SlideButtonView_sb_cursor_icon) ?: mDrawableCursor

                mCursorDrawableSize =
                    getDimension(
                        R.styleable.SlideButtonView_sb_cursor_icon_size,
                        mCursorDrawableSize
                    )

                mProgressDrawableSize = getDimension(
                    R.styleable.SlideButtonView_sb_progress_icon_size,
                    mProgressDrawableSize
                )

                mLabel = getString(R.styleable.SlideButtonView_sb_label) ?: mLabel

                mLabelSize = getDimension(R.styleable.SlideButtonView_sb_label_size, mLabelSize)

                mPrimaryColor =
                    getColor(
                        R.styleable.SlideButtonView_sb_primary_color,
                        context.getColorRes(R.color.colorGreen)
                    )

                mPrimaryColorLight = getColor(
                    R.styleable.SlideButtonView_sb_primary_color_light,
                    context.getColorRes(R.color.colorGreenLight)
                )

                mColorOnPrimary = getColor(
                    R.styleable.SlideButtonView_sb_color_on_primary,
                    context.getColorRes(R.color.white)
                )

            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = resolveSize(DEFAULT_HEIGHT, heightMeasureSpec)
        val width = resolveSize(DEFAULT_WIDTH, widthMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return


        //outer container
        canvas.drawRoundRect(
            0f + mContainerPos,
            0f,
            width.toFloat() - mContainerPos,
            height.toFloat(),
            mContainerBorderRadius,
            mContainerBorderRadius,
            mContainerPaint
        )

        //draw the label
        mLabelPaint.alpha = ((100f - mDragPercent) / 100f * 255f).toInt()
        canvas.drawText(mLabel, mLabelStartX, mLabelStartY, mLabelPaint)

        if (drawShadow) {
            var cursorShadowRight =
                mCurrentCursorX + (mCursorCircleBorderRadius) + MARGIN_BETWEEN_CURSOR_SHADOW

            if (cursorShadowRight >= width) {
                cursorShadowRight = width.toFloat()
            }

            //cursor shadow
            canvas.drawRoundRect(
                0f,
                0f,
                cursorShadowRight,
                height.toFloat(),
                mContainerBorderRadius,
                mContainerBorderRadius,
                mShadowPaint
            )
        }


        //base cursor circle
        canvas.drawCircle(mCurrentCursorX, mCursorY, mCursorCircleBorderRadius, mCursorPaint)


        //draw cursor icon
        val left = (mCurrentCursorX - (mCursorDrawableSize / 2)).toInt()
        val top = (mCursorY - (mCursorDrawableSize / 2)).toInt()
        val right = left + mCursorDrawableSize.toInt()
        val bottom = top + mCursorDrawableSize.toInt()

        mDrawableCursor?.setBounds(
            left,
            top,
            right,
            bottom
        )


        if (mCursorCircleBorderRadius != 0f) {
            mDrawableCursor?.draw(canvas)
        }


        //progress circle ( will appear after dragged 100%)
        mProgressPaint.alpha = mProgressAlpha
        canvas.drawArc(
            mProgressRect, mProgressStartAngle,
            mProgressSweepAngle, false, mProgressPaint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mContainerBorderRadius = (height.toFloat() / 2)

        mCursorCircleBorderRadius = mContainerBorderRadius - MARGIN_BETWEEN_CONTAINER_CURSOR


        mCurrentCursorX = mCursorCircleBorderRadius + MARGIN_BETWEEN_CONTAINER_CURSOR
        mCursorY = h / 2f

        mCursorStartX = mCurrentCursorX
        mCursorEndX = w - MARGIN_BETWEEN_CONTAINER_CURSOR - mCursorCircleBorderRadius

        val progressLeft = (width / 2) - (mProgressDrawableSize / 2)
        val progressRight = progressLeft + mProgressDrawableSize
        val progressTop = (height / 2) - (mProgressDrawableSize / 2)
        val progressBottom = progressTop + mProgressDrawableSize
        mProgressRect = RectF(
            progressLeft,
            progressTop,
            progressRight,
            progressBottom
        )

        val labelWidth = mLabelPaint.measureText(mLabel)
        val labelBound = Rect()
        mLabelPaint.getTextBounds(mLabel, 0, mLabel.length, labelBound)
        val labelHeight = labelBound.height()

        mLabelStartX = (width / 2) - (labelWidth / 2)
        mLabelStartY = (height / 2) + (labelHeight / 2f)


        mContainerPaint.shader = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            intArrayOf(
                mPrimaryColorLight,
                mPrimaryColor,
                mPrimaryColor
            ),
            null,
            Shader.TileMode.MIRROR
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val eventX = event.x
            val eventY = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = eventX
                    if (!isDragComplete && isInsideCircle(
                            mCurrentCursorX,
                            mCursorY,
                            eventX,
                            eventY,
                            mCursorCircleBorderRadius
                        )
                    ) {
                        drawShadow = true
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
                    drawShadow = false
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

        val innerCircleAnimator = ValueAnimator.ofFloat(mCursorCircleBorderRadius, 0f)

        //remove inner circle
        innerCircleAnimator.apply {
            addUpdateListener {
                mCursorCircleBorderRadius = it.animatedValue as Float
                invalidate()
            }
            startDelay = 100
            duration = 100
        }


        //collapse container
        val collapseAnimator = ValueAnimator.ofFloat(0f, (width - height) / 2f)
        collapseAnimator.addUpdateListener {
            mContainerPos = it.animatedValue as Float
            invalidate()
        }

        collapseAnimator.duration = 400


        // progress circle visibility
        val progressVisibilityAnimator = ValueAnimator.ofInt(0, 255)
        progressVisibilityAnimator.addUpdateListener {
            mProgressAlpha = it.animatedValue as Int
            invalidate()
        }
        progressVisibilityAnimator.duration = 300

        val progressRotationAnimator = ValueAnimator.ofFloat(0f, 360f)
        progressRotationAnimator.addUpdateListener {
            mProgressStartAngle = it.animatedValue as Float
            invalidate()
        }
        progressRotationAnimator.repeatCount = Animation.INFINITE
        progressRotationAnimator.duration = 300

        val progressAnimatorSet = AnimatorSet()
        progressAnimatorSet.playTogether(progressVisibilityAnimator, progressRotationAnimator)


        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(
            innerCircleAnimator,
            collapseAnimator,
            progressAnimatorSet
        )
        animatorSet.start()
        collapseAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                onDragListener?.onDragCompleted()
            }
        })
    }

    private fun rollBackToStartPosition() {
        val animator = ValueAnimator.ofFloat(mCurrentCursorX, mCursorStartX)

        animator.addUpdateListener {
            mCurrentCursorX = it.animatedValue as Float
            invalidate()
        }

        animator.duration = 300
        animator.start()
    }

    private fun movePosition(diff: Float) {
        val newPos = mCurrentCursorX + diff
        mCurrentCursorX += diff
        mCurrentCursorX =
            if (newPos < mCursorStartX) {
                mCursorStartX
            } else if (newPos > mCursorEndX) {
                mCursorEndX
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