package com.jamie.zyco.pdfer.listener.clickhandler

import android.view.View

interface MainActivityClickHandler {
    fun add(view: View)
    fun switchList()
    fun findPDF()
    fun quicklyFound()
    fun viewPdf(path: String)
}