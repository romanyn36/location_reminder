package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

    private lateinit var fakedataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupTest() {
        fakedataSource = FakeDataSource()
        viewModel =
            RemindersListViewModel(getApplicationContext(), fakedataSource)
        stopKoin()
        val myModule = module {
            single {
                viewModel
            }
        }
        startKoin {
            modules(listOf(myModule))
        }
    }

    @After
    fun stopDown() {
        stopKoin()
    }
    //    TODO: test the navigation of the fragments.
    @Test
    fun navigate_add_reminderTest() = runBlockingTest {

        val fragmentScenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        fragmentScenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }
//    TODO: test the displayed data on the UI.

    @Test
    fun displayData_uiTest() = runBlockingTest {
        var reminder1 = ReminderDTO("title", "description","Location",30.1,30.1)

        fakedataSource.saveReminder(reminder1)
        launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)
        onView(withId(R.id.noDataTextView)).check(
            ViewAssertions.matches(
                CoreMatchers.not(
                    ViewMatchers.isDisplayed()
                ))
        )
        onView(ViewMatchers.withText(reminder1.title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(reminder1.description)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        onView(ViewMatchers.withText(reminder1.location)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }
//    TODO: add testing for the error messages.

    @Test
    fun displayed_noDataTest() = runBlockingTest {
        // GIVEN -
        fakedataSource.deleteAllReminders()

// THEN - The loaded data contains the expected values
        launchFragmentInContainer<ReminderListFragment>(Bundle.EMPTY, R.style.AppTheme)

        onView(withId(R.id.noDataTextView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


}