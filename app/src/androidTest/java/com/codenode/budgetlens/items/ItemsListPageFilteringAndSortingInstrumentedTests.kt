package com.codenode.budgetlens.items

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.codenode.budgetlens.MainActivity
import com.codenode.budgetlens.R
import com.codenode.budgetlens.home.HomePageActivity
import com.codenode.budgetlens.login.LoginActivity
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ItemsListPageFilteringAndSortingInstrumentedTests {
    // This is used to clear the shared preferences before each test
    companion object {
        @BeforeClass
        fun clearStorage() {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pm clear PACKAGE NAME").close()
        }
    }

    // This specifies the first activity to be launched on the emulator during the test
    @get:Rule
    val mainActivityRule = ActivityScenarioRule(MainActivity::class.java)

    // This is ran before each test for ItemsListPageActivity in order to simulate the user flow/experience/interaction
    // from the opening MainActivity logo splash page and logging in into the app to viewing the items list page
    @Before
    fun setup() {
        clearStorage()
        var intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)

        // The following inputs the username and password into the login page and clicks the login button after making
        // sure to close the keyboard
        onView(withId(R.id.usernameText)).perform(typeText("Test1234"))
        onView(withId(R.id.usernameText)).check(matches(withText("Test1234")))
        onView(withId(R.id.passwordText)).perform(typeText("test1234"))
        onView(withId(R.id.passwordText)).check(matches(withText("test1234")))
        onView(withId(R.id.passwordText)).perform(closeSoftKeyboard())
        onView(withId(R.id.checkCredentials)).perform(click()).check(matches(isDisplayed()))
        intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
        onView(withId(R.id.items)).perform(click()).check(matches(isDisplayed()))
        intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, ItemsListPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }

    @Test
    fun test_items_list_filter_dialog_window_opens() {
        // This test only checks to see if the items list filter dialog window opens when the filter button is clicked
        onView(withId(R.id.filter_item_btn_open)).perform(click())
    }

    @Test
    fun test_items_list_sort_by_dialog_window_opens() {
        // This test only checks to see if the items list sort by dialog window opens when the sort by button is clicked
        onView(withId(R.id.sort_item_btn_open)).perform(click())
    }
}