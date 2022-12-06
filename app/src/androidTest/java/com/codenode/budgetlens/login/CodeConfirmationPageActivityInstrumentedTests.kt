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
import com.codenode.budgetlens.login.password_reset.CodeConfirmationActivity
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class CodeConfirmationPageActivityInstrumentedTests {
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
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, CodeConfirmationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }

    @Test
    fun test_code_confirmation_page_activity_is_displayed() {
        onView(withId(R.id.confirm)).perform(click()).check(matches(isDisplayed()))
    }

    @Test
    fun test_code_confirmation_with_no_code_input() {
        onView(withId(R.id.confirm)).perform(click())

        // This checks that the error message is displayed and shown to the user
        onView(withId(R.id.codeInput)).check(matches(hasErrorText("This field is required")))
    }

    @Test
    fun test_code_confirmation_with_valid_code_input() {
        onView(withId(R.id.codeInput)).perform(typeText("123456"), closeSoftKeyboard()).check(matches(withText("123456")))
        onView(withId(R.id.confirm)).perform(click())

        // When the user enters a valid confirmation code and confirms it, the user should be redirected to the login page
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }
}