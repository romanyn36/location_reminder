package com.udacity.project4


import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get


@RunWith(AndroidJUnit4::class)
@LargeTest
class RemindersActivityTest :
    AutoCloseKoinTest() {
    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private lateinit var remindersActivity: RemindersActivity
    private lateinit var viewModel: SaveReminderViewModel
    // An idling resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    @get:Rule
    var activityTestRule: ActivityTestRule<RemindersActivity> =
        ActivityTestRule(RemindersActivity::class.java)
    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }

        remindersActivity = activityTestRule.activity   // real repository
        repository = get()

        runBlocking {
            repository.deleteAllReminders() //clear
        }
        viewModel = GlobalContext.get().koin.get()

    }
///////

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

//    TODO: add End to End testing to the app

    @Test
    fun endToEnd_Toast(){

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText("title"))
        onView(withId(R.id.reminderDescription)).perform(replaceText("description"))
        onView(withId(R.id.selectLocation)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.save_btn)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())
        onView(withText(R.string.reminder_saved))
            .inRoot(RootMatchers.withDecorView(not(remindersActivity.window.decorView))).check(
                matches(isDisplayed())
            )
        Thread.sleep(2000)
        activityScenario.close()
    }



    @Test
fun endToEnd_SnackBarTest(){
    val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
    onView(withId(R.id.noDataTextView))
        .check(matches(isDisplayed()))
    onView(withId(R.id.addReminderFAB)).perform(click())
    onView(withId(R.id.saveReminder)).perform(click())
    onView(withId(R.id.snackbar_text))
        .check(matches(withText(R.string.err_enter_title)))
    Thread.sleep(4000)
    onView(withId(R.id.reminderTitle)).perform(replaceText("new title"))
    onView(withId(R.id.saveReminder)).perform(click())
    onView(withId(R.id.snackbar_text))
        .check(matches(withText(R.string.err_select_location)))
    activityScenario.close()
}

}