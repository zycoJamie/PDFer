package com.jamie.zyco.pdfer.ui.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jamie.zyco.pdfer.databinding.ItemPdfListBinding
import com.jamie.zyco.pdfer.model.entity.db.PdfDocument
import java.util.*

class PdfListAdapter(var resId: Int,var data: MutableList<PdfDocument>) : RecyclerView.Adapter<PdfListAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): Holder {
        val binding: ItemPdfListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), resId, parent, false)
        return Holder(binding.root, binding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.pdf = data[position]
        if (data[position].lastOpen != null) {
            val calendar = Calendar.getInstance()
            calendar.time = Date(data[position].lastOpen!!)
            val dateStr = "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月${calendar.get(Calendar.DAY_OF_MONTH)}日" +
                    "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
            holder.binding.date = dateStr
        }
    }

    class Holder(itemView: View, var binding: ItemPdfListBinding) : RecyclerView.ViewHolder(itemView)
}