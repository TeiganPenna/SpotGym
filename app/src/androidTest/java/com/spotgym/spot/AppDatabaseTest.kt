package com.spotgym.spot

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineDao
import com.spotgym.spot.data.AppDatabase
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var routineDao: RoutineDao
    private lateinit var db: AppDatabase

    @Before
    fun before() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        routineDao = db.routineDao()
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun insertAndGetRoutine() = runBlocking {
        val routine = Routine(1, "Dummy Routine")
        routineDao.insert(routine)
        val result = routineDao.getById(1)
        assertThat(result?.id, equalTo(1))
    }
}