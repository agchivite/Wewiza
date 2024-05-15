package com.dam.wewiza_front.Formatter

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class MonthAxisValueFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val monthIndex = ((value - 1) % 12).toInt() // Calcula el índice del mes dentro del rango 0-11
        return SimpleDateFormat("MMMM", Locale.getDefault()).format(getDateForMonth(monthIndex))
    }

    private fun getDateForMonth(month: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        return calendar.time
    }
}