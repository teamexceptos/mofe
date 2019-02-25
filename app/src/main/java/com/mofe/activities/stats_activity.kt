package com.mofe.activities

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.data.*
import com.mofe.R
import com.mofe.database.AppDatabase
import com.mofe.database.entities.Cate
import com.mofe.database.entities.Items
import kotlinx.android.synthetic.main.activity_stats_views.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast

/**
 * Created by ${cosmic} on 2/19/19.
 */

class stats_activity : home_activity() {

    lateinit var database_stats : AppDatabase

    val mStatsActivity: Activity = this@stats_activity
    val stats_context: Context = this

    var notgotten_items_dates: ArrayList<Items> = ArrayList()
    var gotten_items_dates: ArrayList<Items> = ArrayList()
    var date_gotten_item_counts: ArrayList<String> = ArrayList()
    var date_notgotten_item_counts: ArrayList<String> = ArrayList()
    var cate_gotten_item_counts: ArrayList<String> = ArrayList()
    var cate_notgotten_item_counts: ArrayList<String> = ArrayList()
    var cates_list: ArrayList<Cate> = ArrayList()
    var count_list_1: ArrayList<Int> = ArrayList()
    var count_list_2: ArrayList<Int> = ArrayList()
    var count_list_cate_1: ArrayList<Int> = ArrayList()
    var count_list_cate_2: ArrayList<Int> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noStatusBar()

        setContentView(R.layout.activity_stats_views)

        database_stats = AppDatabase.getInstance(context = this@stats_activity)
        cates_list = AppDatabase.getInstance(context = this@stats_activity).CateDao().all as ArrayList<Cate>

        notgotten_items_dates = database_stats.ItemsDao().loadAllByGotten(itemisGotten = "no") as ArrayList<Items>
        gotten_items_dates = database_stats.ItemsDao().loadAllByGotten(itemisGotten = "yes") as ArrayList<Items>


        for (ng in notgotten_items_dates){
            date_notgotten_item_counts.add(ng.itemDueDate!!)
            cate_notgotten_item_counts.add(ng.itemCate!!)
        }

        for (g in gotten_items_dates){
            date_gotten_item_counts.add(g.itemDueDate!!)
            cate_gotten_item_counts.add(g.itemCate!!)
        }

        for (unique_ng_date in date_notgotten_item_counts.distinct()){
            count_list_1.add(date_notgotten_item_counts.count { it == unique_ng_date })
        }

        for (unique_g_date in date_gotten_item_counts.distinct()){
            count_list_2.add(date_gotten_item_counts.count { it == unique_g_date })
        }

        for (unique_cate_g in cate_gotten_item_counts.distinct()){
            count_list_cate_2.add(cate_gotten_item_counts.count { it == unique_cate_g })
        }

        for (unique_cate_ng in cate_notgotten_item_counts.distinct()){
            count_list_cate_1.add(cate_notgotten_item_counts.count { it == unique_cate_ng })
        }

        renderLineChart_gottenItems()
        item_gotten_opt.setOnClickListener {

            if(date_gotten_item_counts.size != 0){
                val uniques_gotten: ArrayList<String> = ArrayList()

                for (i in 0 until date_gotten_item_counts.distinct().size){
                    uniques_gotten.add(date_gotten_item_counts[i] +"                "+ count_list_2[i])

                }
                selector("Items gotten", uniques_gotten.toMutableList()){ _, i ->
                    toast(uniques_gotten[i])
                }
            } else {
                toast("No item inputs yet")
            }

        }

        renderLineChart_not_gottenItems()
        item_to_get_opt.setOnClickListener {

            if(date_notgotten_item_counts.size != 0){
                val uniques_gotten: ArrayList<String> = ArrayList()

                for (i in 0 until date_notgotten_item_counts.distinct().size){
                    uniques_gotten.add(date_notgotten_item_counts[i] +"                "+ count_list_1[i])
                }
                selector("Items to get", uniques_gotten.toMutableList()){ _, i ->
                    toast(uniques_gotten[i])
                }

            } else {
                toast("No item inputs yet")
            }
        }

        renderPieChart_ng_Cate()
        renderPieChart_g_Cate()

    }

    @TargetApi(Build.VERSION_CODES.M)
    fun renderLineChart_gottenItems() {

        val entries = ArrayList<Entry>()
        var x = 0

        if(count_list_2.size != 0){

            item_gotten_linechart.visibility = View.VISIBLE

            count_list_2.forEach {
                entries.add(Entry(it.toFloat(), x))
                x += 1
            }

            val lineDataSet = LineDataSet(entries, "Total")
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
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun renderLineChart_not_gottenItems() {
        val entries = ArrayList<Entry>()
        var x = 0

        if(count_list_1.size != 0){

            item_mofe_linechart.visibility = View.VISIBLE

            count_list_1.forEach {
                entries.add(Entry(it.toFloat(), x))
                x += 1
            }

            val lineDataSet = LineDataSet(entries, "Total")
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

            val lineData = LineData(date_notgotten_item_counts.distinct(), lineDataSet)
            item_mofe_linechart.data = lineData
            item_mofe_linechart.xAxis.setDrawGridLines(true)
            item_mofe_linechart.xAxis.gridColor = Color.parseColor("#EFEFFF")
            item_mofe_linechart.xAxis.gridLineWidth = 2f
            item_mofe_linechart.xAxis.axisLineColor = stats_context.getColor(R.color.colorWhite)
            item_mofe_linechart.axisLeft.setDrawGridLines(false)
            item_mofe_linechart.axisLeft.axisLineColor = stats_context.getColor(R.color.colorWhite)
            item_mofe_linechart.setDescription("")
            item_mofe_linechart.setDrawBorders(false)
            item_mofe_linechart.setDrawGridBackground(false)

            val leftAxis = item_mofe_linechart.axisLeft
            leftAxis.isEnabled = true

            val rightAxis = item_mofe_linechart.axisRight
            rightAxis.isEnabled = false

            val xAxis = item_mofe_linechart.xAxis
            xAxis.isEnabled = true

            val legend = item_mofe_linechart.legend
            legend.isEnabled = true

            item_mofe_linechart.invalidate()
            item_mofe_linechart.animateXY(500, 300)

        }

    }

    fun renderPieChart_ng_Cate() {

        val yVentries = ArrayList<Entry>()
        var x = 0

        count_list_cate_1.forEach {
            yVentries.add(Entry(it.toFloat(), x))
            x += 1
        }

        val androidColors = context.resources.getIntArray(R.array.random_color)

        val pieDataSet = PieDataSet(yVentries, "")
        pieDataSet.setSliceSpace(3f)
        pieDataSet.setSelectionShift(5f)
        pieDataSet.setColors(androidColors.slice(0..count_list_cate_1.size))

        val pieData = PieData(cate_notgotten_item_counts.distinct(), pieDataSet)

        pie_ng_cate_chart.rotationAngle = 0f
        pie_ng_cate_chart.isRotationEnabled = true

        pie_ng_cate_chart.data = pieData
        pie_ng_cate_chart.setUsePercentValues(true)
        pie_ng_cate_chart.setCenterTextSize(7f)
        pie_ng_cate_chart.setCenterTextColor(Color.BLACK)
        pie_ng_cate_chart.setHoleColor(Color.WHITE)
        pie_ng_cate_chart.setDescription("categories of items to get")

        pie_ng_cate_chart.invalidate()

    }

    fun renderPieChart_g_Cate() {

        val yVentries = ArrayList<Entry>()
        var x = 0

        count_list_cate_2.forEach {
            yVentries.add(Entry(it.toFloat(), x))
            x += 1
        }

        val androidColors = context.resources.getIntArray(R.array.random_color)

        val pieDataSet = PieDataSet(yVentries, "")
        pieDataSet.setSliceSpace(3f)
        pieDataSet.setSelectionShift(5f)
        pieDataSet.setColors(androidColors.slice(0..count_list_cate_2.size))

        val pieData = PieData(cate_gotten_item_counts.distinct(), pieDataSet)

        pie_g_cate_chart.rotationAngle = 0f
        pie_g_cate_chart.isRotationEnabled = true

        pie_g_cate_chart.data = pieData
        pie_g_cate_chart.setUsePercentValues(true)
        pie_g_cate_chart.setCenterTextSize(7f)
        pie_g_cate_chart.setCenterTextColor(Color.BLACK)
        pie_g_cate_chart.setHoleColor(Color.WHITE)
        pie_g_cate_chart.setDescription("categories of items gotten")

        pie_g_cate_chart.invalidate()

    }

}