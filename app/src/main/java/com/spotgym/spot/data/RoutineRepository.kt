package com.spotgym.spot.data

import androidx.lifecycle.LiveData

class RoutineRepository(private val routineDao: RoutineDao) {

    val readAllData : LiveData<List<Routine>> = routineDao.getAll();

    suspend fun addRoutine(routine: Routine) {
        routineDao.insert(routine);
    }

    suspend fun updateRoutine(routine: Routine) {
        routineDao.update(routine);
    }

    suspend fun deleteRoutine(routine: Routine) {
        routineDao.delete(routine)
    }
}