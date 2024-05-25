package com.dam.wewiza_front.Formatter

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class MonthAxisValueFormatter(private val monthMap: Map<Float, String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return monthMap[value] ?: value.toString()
    }
}
