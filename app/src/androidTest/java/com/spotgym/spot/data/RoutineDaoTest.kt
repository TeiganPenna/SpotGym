package com.spotgym.spot.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
class RoutineDaoTest {

    private lateinit var routineDao: RoutineDao
    private lateinit var db: SpotDatabase

    private val routine1 = Routine(1, "A", "some description")
    private val routine2 = Routine(2, "B", "some description")
    private val routine3 = Routine(3, "C", "some description")

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
    fun `get all routines`(): Unit = runBlocking {
        val routineList = routineDao.getAll()

        assertThat(routineList[0]).isEqualTo(routine1)
        assertThat(routineList[1]).isEqualTo(routine2)
        assertThat(routineList[2]).isEqualTo(routine3)
    }

    @Test
    fun `insert and get routine`(): Unit = runBlocking {
        val routine = Routine(4, "Dummy Routine", "some description")
        routineDao.insert(routine)
        val result = routineDao.getById(4)
        assertThat(result?.id).isEqualTo(4)
    }
}
