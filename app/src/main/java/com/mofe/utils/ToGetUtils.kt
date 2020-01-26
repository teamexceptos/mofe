package com.mofe.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mofe.database.entities.Debtors
import com.mofe.database.entities.Items
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ${cosmic} on 2/10/19.
 */

open class Togetutil : AppCompatActivity() {

    protected var hasCallPermissions: Boolean = false
    protected var hasAllPermissions: Boolean = false
    protected val REQUEST_PERMISSIONS_CALL = 7
    protected val REQUEST_PERMISSIONS = 1

    private var permissionListener: PermissionListener? = null

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

    fun showSnackbar(view: View, mainTextStringId: Int, actionStringId: Int, listener: View.OnClickListener) { Snackbar.make (
                view.findViewById(android.R.id.content),
                view.context.getString(mainTextStringId),
                Snackbar.LENGTH_LONG)
                .setAction(view.context.getString(actionStringId), listener)
                .show()
    }

    /**
     * Get current time
     * */
    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    /**
     * Sorting item list with time long
     * */
    fun sortingforItmes(list: List<Items>){

        Collections.sort<Items>(list, object : Comparator<Items> {
            override fun compare(lhs: Items, rhs: Items): Int {
                return java.lang.Long.compare(rhs.itemLongDateAdded!!, lhs.itemLongDateAdded!!)
            }
        })
    }

    fun sortingforDebtors(list: List<Debtors>){

        Collections.sort<Debtors>(list, object : Comparator<Debtors> {
            override fun compare(lhs: Debtors, rhs: Debtors): Int {
                return java.lang.Long.compare(rhs.debtAddDate!!, lhs.debtAddDate!!)
            }
        })
    }

    fun getCallPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.CALL_PHONE)
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CALL)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS) {
            hasAllPermissions = (grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                    && grantResults[5] == PackageManager.PERMISSION_GRANTED
                    && grantResults[6] == PackageManager.PERMISSION_GRANTED
                    && grantResults[7] == PackageManager.PERMISSION_GRANTED
                    && grantResults[8] == PackageManager.PERMISSION_GRANTED
                    && grantResults[9] == PackageManager.PERMISSION_GRANTED)
            try {
                permissionListener!!.onPermissionCheckCompleted(requestCode, hasAllPermissions)
            } catch (ignored: Exception) {

            }
        }

        if (requestCode == REQUEST_PERMISSIONS_CALL) {
            if (grantResults.size == 1) {
                hasCallPermissions = grantResults[0] == PackageManager.PERMISSION_GRANTED

                try {

                    permissionListener!!.onPermissionCheckCompleted(requestCode, hasCallPermissions)

                } catch (ignored: Exception) {

                }
            }
        }
    }
}

/**
 * Convert current time to String
 * */
fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}