package com.jamie.zyco.pdfer.ui.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jamie.zyco.pdfer.MainActivity
import com.jamie.zyco.pdfer.databinding.ItemPdfListBinding
import com.jamie.zyco.pdfer.databinding.RvHeaderBinding
import com.jamie.zyco.pdfer.model.entity.db.PdfDocument
import com.jamie.zyco.pdfer.utils.Zog
import java.util.*
import kotlin.collections.ArrayList

class PdfListAdapter(var resId: Int, var data: MutableList<PdfDocument> = ArrayList(), var context: Context) : RecyclerView.Adapter<PdfListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): Holder {
        val binding: ItemPdfListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), resId, parent, false)
        return Holder(binding.root, binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when (holder.binding) {
            is ItemPdfListBinding -> {
                holder.binding.pdf = data[position]
                if (data[position].lastOpen != null) {
                    val calendar = Calendar.getInstance()
                    calendar.time = Date(data[position].lastOpen!!)
                    val dateStr = "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月${calendar.get(Calendar.DAY_OF_MONTH)}日" +
                            "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
                    holder.binding.date = dateStr
                }
            }
            is RvHeaderBinding -> {
                if (context is MainActivity) {
                    Zog.log(0, "header onBindViewHolder")
                    holder.binding.click = context as MainActivity
                }
            }
        }
    }

    class Holder(itemView: View, val binding: ViewDataBinding) : RecyclerView.ViewHolder(itemView)
}