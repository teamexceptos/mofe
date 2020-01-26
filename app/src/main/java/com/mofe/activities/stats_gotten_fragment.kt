package com.mofe.activities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import com.mofe.R
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Cate
import com.mofe.database.entities.Items
import com.mofe.utils.Togetutil
import kotlinx.android.synthetic.main.fragment_stats_gotten.*
import kotlinx.android.synthetic.main.fragment_stats_toget.*

class stats_gotten_fragment : Fragment() {

    var itemperc : Int = 0
    var Prefs : SharedPreferences? = null
    open val CUSTOM_PREF_NAME = "amount_data"

    val togetutil = Togetutil()
    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<LinearLayout>

    lateinit var database_stats : AppDatabase

    var notgotten_items_dates: ArrayList<Items> = ArrayList()
    var gotten_items_dates: ArrayList<Items> = ArrayList()
    var date_gotten_item_counts: ArrayList<String> = ArrayList()
    var cate_gotten_item_counts: ArrayList<String> = ArrayList()
    var cates_list: ArrayList<Cate> = ArrayList()
    var count_list_cate_2: ArrayList<Int> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_stats_gotten, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database_stats = AppDatabase.getInstance(context!!)
        cates_list = AppDatabase.getInstance(context!!).CateDao().all as ArrayList<Cate>

        notgotten_items_dates = database_stats.ItemsDao().loadAllByGotten(itemisGotten = "no") as ArrayList<Items>
        gotten_items_dates = database_stats.ItemsDao().loadAllByGotten(itemisGotten = "yes") as ArrayList<Items>

        for (g in gotten_items_dates){
            date_gotten_item_counts.add(g.itemDueDate!!)
            cate_gotten_item_counts.add(g.itemCate!!)
        }

        for (unique_cate_g in cate_gotten_item_counts.distinct()){
            count_list_cate_2.add(cate_gotten_item_counts.count { it == unique_cate_g })
        }

        if (count_list_cate_2.size != 0) {

            stats_status_gotten.text = "Total of " + count_list_cate_2.size.toString() + " categories with different items"

            renderPieChart_g_Cate()

        } else {

            stats_status_gotten.text = "Items gotten in all categories are empty"
        }
    }

    fun renderPieChart_g_Cate() {

        val yVentries = ArrayList<Entry>()
        var x = 0

        count_list_cate_2.forEach {
            yVentries.add(Entry(it.toFloat(), x))
            x += 1
        }

        val androidColors = context!!.resources.getIntArray(R.array.random_color)

        val pieDataSet = PieDataSet(yVentries, "")
        pieDataSet.setSliceSpace(3f)
        pieDataSet.setSelectionShift(5f)
        pieDataSet.setValueFormatter(PercentFormatter())
        pieDataSet.setColors(androidColors.slice(0..count_list_cate_2.size))
        pieDataSet.setValueTextColor(Color.WHITE)
        pieDataSet.valueTextSize = 10f

        val pieData = PieData(cate_gotten_item_counts.distinct(), pieDataSet)

        pie_gotten_cate_chart.rotationAngle = 0f
        pie_gotten_cate_chart.isRotationEnabled = true

        pie_gotten_cate_chart.data = pieData
        pie_gotten_cate_chart.setUsePercentValues(true)
        pie_gotten_cate_chart.setCenterTextSize(5f)
        pie_gotten_cate_chart.setCenterTextColor(Color.WHITE)
        pie_gotten_cate_chart.setHoleColor(Color.WHITE)
        pie_gotten_cate_chart.setDescription("categories of items gotten")

        pie_gotten_cate_chart.invalidate()

    }
}