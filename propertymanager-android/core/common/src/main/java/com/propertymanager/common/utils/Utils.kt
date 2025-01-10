package com.propertymanager.common.utils

import android.util.Log
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Date

object Utils {
    fun Double.getFraction(completedDays: Double): Float {
        return (completedDays / this).toFloat()
    }

    fun Number.toIndianFormat(includeDecimal: Boolean = false): String {
        if (this.toDouble() == 0.0) return "0"
        val numberFormat = DecimalFormat(if (includeDecimal) "##,##,###.00" else "##,##,###")
        return numberFormat.format(this)
    }

}
