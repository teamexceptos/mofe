package com.mofe.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.mofe.R
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Items
import com.mofe.utils.SharedprefManager
import com.mofe.utils.SharedprefManager.amount
import com.mofe.utils.SharedprefManager.customPreference
import com.mofe.utils.SharedprefManager.init_amount
import com.mofe.utils.SharedprefManager.spentamount
import com.mofe.utils.Togetutil
import kotlinx.android.synthetic.main.activity_new_home.*
import kotlinx.android.synthetic.main.bottomsheet_options.*
import kotlinx.android.synthetic.main.bottomsheet_update_amount.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
 * Created by ${cosmic} on 2/10/19.
 */

open class home_activity : AppCompatActivity() {

    open val CUSTOM_PREF_NAME = "amount_data"
    open val mActivity: Activity = this@home_activity
    val togetutil = Togetutil()

    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<LinearLayout>
    private var bottomSheetStateForOnBackPressed = BottomSheetBehavior.STATE_HALF_EXPANDED
    val context: Context = this
    var perc: Int = 0
    var totalAmt:Int = 0
    var total_reduction: Int = 0

    protected lateinit var r: Resources
    protected var px: Float = 0.toFloat()
    protected var width: Int = 0
    protected var height: Int = 0

    lateinit var database : AppDatabase

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        noStatusBar()

        setContentView(R.layout.activity_new_home)

        val Prefs = customPreference(this, CUSTOM_PREF_NAME)

        r = resources
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, r.displayMetrics)
        width = r.displayMetrics.widthPixels
        height = r.displayMetrics.heightPixels

        database = AppDatabase.getInstance(context = this@home_activity)

        doAsync {

            val items = database.ItemsDao().loadAllByGotten("no")
            var item_to_get_price_Sum = 0

            items.forEach {
                item_to_get_price_Sum += it.itemPrice!!
            }

            val items2 = database.ItemsDao().loadAllByGotten("yes")
            var item_gotten_price_Sum = 0

            items2.forEach {
                item_gotten_price_Sum += it.itemPrice!!
            }

            val debtors = database.DebtorsDao().all
            var debtors_Sum = 0

            debtors.forEach {
                debtors_Sum += it.debtAmount!!
            }

            total_reduction = item_to_get_price_Sum + item_gotten_price_Sum + debtors_Sum
            Prefs!!.spentamount = total_reduction

            uiThread {

                item_to_get_count_tvw.text = items.size.toString()
                ttl_amnt_toget.text = togetutil.NumberAmountFormat(item_to_get_price_Sum)

                item_gotten_count_tvw.text = items2.size.toString()
                ttl_amnt_item_gotten.text = togetutil.NumberAmountFormat(item_gotten_price_Sum)

                debtors_count_tvw.text = debtors.size.toString()
                ttl_amnt_debtors.text = togetutil.NumberAmountFormat(debtors_Sum)

                amt_reduction.text = togetutil.NumberAmountFormat(Prefs.init_amount - total_reduction)
                init_amt_input.text = togetutil.NumberAmountFormat(Prefs.init_amount)

                totalAmt = Prefs.init_amount
                Prefs.amount = Prefs.init_amount - total_reduction

                circularProgressBar(Prefs.init_amount, total_reduction)
            }
        }

        bottomSheet = findViewById(R.id.bottomsheet_options_view_inc)
        bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet)

        fab_bottomsheet_option_action.setOnClickListener {

            bottomSheetBehaviourStateInit(bottomSheetBehaviour)
            bottomSheetBehaviourInit(bottomSheetBehaviour)

            bottomsheet_options_init()
        }

        bottomsheet_amount_to_update.text = togetutil.NumberAmountFormat(Prefs!!.init_amount)

        update_income_layout_action.setOnClickListener {

            bottomSheetBehaviourStateInit(bottomSheetBehaviour)
            bottomSheetBehaviourInit(bottomSheetBehaviour)

            bottomsheet_update_amount_init()
        }

        add_new_item_action.setOnClickListener {
            startActivity(Intent(this, add_Item_activity::class.java))
            finish()
        }

        history_layout_action.setOnClickListener {
            startActivity(Intent(this, history_tabs_fragment::class.java))
            finish()
        }

        debtors_layout_action.setOnClickListener {
            startActivity(Intent(this, add_debtor_activity::class.java))
            finish()
        }

        update_amount_action.setOnClickListener {

            if (edtAmt.text!!.isEmpty()) {

                toast("Please, input the amount you're adding to your inital amount")

            } else {

                Prefs.init_amount = Prefs.init_amount + edtAmt.text.toString().toInt()
                Prefs.amount = Prefs.amount + edtAmt.text.toString().toInt()

                init_amt_input.text = togetutil.NumberAmountFormat(Prefs.init_amount)
                amt_reduction.text = togetutil.NumberAmountFormat(Prefs.amount)

                circularProgressBar(Prefs.init_amount, Prefs.spentamount)

                bottomsheet_options_init()
            }
        }

        about_layout_action.setOnClickListener {
            startActivity(Intent(this, about_activity::class.java))
        }

        stats_layout_action.setOnClickListener {
            startActivity(Intent(this, stats_tabs_fragment::class.java))
        }
    }

    fun checkLowCashtoTrack(context: Context) {

        val Prefs = customPreference(context, CUSTOM_PREF_NAME)

        if(Prefs!!.amount <= (Prefs.init_amount).div(4)) {
            Prefs.init_amount = (Prefs.init_amount).div(4)
        }
    }

    @SuppressLint("NewApi", "SetTextI18n")
    fun circularProgressBar(amount: Int, spentamount: Int){

        cp_bar.run {
            setRounded(true)
            setMaxProgress(amount.toFloat())
            setProgressWidth(36.0F)
            setProgress(spentamount.toFloat())
            perc = getProgressPercentage()
            actual_money_spent.text = "$perc%"

            if(perc >= 80) {
                setProgressColor(context.getColor(R.color.red))

            } else {

                setProgressColor(context.getColor(R.color.lime_progress_100))
            }

            setProgressBackgroundColor(context.getColor(R.color.colorWhite))
        }
    }

    fun bottomsheet_options_init() {

        update_amount_view_inc.visibility = View.GONE
        bottomsheet_options_view_inc.visibility = View.VISIBLE

        bottomSheet = findViewById(R.id.bottomsheet_options_view_inc)
        bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehaviourStateInit(bottomSheetBehaviour)
    }

    fun bottomsheet_update_amount_init() {

        bottomsheet_options_view_inc.visibility = View.GONE
        update_amount_view_inc.visibility = View.VISIBLE

        bottomSheet = findViewById(R.id.update_amount_view_inc)
        bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehaviourInit(bottomSheetBehaviour)
    }

    fun noStatusBar() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun bottomSheetBehaviourInit(bottomSheetBehaviour: BottomSheetBehavior<LinearLayout>){

        bottomSheetBehaviour.isFitToContents = true

        bottomSheetBehaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(p0: View, p1: Float) {

                when (p1) {

                    1f -> {
                        if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetBehaviour.peekHeight.toFloat() == 70 * px){
                            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }

                    -1f -> {
                        if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED ){
                            bottomSheetBehaviour.state = BottomSheetBehavior.PEEK_HEIGHT_AUTO
                        }
                    }
                }
            }

            override fun onStateChanged(p0: View, p1: Int) {
                bottomSheetStateForOnBackPressed = p1
            }
        })
    }

    fun bottomSheetBehaviourStateInit(bottomSheetBehaviour: BottomSheetBehavior<LinearLayout>){

        if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {

            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED

        } else if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_COLLAPSED) {

            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}

