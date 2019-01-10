package com.jamie.zyco.pdfer.ui.adapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

class MainViewPagerAdapter(var list: MutableList<View>) : PagerAdapter() {

    override fun getItemPosition(`object`: Any): Int {
        val view = `object` as View
        return list.indexOf(view)
    }

    override fun getCount() = list.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(list[position])
        return list[position]
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(list[position])
    }
}