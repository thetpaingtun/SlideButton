package me.thet.slidebutton

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 *
 * Created by thet on 18/4/2020.
 *
 */

/**
 * convert sp to px using scaledensity
 *
 * @param sp to be converted
 * @return px
 */
fun Context.sp(sp: Float): Float {
    return resources.displayMetrics.scaledDensity * sp
}

/**
 * convert dp to px using density
 *
 * @param dp to be converted
 * @return
 */
fun Context.dp(dp: Float): Float {
    return resources.displayMetrics.density * dp
}

fun Context.getColorRes(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}