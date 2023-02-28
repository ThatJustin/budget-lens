package com.codenode.budgetlens.friends

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
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
class FriendsPageActivityInstrumentedTests {

    private fun ViewInteraction.isDisplayed(): Boolean {
        return try {
            check(matches(ViewMatchers.isDisplayed()))
            true
        } catch (e: NoMatchingViewException) {
            false
        }
    }

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

    // This is ran before each test for FriendsPageActivity in order to simulate the user flow/experience/interaction
    // from the opening MainActivity logo splash page and logging in into the app to viewing the friends page
    @Before
    fun setup() {
        clearStorage()
        var intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)

        // The following inputs the username and password into the login page and clicks the login button after making
        // sure to close the keyboard
        onView(withId(R.id.usernameText)).perform(typeText("Test1234"), closeSoftKeyboard()).check(matches(withText("Test1234")))
        onView(withId(R.id.passwordText)).perform(typeText("test1234"), closeSoftKeyboard()).check(matches(withText("test1234")))
        onView(withId(R.id.checkCredentials)).perform(click()).check(matches(isDisplayed()))
        intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }

    @Test
    fun test_receipts_list_page_activity_is_displayed_and_scrollable() {
        // This test checks to see if the friends page activity is displayed and scrollable
        // by first checking if it is displayed when the "Friends" button/item on the navigation
        // bar is clicked and then scrolling down and up the list to make sure that it is scrollable
        onView(withId(R.id.friends)).perform(click()).check(matches(isDisplayed()))
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, FriendsPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
        if (onView(withId(R.id.friend_card_view)).isDisplayed()) {
            onView(withId(R.id.friends_list)).perform(swipeUp())
            onView(withId(R.id.friends_list)).perform(swipeDown())
            onView(withId(R.id.friends_list)).perform(click())
            onView(withId(R.id.relativeLayout)).check(matches(isDisplayed()))
        }
        else {
            !onView(withId(R.id.friend_card_view)).isDisplayed()
        }
    }
}