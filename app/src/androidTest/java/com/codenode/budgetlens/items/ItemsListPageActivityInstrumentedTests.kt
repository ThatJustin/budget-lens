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
class ItemsListPageActivityInstrumentedTests{
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
    // from the opening MainActivity logo splash page and logging in into the app to accessing the receipts list page to then opening a receipt to
    // view the items list page
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
        onView(withId(R.id.receipts)).perform(click()).check(matches(isDisplayed()))
    }

    fun test_items_list_page_activity_is_displayed() {
        // This test checks to see if the items list page activity is displayed by first checking
        // if it is displayed when the "Items" button/item on the navigation bar is clicked
        onView(withId(R.id.items)).perform(click()).check(matches(isDisplayed()))
        onView(withId(R.id.search_bar_text)).check(matches(isDisplayed()))
    }

    @Test
    fun test_items_list_page_activity_scan_receipt_is_clickable() {
        onView(withId(R.id.receipts_list)).perform(click())
        onView(withId(R.id.receipt_info_view_items)).perform(click())
        onView(withId(R.id.addReceipts)).perform(click())
        onView(withId(R.id.ScanReceipt)).perform(click())
    }

    @Test
    fun test_items_list_page_activity_create_manual_receipt_is_clickable() {
        onView(withId(R.id.receipts_list)).perform(click())
        onView(withId(R.id.receipt_info_view_items)).perform(click())
        onView(withId(R.id.addReceipts)).perform(click())
        onView(withId(R.id.createManual)).perform(click())
        onView(withId(R.id.filledButton)).check(matches(isDisplayed()))
    }
}
