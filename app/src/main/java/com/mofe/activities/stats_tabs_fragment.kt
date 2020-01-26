package com.mofe.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import com.mofe.R
import com.mofe.utils.Togetutil
import com.mofe.utils.viewPagers
import java.util.ArrayList

class stats_tabs_fragment : Togetutil(), viewPagers.tabInterface {

    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    protected lateinit var r: Resources
    val mActivity: Activity = this@stats_tabs_fragment
    protected var px: Float = 0.toFloat()
    open val CUSTOM_PREF_NAME = "amount_data"
    val context: Context = this
    private var bottomSheetStateForOnBackPressed = BottomSheetBehavior.STATE_HALF_EXPANDED

    override fun startmyIntent(intent: Intent?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_stats)

        viewPager = findViewById(R.id.stats_viewpager)
        setupViewPager(viewPager!!)

//        setSupportActionBar(toolbarHistory)

        tabLayout = findViewById(R.id.stats_tabs)
        tabLayout!!.setupWithViewPager(viewPager)

        r = resources
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, r.displayMetrics)

    }

    private fun setupViewPager(viewPager: ViewPager) {

        val adapter = ViewPagerAdapter(this.supportFragmentManager)

        adapter.addFragment(stats_toget_fragment(), "Items to get")
        adapter.addFragment(stats_gotten_fragment(), "Items gotten")

        viewPager.adapter = adapter
        viewPager.setCurrentItem(0, true)
        viewPager.offscreenPageLimit = 2

    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {

            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)

        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    fun bottomSheetBehaviourInit(bottomSheetBehaviour: BottomSheetBehavior<LinearLayout>){

        bottomSheetBehaviour.isFitToContents = true

        bottomSheetBehaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(p0: View, p1: Float) {

                when (p1){
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