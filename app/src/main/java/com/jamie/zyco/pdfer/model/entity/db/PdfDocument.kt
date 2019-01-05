package com.jamie.zyco.pdfer.model.entity.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "pdf", indices = [Index(value = ["DATA"], unique = true)])
data class PdfDocument(
        @PrimaryKey
        @ColumnInfo(name = "DATA")
        var path: String,
        @ColumnInfo(name = "SIZE")
        var size: Long,
        @ColumnInfo(name = "OPEN")
        var lastOpen: Long,
        @ColumnInfo(name = "COVER")
        var cover: String
)