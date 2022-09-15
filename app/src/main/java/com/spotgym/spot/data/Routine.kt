package com.spotgym.spot.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class Routine(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "routineName")
    val name: String,

    @ColumnInfo(name = "routineDescription")
    val description: String,
)
