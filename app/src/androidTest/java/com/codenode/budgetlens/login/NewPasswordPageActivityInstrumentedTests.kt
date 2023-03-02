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
import com.codenode.budgetlens.login.password_reset.NewPasswordActivity
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class NewPasswordPageActivityInstrumentedTests {
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

    @Before
    fun setup() {
        clearStorage()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, NewPasswordActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }

    @Test
    fun test_new_password_page_activity_is_displayed() {
        onView(withId(R.id.resetPassword)).perform(click()).check(matches(isDisplayed()))
    }

    @Test
    fun test_new_password_with_no_password_inputs() {
        onView(withId(R.id.resetPassword)).perform(click())

        // This checks that the error messages are displayed and shown to the user
        onView(withId(R.id.newPasswordInput)).check(matches(hasErrorText("This field is required")))
        onView(withId(R.id.confirmPasswordInput)).check(matches(hasErrorText("This field is required")))
    }

    @Test
    fun test_new_password_with_valid_password_inputs() {
        onView(withId(R.id.newPasswordInput)).perform(typeText("tester_password"), closeSoftKeyboard()).check(matches(withText("tester_password")))
        onView(withId(R.id.confirmPasswordInput)).perform(typeText("tester_password"), closeSoftKeyboard()).check(matches(withText("tester_password")))
        onView(withId(R.id.resetPassword)).perform(click())

        // When the user enters a new valid password and confirms it, the user should be redirected back to the login page
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }
}