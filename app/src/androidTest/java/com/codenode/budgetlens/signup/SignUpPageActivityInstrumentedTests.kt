package com.codenode.budgetlens.signup

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
import com.codenode.budgetlens.login.LoginActivity
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignUpPageActivityInstrumentedTests {
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

    // This is ran before each test for SignUpActivity in order to simulate the user flow/experience/interaction
    // from the opening MainActivity logo splash page to the SignUpActivity page when clicking on the "Register" button
    // in the LoginActivity page
    @Before
    fun setup() {
        clearStorage()
        var intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
        onView(withId(R.id.createNewUser)).perform(click())
        intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, SignUpActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }

    @Test
    fun test_sign_up_page_activity_is_displayed() {
        onView(withId(R.id.filledButton)).perform(click()).check(matches(isDisplayed()))
    }

    @Test
    fun test_registration_with_no_input_fields() {
        onView(withId(R.id.filledButton)).perform(click())

        // This checks that the empty field-specific error messages are displayed and shown to the user
        onView(withId(R.id.email)).check(matches(hasErrorText("This field is required")))
        onView(withId(R.id.firstName)).check(matches(hasErrorText("This field is required")))
        onView(withId(R.id.lastName)).check(matches(hasErrorText("This field is required")))
        onView(withId(R.id.telephoneNumber)).check(matches(hasErrorText("This field is required")))
        onView(withId(R.id.password)).check(matches(hasErrorText("This field is required")))
        onView(withId(R.id.confirmPassword)).check(matches(hasErrorText("This field is required")))
    }

    @Test
    fun test_registration_with_invalid_email_input_field() {
        // This is an invalid email input field
        onView(withId(R.id.email)).perform(typeText("43r34"), closeSoftKeyboard()).check(matches(withText("43r34")))

        onView(withId(R.id.firstName)).perform(typeText("Tester"), closeSoftKeyboard()).check(matches(withText("Tester")))
        onView(withId(R.id.lastName)).perform(typeText("Tester"), closeSoftKeyboard()).check(matches(withText("Tester")))
        onView(withId(R.id.telephoneNumber)).perform(typeText("+15145390682"), closeSoftKeyboard()).check(matches(withText("+15145390682")))
        onView(withId(R.id.password)).perform(typeText("tester_password"), closeSoftKeyboard()).check(matches(withText("tester_password")))
        onView(withId(R.id.confirmPassword)).perform(typeText("tester_password"), closeSoftKeyboard()).check(matches(withText("tester_password")))

        onView(withId(R.id.filledButton)).perform(click())

        // This checks that the email input field-specific error message is displayed and shown to the user
        onView(withId(R.id.email)).check(matches(hasErrorText("This field is not a valid email address")))
    }

    @Test
    fun test_registration_with_invalid_password_size_input_field() {
        onView(withId(R.id.email)).perform(typeText("tester_email@yahoo.com"), closeSoftKeyboard()).check(matches(withText("tester_email@yahoo.com")))
        onView(withId(R.id.firstName)).perform(typeText("Tester"), closeSoftKeyboard()).check(matches(withText("Tester")))
        onView(withId(R.id.lastName)).perform(typeText("Tester"), closeSoftKeyboard()).check(matches(withText("Tester")))
        onView(withId(R.id.telephoneNumber)).perform(typeText("+15145390682"), closeSoftKeyboard()).check(matches(withText("+15145390682")))

        // This is an invalid password input field
        onView(withId(R.id.password)).perform(typeText("te"), closeSoftKeyboard()).check(matches(withText("te")))

        onView(withId(R.id.confirmPassword)).perform(typeText("te"), closeSoftKeyboard()).check(matches(withText("te")))
        onView(withId(R.id.filledButton)).perform(click())

        // This checks that the password input field-specific error message is displayed and shown to the user
        onView(withId(R.id.password)).check(matches(hasErrorText("Password must be greater than 8 characters")))
    }

    @Test
    fun test_registration_with_invalid_matching_passwords_input_field() {
        onView(withId(R.id.email)).perform(typeText("tester_email@yahoo.com"), closeSoftKeyboard()).check(matches(withText("tester_email@yahoo.com")))
        onView(withId(R.id.firstName)).perform(typeText("Tester"), closeSoftKeyboard()).check(matches(withText("Tester")))
        onView(withId(R.id.lastName)).perform(typeText("Tester"), closeSoftKeyboard()).check(matches(withText("Tester")))
        onView(withId(R.id.telephoneNumber)).perform(typeText("+15145390682"), closeSoftKeyboard()).check(matches(withText("+15145390682")))
        onView(withId(R.id.password)).perform(typeText("tester_password"), closeSoftKeyboard()).check(matches(withText("tester_password")))

        // This is an invalid password input field
        onView(withId(R.id.confirmPassword)).perform(typeText("te"), closeSoftKeyboard()).check(matches(withText("te")))

        onView(withId(R.id.filledButton)).perform(click())

        // This checks that the password input field-specific error message is displayed and shown to the user
        onView(withId(R.id.confirmPassword)).check(matches(hasErrorText("Passwords do not match")))
    }

    @Test
    fun test_registration_with_valid_input_fields() {
        onView(withId(R.id.email)).perform(typeText("tester_email@yahoo.com"), closeSoftKeyboard()).check(matches(withText("tester_email@yahoo.com")))
        onView(withId(R.id.firstName)).perform(typeText("Tester"), closeSoftKeyboard()).check(matches(withText("Tester")))
        onView(withId(R.id.lastName)).perform(typeText("Tester"), closeSoftKeyboard()).check(matches(withText("Tester")))
        onView(withId(R.id.telephoneNumber)).perform(typeText("+15145390682"), closeSoftKeyboard()).check(matches(withText("+15145390682")))
        onView(withId(R.id.password)).perform(typeText("tester_password"), closeSoftKeyboard()).check(matches(withText("tester_password")))
        onView(withId(R.id.confirmPassword)).perform(typeText("tester_password"), closeSoftKeyboard()).check(matches(withText("tester_password")))
        onView(withId(R.id.filledButton)).perform(click())

        // When the user clicks on the "Register" button, the user should be redirected back to the LoginActivity page to log in to their account for the first time
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }
}