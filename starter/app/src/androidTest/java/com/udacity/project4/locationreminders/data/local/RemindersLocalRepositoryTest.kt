package com.udacity.project4.locationreminders.data.local


import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt
@get:Rule
var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository = RemindersLocalRepository(
            database.reminderDao(), Dispatchers.Unconfined
        )
    }

    @Test
    fun saveReminders_retrievesReminderTest() = runBlocking {
        // GIVEN - insert
        val reminder =ReminderDTO("title", "description","Location",30.1,30.1)
        remindersLocalRepository.saveReminder(reminder)

        // WHEN - Get
        val result = remindersLocalRepository.getReminder(reminder.id)
        result as Result.Success

        // THEN - The loaded data
        assertThat(result.data.title, `is`("title"))
        assertThat(result.data.description, `is`("description"))
        assertThat(result.data.latitude, `is`(30.1))
        assertThat(result.data.longitude, `is`(30.1))

    }


    @Test
    fun delete_reminderTest() = runBlocking {
        // GIVEN - insert // delete
        var reminder = ReminderDTO("title", "description", "dhaka", 30.1, 30.1)
        remindersLocalRepository.saveReminder(reminder)
        remindersLocalRepository.deleteAllReminders()

        // WHEN - Get
        val result = remindersLocalRepository.getReminder(reminder.id)

        // THEN -
        assertThat(result is Result.Error, `is`(true))
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }
}