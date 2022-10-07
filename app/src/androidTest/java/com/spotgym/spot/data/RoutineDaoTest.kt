package com.spotgym.spot.data

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.spotgym.spot.data.room.RoutineDao
import com.spotgym.spot.data.room.SpotDatabase
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
class RoutineDaoTest {

    private lateinit var routineDao: RoutineDao
    private lateinit var db: SpotDatabase

    private lateinit var routine1: Routine
    private lateinit var routine2: Routine
    private lateinit var routine3: Routine

    @BeforeEach
    fun beforeEach() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context,
            SpotDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        routineDao = db.routineDao()

        routine1 = Routine(1, "A", "some description", 2)
        routine2 = Routine(2, "B", "some description", 0)
        routine3 = Routine(3, "C", "some description", 0)

        routineDao.insert(routine1)
        routineDao.insert(routine3)
        routineDao.insert(routine2)
    }

    @AfterEach
    fun afterEach() {
        db.close()
    }

    @Test
    fun `get all routines sorted by index`(): Unit = runBlocking {
        val routineList = routineDao.getAll()

        assertThat(routineList).hasSize(3)
        assertThat(routineList[0]).isEqualTo(routine2)
        assertThat(routineList[1]).isEqualTo(routine3)
        assertThat(routineList[2]).isEqualTo(routine1)
    }

    @Test
    fun `insert and get routine`(): Unit = runBlocking {
        val routine = Routine(4, "Dummy Routine", "some description", 3)
        routineDao.insert(routine)
        val result = routineDao.getById(4)
        assertThat(result?.id).isEqualTo(4)
    }

    @Test
    fun `update routines`(): Unit = runBlocking {
        routine1.index = 0
        routine2.index = 1
        routine3.index = 2
        routineDao.updateMany(listOf(routine1, routine2, routine3))

        val routineList = routineDao.getAll()
        assertThat(routineList).hasSize(3)
        assertThat(routineList[0]).isEqualTo(routine1)
        assertThat(routineList[1]).isEqualTo(routine2)
        assertThat(routineList[2]).isEqualTo(routine3)
    }
}
