package me.thet.slidebutton

import android.content.Context

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
private fun Context.sp(sp: Float): Float {
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