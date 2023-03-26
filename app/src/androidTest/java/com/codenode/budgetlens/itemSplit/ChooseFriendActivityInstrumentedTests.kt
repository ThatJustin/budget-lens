package com.codenode.budgetlens.itemSplit

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.codenode.budgetlens.R
import com.codenode.budgetlens.friends.FriendsPageActivityInstrumentedTests
import com.codenode.budgetlens.home.HomePageActivity
import com.codenode.budgetlens.login.LoginActivity
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.codenode.budgetlens.receipts.ReceiptsListPageActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class ChooseFriendActivityInstrumentedTests {

    companion object {
        @BeforeClass
        fun clearStorage() {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pm clear PACKAGE NAME")
                .close()
        }
    }

    @get:Rule
    val chooseFriendActivityRule = ActivityScenarioRule(SplitItemListActivity::class.java)

    @Before
    fun setup() {
        FriendsPageActivityInstrumentedTests.clearStorage()
        var intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            LoginActivity::class.java
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        androidx.core.content.ContextCompat.startActivity(
            InstrumentationRegistry.getInstrumentation().targetContext,
            intent,
            null
        )

        // The following inputs the username and password into the login page and clicks the login button after making
        // sure to close the keyboard
        onView(withId(R.id.usernameText)).perform(
            ViewActions.typeText("Test1234"),
            ViewActions.closeSoftKeyboard()
        ).check(matches(withText("Test1234")))
        onView(withId(R.id.passwordText)).perform(
            ViewActions.typeText("test1234"),
            ViewActions.closeSoftKeyboard()
        ).check(matches(withText("test1234")))
        onView(withId(R.id.checkCredentials)).perform(click())
            .check(matches(ViewMatchers.isDisplayed()))
        intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ChooseFriendActivity::class.java
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        androidx.core.content.ContextCompat.startActivity(
            InstrumentationRegistry.getInstrumentation().targetContext,
            intent,
            null
        )

    }

    @Test
    fun test_split_item_list_activity_is_displayed_and_scrollable() {
        // This test checks to see if the split item list activity is displayed and scrollable
        // by first checking if it is displayed when the SplitItemListActivity is started
        // and then scrolling down and up the list to make sure that it is scrollable
        onView(withId(R.id.friends_list)).perform(swipeUp())
        onView(withId(R.id.friends_list)).perform(swipeDown())
        onView(withId(R.id.friends_list)).perform(click())

    }

    @Test
    fun test_split_item_list_activity_ui_elements() {
        onView(withId(R.id.friends_list)).check(matches(withId(R.id.friends_list)))
        onView(withId(R.id.cancel_button)).check(matches(withText("Cancel")))
        onView(withId(R.id.confirm_button)).check(matches(withText("Confirm")))
    }

    @Test
    fun test_split_item_list_activity_cancel_button() {
        onView(withId(R.id.cancel_button)).perform(click())
        // Add an assertion or check to verify that the expected behavior occurs when the cancel button is clicked.
    }

    @Test
    fun test_split_item_list_activity_sure_button() {
        onView(withId(R.id.confirm_button)).perform(click())
        // Add an assertion or check to verify that the expected behavior occurs when the sure button is clicked.
    }

    // Add more tests as needed for other specific UI interactions or behavior in the SplitItemListActivity.
}
