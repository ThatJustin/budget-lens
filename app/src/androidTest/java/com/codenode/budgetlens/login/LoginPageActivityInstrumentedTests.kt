package com.codenode.budgetlens.login

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
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginPageActivityInstrumentedTests {
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

    // This is ran before each test for LoginActivity in order to simulate the user flow/experience/interaction
    // from the opening MainActivity logo splash page to logging in into the app
    @Before
    fun setup() {
        clearStorage()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }

    @Test
    fun test_login_page_activity_is_displayed() {
        onView(withId(R.id.checkCredentials)).perform(click()).check(matches(isDisplayed()))
    }

    @Test
    fun test_login_with_no_user_account_credentials() {
        onView(withId(R.id.checkCredentials)).perform(click())

        // This checks that the field-specific error messages are displayed and shown to the user
        onView(withId(R.id.usernameText)).check(matches(hasErrorText("This field is required.")))
        onView(withId(R.id.passwordText)).check(matches(hasErrorText("This field is required.")))
    }

    @Test
    fun test_login_with_valid_user_account_credentials() {
        onView(withId(R.id.usernameText)).perform(typeText("tester"), closeSoftKeyboard()).check(matches(withText("tester")))
        onView(withId(R.id.passwordText)).perform(typeText("tester_password"), closeSoftKeyboard()).check(matches(withText("tester_password")))
        onView(withId(R.id.checkCredentials)).perform(click())

        // When the user logs in successfully, the app should navigate to the HomePageActivity
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, HomePageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }
}