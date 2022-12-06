package com.codenode.budgetlens.home

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
import com.codenode.budgetlens.common.ScanningReceiptActivity
import com.codenode.budgetlens.login.LoginActivity
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// This Test Class is very flaky. Some tests pass sometimes and other times they fail randomly. I have no idea why.
@RunWith(AndroidJUnit4::class)
@LargeTest
class HomePageActivityInstrumentedTests {
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

    // This is ran before each test for HomePageActivity in order to simulate the user flow/experience/interaction
    // from the opening MainActivity logo splash page to reaching the home page after logging in
    @Before
    fun setup() {
        clearStorage()
        onView(withId(R.id.LoginActivityBtn)).perform(click())
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
    fun test_home_page_activity_is_displayed() {
        // This action is done to change the displayed page first before coming back to the home page to
        // show it is indeed displayed since the default page that is opened from the login page is already
        // the home page as was done in the setup above
        onView(withId(R.id.receipts)).perform(click())
        onView(withId(R.id.home)).perform(click()).check(matches(isDisplayed()))
    }

    @Test
    fun test_home_page_activity_create_manual_is_clickable() {
        onView(withId(R.id.addReceipts)).perform(click())
        onView(withId(R.id.createManual)).perform(click()).check(matches(isDisplayed()))
    }

    @Test
    fun test_home_page_activity_scan_receipt_is_clickable() {
        onView(withId(R.id.addReceipts)).perform(click())
        onView(withId(R.id.ScanReceipt)).perform(click())

        // When the user clicks on the "Scan Receipt" button from the home page, the user should be redirected to the ScanningReceiptActivity
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, ScanningReceiptActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(InstrumentationRegistry.getInstrumentation().targetContext, intent, null)
    }
}