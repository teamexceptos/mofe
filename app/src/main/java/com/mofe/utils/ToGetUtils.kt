package com.mofe.utils

import android.annotation.SuppressLint
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ${cosmic} on 2/10/19.
 */


/**
 * Convert formatted Date
 * */
@SuppressLint("SimpleDateFormat")
fun getFormatDate(inputDate: String): String {

    val inputFormat = SimpleDateFormat("yyyy-MM-dd")
    val outputFormat = SimpleDateFormat("EEE, d MMM yyyy")

    var date: Date? = null

    try {
        date = inputFormat.parse(inputDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    val outputDate = outputFormat.format(date)
    return outputDate
}

/**
 * Convert formatted Time
 * */
@SuppressLint("SimpleDateFormat")
fun getFormatTime(inputTime: String): String {

    val inputFormat = SimpleDateFormat("HH:mm") // HH:mm:ss
    val outputFormat = SimpleDateFormat("h:mm a")

    var date: Date? = null

    try {
        date = inputFormat.parse(inputTime)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    val outputTime = outputFormat.format(date)
    return outputTime
}


/**
 * Convert formatted Number
 * */
fun NumberAmountFormat(inputAmt: Int): String? {

    return NumberFormat.getNumberInstance(Locale.US).format(inputAmt)
}

/**
 * Convert current time to String
 * */
fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

/**
 * Get current time
 * */
fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

//fun sortingforItmes(list: List<Items>){
//
//    Collections.sort<Items>(list, object : Comparator<Items> {
//        override fun compare(lhs: Items, rhs: Items): Int {
//            return java.lang.Long.compare(rhs., lhs.)
//        }
//    })
//}