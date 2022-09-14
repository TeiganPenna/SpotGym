package com.spotgym.spot.data

import com.spotgym.spot.data.room.RoutineDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineRepository @Inject constructor(private val routineDao: RoutineDao) {

    suspend fun getAllRoutines(): List<Routine> = routineDao.getAll()

    suspend fun getRoutine(id: Int): Routine? = routineDao.getById(id)

    suspend fun addRoutine(routine: Routine) {
        routineDao.insert(routine)
    }

    suspend fun updateRoutine(routine: Routine) {
        routineDao.update(routine)
    }

    suspend fun deleteRoutine(routine: Routine) {
        routineDao.delete(routine)
    }
}
