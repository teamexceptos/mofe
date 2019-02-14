package com.mofe.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by ${cosmic} on 2/6/19.
 */

object SharedprefManager {

    val AMOUNT = "AMOUNT"
    val INIT_AMOUNT = "INIT_AMOUNT"
    val LASTDATE = "LAST_DATE"

    fun customPreference(context: Context, name: String):
            SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    fun SharedPreferences.Editor.put(pair: Pair<String, Any>) {
        val key = pair.first
        val value = pair.second
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> error("Only primitive types can be stored in SharedPreferences")
        }
    }

    var SharedPreferences.amount
        get() = getInt(AMOUNT, 0)
        set(value) {
            editMe {
                it.putInt(AMOUNT, value)
            }
        }

    var SharedPreferences.init_amount
        get() = getInt(INIT_AMOUNT, 0)
        set(value) {
            editMe {
                it.putInt(INIT_AMOUNT, value)
            }
        }

    var SharedPreferences.lastdate
        get() = getString(LASTDATE, "")
        set(value) {
            editMe {
                it.putString(LASTDATE, value)
            }
        }

    var SharedPreferences.clearValues
        get() = { }
        set(value) {
            editMe {

                it.clear()
            }
        }
}