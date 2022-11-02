package com.codenode.budgetlens.receipts

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import java.io.IOException


class ReceiptInfoDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {
    // How to use this dialog :
    // Should be opened via clicking a receipt from a list on the receipt page, but it's not ready yet.
    // When the receipt list page is done, clicking on a receipt should open this dialog using the code below
    // you will also need to pass data from the activity to this class, you can add and pass a variable from constructor if needed
    // I assume a Receipt class instance that holds the values will suffice.
    /*
                val openReceiptDialog = findViewById.... // code it
        val dialog = ReceiptInfoDialog(this, R.style.ReceiptItemDialog)
        dialog.setOnDismissListener {
            if (dialog.isDeletedReceipt) {
                Snackbar.make(
                    findViewById<BottomNavigationView>(R.id.bottom_navigation),
                    "Receipt deleted.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        openReceiptDialog.setOnClickListener {
            dialog.show()
        }*/
    // Temporary here until receipt list page is done. Receipt page should send info to this dialog and populate the screen
    var RECEIPT_ID = -1

    private lateinit var receiptInfoDialog: Dialog
    var isDeletedReceipt = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_receipt_info, null)
        setContentView(dialogView)
        receiptInfoDialog = this

        val window = window;
        if (window != null) {
            // Remove the bar on top with the battery, notifications, service etc
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            window.setBackgroundDrawableResource(R.color.purple_50)
            window.setLayout(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.FILL_PARENT
            )
        }
        handleListeners()
    }


    private fun handleListeners() {
        findViewById<Button>(R.id.receipt_info_delete)?.setOnClickListener {

            MaterialAlertDialogBuilder(context)
                .setTitle("Delete Receipt")
                .setMessage("Are you sure you want to delete this receipt?\r\nThe action cannot be undone.")
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Delete") { dialog, _ ->
                    requestReceiptDeletion(dialog)
                }
                .show()

        }

        val toolBar = findViewById<Toolbar>(R.id.toolbar)
        toolBar.setNavigationOnClickListener { dismiss() }
        toolBar.setOnMenuItemClickListener {
            dismiss()
            true
        }
    }


    // This should ideally not be here, but for now it is until further infrastructure is done
    //TODO move to appropriate class when it can
    private fun requestReceiptDeletion(dialog: DialogInterface) {
        val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/${RECEIPT_ID}/"

        val registrationPost = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
            .delete()
            .build()

        registrationPost.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Response", "Got the response from server")
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            isDeletedReceipt = true;
                            dialog.dismiss()
                            receiptInfoDialog.dismiss()
                            Log.i("Successful", "Receipt ID $RECEIPT_ID deleted.")
                        } else {
                            Log.i(
                                "Error",
                                "Something went wrong ${response.message} ${response.headers}"
                            )
                        }
                    } else {
                        Snackbar.make(
                            findViewById(R.id.toolbar),
                            "Failed to delete.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                        Log.e(
                            "Error",
                            "Something went wrong ${response.message} ${response.headers}"
                        )
                    }
                }
            }
        })
    }
}

