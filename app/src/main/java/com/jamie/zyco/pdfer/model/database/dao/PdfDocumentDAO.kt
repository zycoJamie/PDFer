package com.jamie.zyco.pdfer.model.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import com.jamie.zyco.pdfer.model.entity.db.PdfDocument

@Dao
interface PdfDocumentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg pdf: PdfDocument)

}