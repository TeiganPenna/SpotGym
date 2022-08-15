package com.spotgym.spot.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Query("select * from routines")
    fun getAll(): Flow<List<Routine>>

    @Query("select * from routines where id = :id")
    fun getById(id: Int): Routine?

    @Insert
    suspend fun insert(routine: Routine)

    @Update
    suspend fun update(routine: Routine)

    @Delete
    suspend fun delete(routine: Routine)
}
