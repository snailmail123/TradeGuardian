package com.penguinstudios.tradeguardian.util

import android.content.Context
import android.util.TypedValue

object SpacingUtil {

    fun convertIntToDP(context: Context, value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}