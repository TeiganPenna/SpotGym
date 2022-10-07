package com.spotgym.spot.data

import com.spotgym.spot.data.room.RoutineDao
import javax.inject.Inject
import javax.inject.Singleton

interface RoutineRepository {
    suspend fun getAllRoutines(): List<Routine>
    suspend fun getRoutine(id: Int): Routine?
    suspend fun addRoutine(routine: Routine)
    suspend fun updateRoutine(routine: Routine)
    suspend fun updateRoutines(routines: List<Routine>)
    suspend fun deleteRoutine(routine: Routine)
}

@Singleton
class RoutineRepositoryImpl
@Inject constructor(private val routineDao: RoutineDao) : RoutineRepository {

    override suspend fun getAllRoutines(): List<Routine> = routineDao.getAll()

    override suspend fun getRoutine(id: Int): Routine? = routineDao.getById(id)

    override suspend fun addRoutine(routine: Routine) {
        routineDao.insert(routine)
    }

    override suspend fun updateRoutine(routine: Routine) {
        routineDao.update(routine)
    }

    override suspend fun updateRoutines(routines: List<Routine>) {
        routineDao.updateMany(routines)
    }

    override suspend fun deleteRoutine(routine: Routine) {
        routineDao.delete(routine)
    }
}
