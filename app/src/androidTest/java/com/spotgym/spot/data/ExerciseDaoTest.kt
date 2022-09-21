package com.spotgym.spot.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.spotgym.spot.data.room.ExerciseDao
import com.spotgym.spot.data.room.RoutineDao
import com.spotgym.spot.data.room.SpotDatabase
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExerciseDaoTest {
    private lateinit var routineDao: RoutineDao
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var db: SpotDatabase

    private val routine1 = Routine(1, "1", "some description")
    private val routine2 = Routine(2, "2", "some description")
    private val routine3 = Routine(3, "3", "some description")

    private val exercise1 = Exercise(1, "A", "some description", routine1.id)
    private val exercise2 = Exercise(2, "B", "some description", routine1.id)
    private val exercise3 = Exercise(3, "C", "some description", routine2.id)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context,
            SpotDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        exerciseDao = db.exerciseDao()
        routineDao = db.routineDao()

        routineDao.insert(routine1)
        routineDao.insert(routine2)
        routineDao.insert(routine3)

        exerciseDao.insert(exercise1)
        exerciseDao.insert(exercise2)
        exerciseDao.insert(exercise3)
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun getExercises(): Unit = runBlocking {
        val exerciseList = exerciseDao.getAll()

        assertThat(exerciseList[0]).isEqualTo(exercise1)
        assertThat(exerciseList[1]).isEqualTo(exercise2)
        assertThat(exerciseList[2]).isEqualTo(exercise3)
    }

    @Test
    fun insertAndGetRoutine(): Unit = runBlocking {
        val exercise = Exercise(4, "Dummy Exercise", "some description", routine3.id)
        exerciseDao.insert(exercise)
        val result = exerciseDao.getById(4)
        assertThat(result?.id).isEqualTo(4)
    }

    @Test
    fun getRoutineWithExercises(): Unit = runBlocking {
        var routineWithExercises = exerciseDao.getRoutineWithExercises(1)
        assertThat(routineWithExercises!!.routine.id).isEqualTo(1)
        assertThat(routineWithExercises.exercises).hasSize(2)
        assertThat(routineWithExercises.exercises[0].id).isEqualTo(1)
        assertThat(routineWithExercises.exercises[1].id).isEqualTo(2)

        routineWithExercises = exerciseDao.getRoutineWithExercises(2)
        assertThat(routineWithExercises!!.routine.id).isEqualTo(2)
        assertThat(routineWithExercises.exercises).hasSize(1)
        assertThat(routineWithExercises.exercises[0].id).isEqualTo(3)

        routineWithExercises = exerciseDao.getRoutineWithExercises(3)
        assertThat(routineWithExercises!!.routine.id).isEqualTo(3)
        assertThat(routineWithExercises.exercises).isEmpty()
    }
}