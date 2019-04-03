package com.mofe.activities

import android.os.Bundle
import com.mofe.R

/**
 * Created by ${cosmic} on 3/22/19.
 */

open class about_activity : home_activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noStatusBar()

        setContentView(R.layout.activity_about)
    }
}