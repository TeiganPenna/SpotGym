package com.spotgym.spot.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.spotgym.spot.data.Routine

@Dao
interface RoutineDao {

    @Query("select * from routines")
    suspend fun getAll(): List<Routine>

    @Query("select * from routines where id = :id")
    suspend fun getById(id: Int): Routine?

    @Insert
    suspend fun insert(routine: Routine)

    @Update
    suspend fun update(routine: Routine)

    @Delete
    suspend fun delete(routine: Routine)
}