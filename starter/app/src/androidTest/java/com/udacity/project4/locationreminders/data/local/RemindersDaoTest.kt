package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import junit.framework.Assert

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test



@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        // GIVEN - insert a task
        val reminder = ReminderDTO("title", "description","Location",30.1,30.1)
        database.reminderDao().saveReminder(reminder)// taskDao().insertTask(reminder)

        // WHEN - Get the task by id from the database
        val loaded = database.reminderDao().getReminderById(reminder.id)//.taskDao().getTaskById(reminder.id)

        // THEN - The loaded data contains the expected values

        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.location, `is`(reminder.location))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    @Test
    fun return_DataNotFound()= runBlockingTest{
        // GIVEN - insert a reminder
        var reminder =ReminderDTO("title", "description","Location",30.1,30.1)
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the reminder by id from the database
        val id="10000000"
        val loaded = database.reminderDao().getReminderById(id)

        // THEN - The loaded data contains the expected values
        Assert.assertNull(loaded)

    }
}
