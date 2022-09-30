package com.spotgym.spot.data.room

import androidx.room.Dao
import androidx.room.Query
import com.spotgym.spot.data.Routine

@Dao
interface RoutineDao : DataDao<Routine> {

    @Query("select * from routines")
    suspend fun getAll(): List<Routine>

    @Query("select * from routines where id = :id LIMIT 1")
    suspend fun getById(id: Int): Routine?
}
