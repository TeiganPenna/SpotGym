package com.spotgym.spot.data

import com.spotgym.spot.data.room.RoutineDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineRepository @Inject constructor(private val routineDao: RoutineDao) {

    val readAllData: Flow<List<Routine>> = routineDao.getAll()

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
