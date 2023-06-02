package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNot.not
import org.hamcrest.core.Is.`is`
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun initViewModel() {
        fakeDataSource = FakeDataSource()
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }
    @After
    fun stopDown() {
        stopKoin()
    }
    @Test
    fun reminderListTest() {
        var reminder1 = ReminderDTO(
            "title1",
            "description",
            "Location",
            30.1,
            30.1)
        var reminder2 = ReminderDTO("title2", "description","Location",30.1,30.1)
        var reminder3 = ReminderDTO("title3", "description","Location",30.1,30.1)
        val remindersList = mutableListOf(reminder1, reminder2, reminder3)
        fakeDataSource = FakeDataSource(remindersList!!)
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue(), (not(emptyList())))
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue().size, `is`(remindersList.size))
    }
    @Test
    fun reminder_return_EmptyListTest() {
        val remindersList = mutableListOf<ReminderDTO>()
        fakeDataSource = FakeDataSource(remindersList)
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue(), (`is`(emptyList())))
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue().size, `is`(0))
    }

    @Test
    fun check_loading() {
        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun returnError() {
        var reminder1 = ReminderDTO(
            "title1",
            "description",
            "Location",
            30.1,
            30.1)
        var reminder2 = ReminderDTO("title2", "description","Location",30.1,30.1)
        var reminder3 = ReminderDTO("title3", "description","Location",30.1,30.1)
        val remindersList = mutableListOf(reminder1, reminder2, reminder3)
        fakeDataSource = FakeDataSource(remindersList)
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        fakeDataSource.setShouldReturnError(true)

        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.showSnackBar.getOrAwaitValue(), `is`("error happened while retrieving the data"))
    }


}