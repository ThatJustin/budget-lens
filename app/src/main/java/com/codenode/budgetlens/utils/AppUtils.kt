package com.codenode.budgetlens.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.widget.TextView
import com.codenode.budgetlens.home.HomePageActivity
import java.text.SimpleDateFormat
import java.util.*

class AppUtils {
    companion object {

        @SuppressLint("SetTextI18n")
        fun setData(text: TextView, month: String?, day: String) {
            when (month) {
                "01" -> text.text = "January 01-January $day"
                "02" -> text.text = "February 01-February $day"
                "03" -> text.text = "March 01-March $day"
                "04" -> text.text = "April 01-April $day"
                "05" -> text.text = "May 01-May $day"
                "06" -> text.text = "June 01-June $day"
                "07" -> text.text = "July 01-July $day"
                "08" -> text.text = "August 01-August $day"
                "09" -> text.text = "September 01-September $day"
                "10" -> text.text = "October 01-October $day"
                "11" -> text.text = "November 01-November $day"
                "12" -> text.text = "December 01-December $day"
            }
        }

        fun convertDateToFormat(milliSeconds: Long, dateFormat: String?): String? {
            val formatter = SimpleDateFormat(dateFormat, Locale.CANADA)
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }

        /**
         * Changes activity by finishing the previous one and overrides the pending transition with given parameters enterAnim and exitAnim.
         *
         */

        fun changeActivity(activity: Activity, javaClass: Class<*>, enterAnim: Int, exitAnim: Int) {
            val intent = Intent(activity, javaClass)
            activity.startActivity(intent)
            activity.finish()
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
        fun changeActivities(activity: Activity, javaClass: Class<*>, enterAnim: Int, exitAnim: Int) {
            val intent = Intent(activity, javaClass)
            activity.startActivity(intent)
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }
}