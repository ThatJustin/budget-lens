package com.codenode.budgetlens.itemSplit

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.codenode.budgetlens.R
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SplitItemListActivityInstrumentedTests {
    // This is used to clear the shared preferences before each test
    companion object {
        @BeforeClass
        fun clearStorage() {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pm clear PACKAGE NAME").close()
        }
    }

    @get:Rule
    val splitItemListActivityRule = ActivityScenarioRule(SplitItemListActivity::class.java)

    @Before
    fun setup() {
        clearStorage()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, SplitItemListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }

    @Test
    fun test_split_item_list_activity_is_displayed() {
        // This test checks to see if the split item list activity is displayed
        // by checking if it is displayed when the SplitItemListActivity is started
        onView(withId(R.id.friends_list)).perform(swipeUp())
        onView(withId(R.id.friends_list)).perform(swipeDown())
        onView(withId(R.id.friends_list)).perform(click())
    }

    @Test
    fun test_split_item_list_activity_ui_elements() {
        onView(withId(R.id.friends_list)).check(matches(withId(R.id.friends_list)))
        onView(withId(R.id.tv_cancel)).check(matches(withText("Cancel")))
        onView(withId(R.id.tv_sure)).check(matches(withText("Confirm")))
    }

    @Test
    fun test_split_item_list_activity_cancel_button() {
        onView(withId(R.id.tv_cancel)).perform(click())
        // Add an assertion or check to verify that the expected behavior occurs when the cancel button is clicked.
    }

    @Test
    fun test_split_item_list_activity_sure_button() {
        onView(withId(R.id.tv_sure)).perform(click())
        // Add an assertion or check to verify that the expected behavior occurs when the sure button is clicked.
    }

    // Add more tests as needed for other specific UI interactions or behavior in the SplitItemListActivity.
}