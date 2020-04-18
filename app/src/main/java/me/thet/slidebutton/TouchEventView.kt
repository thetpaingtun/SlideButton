package me.thet.slidebutton

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
class TouchEventView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mDrawableArrow: Drawable?
    private var drawableSize: Float

    private var borderRadius: Float = 0f
    private var innerBorderRadius: Float = 0f
    private var MARGIN_BETWEEN_OUTER_INNER = context.dp(6f)

    private val path: Path
    private val paint: Paint
    private val containerPaint: Paint

    private val vc: ViewConfiguration
    private val touchSlop: Int

    private var mInitCx: Float = 0f
    private var mInitCy: Float = 0f

    //the range that inner circle can be dragged
    private var mCxStart: Float = 0.0f
    private var mCxEnd: Float = 0f


    private val DEFAULT_HEIGHT = context.dp(65f).toInt()


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
        val width = resolveSize(width, widthMeasureSpec)
        setMeasuredDimension(width, height)

        Logger.d("on measure =>")
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return

        canvas.drawRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            borderRadius,
            borderRadius,
            containerPaint
        )

        canvas.drawCircle(mInitCx, mInitCy, innerBorderRadius, paint)


        val left = (mInitCx - (drawableSize / 2)).toInt()
        val top = (mInitCy - (drawableSize / 2)).toInt()
        val right = left + drawableSize.toInt()
        val bottom = top + drawableSize.toInt()



        mDrawableArrow?.setBounds(
            left,
            top,
            right,
            bottom
        )
        mDrawableArrow?.draw(canvas);


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        borderRadius = (DEFAULT_HEIGHT.toFloat() / 2)

        innerBorderRadius = borderRadius - MARGIN_BETWEEN_OUTER_INNER


        mInitCx = innerBorderRadius + MARGIN_BETWEEN_OUTER_INNER
        mInitCy = h / 2f

        mCxStart = mInitCx
        mCxEnd = w - MARGIN_BETWEEN_OUTER_INNER - innerBorderRadius
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val eventX = event.x
            val eventY = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Logger.d(
                        "inside => ${isInsideCircle(
                            mInitCx,
                            mInitCy,
                            eventX,
                            eventY,
                            100f
                        )}"
                    )
                    Logger.d("inside => $eventX")
                    lastX = eventX
                    if (isInsideCircle(mInitCx, mInitCy, eventX, eventY, innerBorderRadius)) {
                        return true
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (Math.abs(eventX - lastX) > touchSlop && eventX > mCxStart && eventX < mCxEnd) {
                        mInitCx = eventX
                        lastX = eventX
                        invalidate()
                        return true
                    }
                    return false
                }
                else -> return false
            }
        }
        return super.onTouchEvent(event)
    }

    private fun isInsideCircle(
        circleX: Float,
        circleY: Float,
        touchX: Float,
        touchY: Float,
        radius: Float
    ): Boolean {
        return (pow((circleX - touchX).toDouble(), 2.0) + pow(
            (circleY - touchY).toDouble(),
            2.0
        )) <= pow(
            radius.toDouble(), 2.0
        )
    }

}