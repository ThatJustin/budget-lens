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
import com.codenode.budgetlens.login.password_reset.PasswordResetActivity
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ResetPasswordPageActivityInstrumentedTests {
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

    // This is ran before each test for PasswordResetActivity in order to simulate the user flow/experience/interaction
    // from the opening MainActivity logo splash page to reaching the reset password page from the login page
    @Before
    fun setup() {
        clearStorage()
        var intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
        onView(withId(R.id.forgorPass)).perform(click())
        intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, PasswordResetActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }

    @Test
    fun test_reset_password_page_activity_is_displayed() {
        onView(withId(R.id.sendEmail)).perform(click()).check(matches(isDisplayed()))
    }

    @Test
    fun test_reset_password_with_no_email_input() {
        onView(withId(R.id.sendEmail)).perform(click())

        // This is the error message that is displayed when the user does not enter an email address as input
        onView(withId(R.id.emailInput)).check(matches(hasErrorText("This field is required")))
    }

    @Test
    fun test_reset_password_with_valid_email_input() {
        onView(withId(R.id.emailInput)).perform(typeText("tester_email@yahoo.com"), closeSoftKeyboard()).check(matches(withText("tester_email@yahoo.com")))
        onView(withId(R.id.sendEmail)).perform(click())

        // When the user enters a valid email address, the app should redirect the user to the CodeConfirmationActivity
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, CodeConfirmationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }
}