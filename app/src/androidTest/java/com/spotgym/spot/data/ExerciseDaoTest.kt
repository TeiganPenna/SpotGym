package com.spotgym.spot.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.spotgym.spot.data.room.ExerciseDao
import com.spotgym.spot.data.room.RoutineDao
import com.spotgym.spot.data.room.SpotDatabase
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
class ExerciseDaoTest {
    private lateinit var routineDao: RoutineDao
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var db: SpotDatabase

    private lateinit var routine1: Routine
    private lateinit var routine2: Routine
    private lateinit var routine3: Routine

    private lateinit var exercise1: Exercise
    private lateinit var exercise2: Exercise
    private lateinit var exercise3: Exercise

    @BeforeEach
    fun beforeEach() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context,
            SpotDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        exerciseDao = db.exerciseDao()
        routineDao = db.routineDao()

        routine1 = Routine(1, "1", "some description", 0)
        routine2 = Routine(2, "2", "some description", 1)
        routine3 = Routine(3, "3", "some description", 3)

        routineDao.insert(routine1)
        routineDao.insert(routine2)
        routineDao.insert(routine3)

        exercise1 = Exercise(1, "A", "some description", routine1.id, 2)
        exercise2 = Exercise(2, "B", "some description", routine1.id, 0)
        exercise3 = Exercise(3, "C", "some description", routine2.id, 0)

        exerciseDao.insert(exercise1)
        exerciseDao.insert(exercise3)
        exerciseDao.insert(exercise2)
    }

    @AfterEach
    fun afterEach() {
        db.close()
    }

    @Test
    fun `get all exercises sorted by index`(): Unit = runBlocking {
        val exerciseList = exerciseDao.getAll()

        assertThat(exerciseList).hasSize(3)
        assertThat(exerciseList[0]).isEqualTo(exercise2)
        assertThat(exerciseList[1]).isEqualTo(exercise3)
        assertThat(exerciseList[2]).isEqualTo(exercise1)
    }

    @Test
    fun `insert and get exercise`(): Unit = runBlocking {
        val exercise = Exercise(4, "Dummy Exercise", "some description", routine3.id, 3)
        exerciseDao.insert(exercise)
        val result = exerciseDao.getById(4)
        assertThat(result?.id).isEqualTo(4)
    }

    @Test
    fun `get routine with exercises sorted by index`(): Unit = runBlocking {
        var routineWithExercises = exerciseDao.getRoutineWithExercises(1)
        assertThat(routineWithExercises!!.routine.id).isEqualTo(1)
        var exercises = routineWithExercises.getOrderedExercises()
        assertThat(exercises).hasSize(2)
        assertThat(exercises[0].id).isEqualTo(2)
        assertThat(exercises[1].id).isEqualTo(1)

        routineWithExercises = exerciseDao.getRoutineWithExercises(2)
        assertThat(routineWithExercises!!.routine.id).isEqualTo(2)
        exercises = routineWithExercises.getOrderedExercises()
        assertThat(exercises).hasSize(1)
        assertThat(exercises[0].id).isEqualTo(3)

        routineWithExercises = exerciseDao.getRoutineWithExercises(3)
        assertThat(routineWithExercises!!.routine.id).isEqualTo(3)
        exercises = routineWithExercises.getOrderedExercises()
        assertThat(exercises).isEmpty()
    }

    @Test
    fun `delete routine and all its related exercises`(): Unit = runBlocking {
        routineDao.delete(routine1)

        val routineList = routineDao.getAll()
        assertThat(routineList).hasSize(2)
        assertThat(routineList[0]).isEqualTo(routine2)
        assertThat(routineList[1]).isEqualTo(routine3)

        val exerciseList = exerciseDao.getAll()
        assertThat(exerciseList).hasSize(1)
        assertThat(exerciseList[0]).isEqualTo(exercise3)
    }

    @Test
    fun `update exercises`(): Unit = runBlocking {
        exercise1.index = 0
        exercise2.index = 1
        exercise3.index = 2
        exerciseDao.updateMany(listOf(exercise1, exercise2, exercise3))

        val exerciseList = exerciseDao.getAll()
        assertThat(exerciseList).hasSize(3)
        assertThat(exerciseList[0]).isEqualTo(exercise1)
        assertThat(exerciseList[1]).isEqualTo(exercise2)
        assertThat(exerciseList[2]).isEqualTo(exercise3)
    }
}
