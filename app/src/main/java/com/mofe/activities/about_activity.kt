package com.mofe.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mofe.R
import kotlinx.android.synthetic.main.activity_about.*
import org.jetbrains.anko.toast

/**
 * Created by ${cosmic} on 3/22/19.
 */

open class about_activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)

        feedback_layout_vw.setOnClickListener {

            val mailIntent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto: " + "teamexpectos@gmail.com"))
            mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Subject")
            mailIntent.putExtra(Intent.EXTRA_TEXT, "Your text")

            try {

                startActivity(mailIntent)
            }
            catch (e: Exception) {
                toast("Sorry, you have no mailing app")
            }
        }
    }
}