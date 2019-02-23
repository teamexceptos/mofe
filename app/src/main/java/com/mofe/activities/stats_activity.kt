package com.mofe.activities

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mofe.R
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Items
import kotlinx.android.synthetic.main.activity_stats_views.*

/**
 * Created by ${cosmic} on 2/19/19.
 */

class stats_activity : home_activity() {

    lateinit var database_stats : AppDatabase

    val mStatsActivity: Activity = this@stats_activity
    val stats_context: Context = this

    var unique_notgotten_items_dates: ArrayList<Items> = ArrayList()
    var unique_gotten_items_dates: ArrayList<Items> = ArrayList()
    var date_gotten_item_counts: ArrayList<String> = ArrayList()
    var date_notgotten_item_counts: ArrayList<String> = ArrayList()
    var count_list_1: ArrayList<Int> = ArrayList()
    var count_list_2: ArrayList<Int> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noStatusBar()

        setContentView(R.layout.activity_stats_views)

        database_stats = AppDatabase.getInstance(context = this@stats_activity)

        unique_notgotten_items_dates = database_stats.ItemsDao().loadAllByGotten(itemisGotten = "no") as ArrayList<Items>

        unique_gotten_items_dates = database_stats.ItemsDao().loadAllByGotten(itemisGotten = "yes") as ArrayList<Items>


        for(ng_dates in unique_notgotten_items_dates){
           date_notgotten_item_counts.add(ng_dates.itemDueDate!!)
        }

        for(g_dates in unique_gotten_items_dates){
            date_gotten_item_counts.add(g_dates.itemDueDate!!)
        }

        for (unique_ng_date in date_notgotten_item_counts.distinct()){
            count_list_1.add(date_notgotten_item_counts.count { it == unique_ng_date })
        }

        for (unique_g_date in date_gotten_item_counts.distinct()){
            count_list_2.add(date_gotten_item_counts.count { it == unique_g_date })
        }


        renderLineChart_gottenItems()

    }

    @TargetApi(Build.VERSION_CODES.M)
    fun renderLineChart_gottenItems() {

        val entries = ArrayList<Entry>()
        var x = 0

        count_list_2.forEach {
            entries.add(Entry(it.toFloat(), x))
            x += 1
        }

        val lineDataSet = LineDataSet(entries, "Amount")
        lineDataSet.setDrawCubic(true)
        lineDataSet.setDrawFilled(true)
        lineDataSet.setDrawHighlightIndicators(true)
        lineDataSet.lineWidth = 1f
        lineDataSet.circleRadius = 3f
        lineDataSet.color = Color.parseColor("#EFEFFF")
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.setDrawCircles(true)
        lineDataSet.highLightColor = Color.WHITE
        lineDataSet.setDrawValues(false)

        val lineData = LineData(date_gotten_item_counts.distinct(), lineDataSet)
        item_gotten_linechart.data = lineData
        item_gotten_linechart.xAxis.setDrawGridLines(true)
        item_gotten_linechart.xAxis.gridColor = Color.parseColor("#EFEFFF")
        item_gotten_linechart.xAxis.gridLineWidth = 2f
        item_gotten_linechart.xAxis.axisLineColor = stats_context.getColor(R.color.colorWhite)
        item_gotten_linechart.axisLeft.setDrawGridLines(false)
        item_gotten_linechart.axisLeft.axisLineColor = stats_context.getColor(R.color.colorWhite)
        item_gotten_linechart.setDescription("")
        item_gotten_linechart.setDrawBorders(false)
        item_gotten_linechart.setDrawGridBackground(false)

        val leftAxis = item_gotten_linechart.axisLeft
        leftAxis.isEnabled = true

        val rightAxis = item_gotten_linechart.axisRight
        rightAxis.isEnabled = false

        val xAxis = item_gotten_linechart.xAxis
        xAxis.isEnabled = true

        val legend = item_gotten_linechart.legend
        legend.isEnabled = true

        item_gotten_linechart.invalidate()
        item_gotten_linechart.animateXY(500, 300)

    }

    fun renderLineChart_not_gottenItems() {

    }

    fun renderPieChart_Cate() {

    }

}