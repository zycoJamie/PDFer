package com.jamie.zyco.pdfer.base

class Constants {
    companion object {
        @JvmField
        var gPdfCount = 0
        const val SP_NAME = "pdfer_sp_1"
        const val PDF_COUNT = "PDF_COUNT"
        const val ACTION_SCANNER = "ACTION_SCANNER"
        @JvmField
        val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        @JvmField
        val CORE = CPU_COUNT + 1
        @JvmField
        val MAX = CPU_COUNT * 7
    }
}