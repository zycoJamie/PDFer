package com.jamie.zyco.pdfer.model.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.jamie.zyco.pdfer.model.entity.db.PdfDocument

@Dao
interface PdfDocumentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg pdf: PdfDocument)

    @Query("Select * from pdf")
    fun queryAll(): LiveData<MutableList<PdfDocument>>

    @Query("Select DATA from pdf")
    fun queryPath(): List<String>

}