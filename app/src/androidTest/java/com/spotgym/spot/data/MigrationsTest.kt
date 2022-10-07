package com.spotgym.spot.data

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.spotgym.spot.data.room.Migrations.ROUTINE_EXERCISE_INDEX_3_4
import com.spotgym.spot.data.room.SpotDatabase
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class MigrationsTest {

    @get:Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SpotDatabase::class.java
    )

    @Test
    fun `migration 3 to 4`(): Unit = runBlocking {
        migrationTestHelper.createDatabase(TEST_DB_NAME, 3).apply {
            execSQL(
                "INSERT INTO routines (routineName, routineDescription)" +
                    "VALUES ('1 routine', '1 description')"
            )
            execSQL(
                "INSERT INTO exercises (exerciseName, exerciseDescription, routineId)" +
                    "VALUES ('1 exercise', '1 description', 1)"
            )
            close()
        }

        migrationTestHelper.runMigrationsAndValidate(TEST_DB_NAME, 4, true, ROUTINE_EXERCISE_INDEX_3_4).apply {
            query("SELECT * FROM routines").use { cursor ->
                assertThat(cursor.count).isEqualTo(1)
                cursor.moveToFirst()
                val indexColumnIndex = cursor.getColumnIndex("routineIndex")
                assertThat(indexColumnIndex).isNotEqualTo(-1)
                val index = cursor.getInt(indexColumnIndex)
                assertThat(index).isEqualTo(0) // Default value
            }

            query("SELECT * FROM exercises").use { cursor ->
                assertThat(cursor.count).isEqualTo(1)
                cursor.moveToFirst()
                val indexColumnIndex = cursor.getColumnIndex("exerciseIndex")
                assertThat(indexColumnIndex).isNotEqualTo(-1)
                val index = cursor.getInt(indexColumnIndex)
                assertThat(index).isEqualTo(0) // Default value
            }

            close()
        }
    }

    companion object {
        const val TEST_DB_NAME = "spot-migration-test"
    }
}
