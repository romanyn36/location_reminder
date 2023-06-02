package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import androidx.test.core.app.ApplicationProvider.getApplicationContext


import androidx.test.ext.junit.runners.AndroidJUnit4


import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource

import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers

import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat

import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(maxSdk = Build.VERSION_CODES.P)
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun createRepository() {
        stopKoin()

        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            getApplicationContext(),
            fakeDataSource
        )
        runBlocking{ fakeDataSource.deleteAllReminders()}

    }
    //TODO: provide testing to the SaveReminderView and its live data objects

    private fun getReminder(): ReminderDataItem {
        return ReminderDataItem("title", "description","Location",30.1,30.1)
    }
    @Test
    fun saveReminder() {

        val reminder = getReminder()
        saveReminderViewModel.saveReminder(reminder)
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
    }

    @Test
    fun saveReminder_withouLocation() {

        val reminder = ReminderDataItem("title", "desc", "", 30.1, 30.1)

        saveReminderViewModel.validateAndSaveReminder(reminder)
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())

    }

    @Test
    fun showLoading() = runBlocking {
//GIVEN
        val reminder = getReminder()
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(reminder)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true))
//THEN
        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(false))


    }


    @Test
    fun saveReminder_NoTitle() {

        val reminder = ReminderDataItem("", "description","Location",30.1,30.1)

        saveReminderViewModel.validateAndSaveReminder(reminder)
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), notNullValue())

    }
}