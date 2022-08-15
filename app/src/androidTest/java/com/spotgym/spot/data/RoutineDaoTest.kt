package com.spotgym.spot.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoutineDaoTest {

    private lateinit var routineDao: RoutineDao
    private lateinit var db: AppDatabase

    private val routine1 = Routine(1, "A")
    private val routine2 = Routine(2, "B")
    private val routine3 = Routine(3, "C")

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        routineDao = db.routineDao()

        routineDao.insert(routine1)
        routineDao.insert(routine2)
        routineDao.insert(routine3)
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun getRoutines() = runBlocking {
        val routineList = routineDao.getAll().first()

        assertThat(routineList[0], equalTo(routine1))
        assertThat(routineList[1], equalTo(routine2))
        assertThat(routineList[2], equalTo(routine3))
    }

    @Test
    fun insertAndGetRoutine() = runBlocking {
        val routine = Routine(4, "Dummy Routine")
        routineDao.insert(routine)
        val result = routineDao.getById(4)
        assertThat(result?.id, equalTo(4))
    }
}
