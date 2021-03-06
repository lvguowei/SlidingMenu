package com.example.slidingmenu
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * 获得屏幕高度
 *
 * @param context
 * @return
 */
fun getScreenWidth(context: Context): Int {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(outMetrics)
    return outMetrics.widthPixels
}

/**
 * Dip into pixels
 */
fun dip2px(context: Context, dpValue: Float): Float {
    val scale = context.resources.displayMetrics.density
    return dpValue * scale + 0.5f
}
