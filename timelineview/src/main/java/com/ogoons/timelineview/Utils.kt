package com.ogoons.timelineview

import android.content.Context
import android.util.TypedValue


/**
 * Created by OGOONS on 2017. 11. 20..
 */
object Utils {

    fun spToPx(context: Context, sp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics)
    }

    fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun pxToDp(context: Context, pixel: Int): Float {
        val metrics = context.resources.displayMetrics
        return pixel / (metrics.densityDpi / 160f)
    }
}