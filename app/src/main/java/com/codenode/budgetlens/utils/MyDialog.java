package com.codenode.budgetlens.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.codenode.budgetlens.R;

public class MyDialog extends Dialog {
    public MyDialog(Context context, View layout, boolean isCancelable, boolean isBackCancelable) {

        super(context, R.style.MyDialog);

        setContentView(layout);
        setCancelable(isCancelable);
        setCanceledOnTouchOutside(isBackCancelable);
        Window window = getWindow();

        WindowManager.LayoutParams params = window.getAttributes();

        params.gravity = Gravity.CENTER;

        window.setAttributes(params);
    }

}