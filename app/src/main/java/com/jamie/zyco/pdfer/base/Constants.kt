package com.jamie.zyco.pdfer.base

class Constants {
    companion object {
        @JvmField
        var gPdfCount = 0
        @JvmField
        var gDirCount = 2
        @JvmField
        val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        @JvmField
        val CORE = CPU_COUNT + 1
        @JvmField
        val MAX = CPU_COUNT * 7
        const val SP_NAME = "pdfer_sp_1"
        const val PDF_COUNT = "PDF_COUNT"
        const val DIR_COUNT = "DIR_COUNT"
        const val ACTION_SCANNER = "ACTION_SCANNER"
        const val SCAN_PATH = "SCAN_PATH"
        const val ITEM_PDF_PATH = "ITEM_PDF_PATH"
        const val DEFAULT_PAGE = "DEFAULT_PAGE"

    }
}