package com.mofe.utils

/**
 * Created by ${cosmic} on 4/13/19.
 */

interface PermissionListener {

    fun onPermissionCheckCompleted(requestCode: Int, isPermissionGranted: Boolean)
}