package com.codenode.budgetlens.utils;

import android.widget.TextView;

public class AppUtils {
    public static void setData(TextView text ,String mm,String dd){
        switch (mm){
            case "1":
                text.setText("January 01-January "+dd);
                break;
            case "2":
                text.setText("February 01-February "+dd);
                break;
            case "3":
                text.setText("March 01-March "+dd);
                break;
            case "4":
                text.setText("April 01-April "+dd);
                break;
            case "5":
                text.setText("May 01-May "+dd);
                break;
            case "6":
                text.setText("June 01-June "+dd);
                break;
            case "7":
                text.setText("July 01-July "+dd);
                break;
            case "8":
                text.setText("August 01-August "+dd);
                break;
            case "9":
                text.setText("September 01-September "+dd);
                break;
            case "10":
                text.setText("October 01-October "+dd);
                break;
            case "11":
                text.setText("November 01-November "+dd);
                break;
            case "12":
                text.setText("December 01-December "+dd);
                break;
        }
    }
}