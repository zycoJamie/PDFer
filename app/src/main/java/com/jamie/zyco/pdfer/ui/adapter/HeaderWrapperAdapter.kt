package com.jamie.zyco.pdfer.ui.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jamie.zyco.pdfer.R
import com.jamie.zyco.pdfer.ui.adapter.PdfListAdapter.Holder

class HeaderWrapperAdapter(adapter: RecyclerView.Adapter<Holder>) : RecyclerView.Adapter<Holder>() {
    private var mInnerAdapter: RecyclerView.Adapter<Holder> = adapter
    private var headers: SparseArray<View> = SparseArray()

    companion object {
        private const val BASE_ITEM_TYPE = 100
    }

    fun getRealCount() = mInnerAdapter.itemCount

    fun getHeaderCount() = headers.size()

    fun isHeaders(position: Int) = position < headers.size()

    fun addHeaders(view: View) {
        headers.put(BASE_ITEM_TYPE + headers.size(), view)
    }

    override fun getItemViewType(position: Int): Int {
        if (isHeaders(position)) {
            return headers.keyAt(position)
        }
        return mInnerAdapter.getItemViewType(position)
    }

    override fun onCreateViewHolder(container: ViewGroup, type: Int): Holder {
        if (headers[type] != null) {
            return Holder(headers[type], DataBindingUtil.inflate(LayoutInflater.from(container.context), R.layout.rv_header, container, false))
        }
        return mInnerAdapter.onCreateViewHolder(container, type)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (isHeaders(position)) {
            return
        }
        return mInnerAdapter.onBindViewHolder(holder, position - headers.size())
    }

    override fun getItemCount() = getHeaderCount() + getRealCount()

}