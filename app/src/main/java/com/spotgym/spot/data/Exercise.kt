package com.spotgym.spot.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = Routine::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("routineId")
    ]
)
data class Exercise(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "exerciseName")
    val name: String,

    @ColumnInfo(name = "exerciseDescription")
    val description: String,

    @ColumnInfo(name = "routineId")
    val routineId: Int,

    @ColumnInfo(name = "exerciseIndex")
    var index: Int,
)
