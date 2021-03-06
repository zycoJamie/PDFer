package com.jamie.zyco.pdfer.model.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.jamie.zyco.pdfer.model.database.dao.PdfDocumentDAO
import com.jamie.zyco.pdfer.model.entity.db.PdfDocument

@Database(entities = [PdfDocument::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pdfDAO(): PdfDocumentDAO
}