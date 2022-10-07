package com.spotgym.spot.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface DataDao<T> {

    @Insert
    suspend fun insert(obj: T)

    @Update
    suspend fun update(obj: T)

    @Update
    suspend fun updateMany(objs: List<T>)

    @Delete
    suspend fun delete(obj: T)
}
