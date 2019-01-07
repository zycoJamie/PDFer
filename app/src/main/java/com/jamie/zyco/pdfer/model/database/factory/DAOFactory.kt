package com.jamie.zyco.pdfer.model.database.factory

import com.jamie.zyco.pdfer.base.MyApplication
import com.jamie.zyco.pdfer.model.database.dao.PdfDocumentDAO

class DAOFactory {
    companion object {
        fun getPdfDocumentDAO(): PdfDocumentDAO = MyApplication.getDatabase().pdfDAO()
    }
}