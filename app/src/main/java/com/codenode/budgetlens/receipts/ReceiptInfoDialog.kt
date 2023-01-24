package com.codenode.budgetlens.receipts

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.R
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.data.Receipts
import com.codenode.budgetlens.data.UserProfile
import com.codenode.budgetlens.items.ItemsListPageActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


//Open already uploaded receipt
class ReceiptInfoDialog(
    context: Context,
    receipt: Receipts
) : Dialog(context) {

    var receiptInfo = receipt

    private lateinit var receiptInfoDialog: Dialog
    var isDeletedReceipt = false

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_receipt_info, null)
        setContentView(dialogView)

        //connect your xml component(like: textview, imageview, button)
        val imageReceipt = dialogView.findViewById<ImageView>(R.id.receipt_info_receipt_image)
        val tvMerchantName = dialogView.findViewById<TextView>(R.id.tvMerchantName)
//        val tvAddedBy = dialogView.findViewById<TextView>(R.id.tvAddedBy)
        val tvReceiptDate = dialogView.findViewById<TextView>(R.id.tvReceiptDate)
        val tvAddedBy = dialogView.findViewById<TextView>(R.id.tvAddedBy)
        val tvSplitAmount = dialogView.findViewById<TextView>(R.id.tvTotalAmount)
        val tvTotalAmountCurrency = dialogView.findViewById<TextView>(R.id.tvTotalAmountCurrency)
//        val tvExpirationDate = dialogView.findViewById<TextView>(R.id.tvExpirationDate)
        val tvDateUploaded = dialogView.findViewById<TextView>(R.id.tvDateUploaded)
//        val tvReturnPeriod = dialogView.findViewById<TextView>(R.id.tvReturnPeriod)


        if (receiptInfo.merchant_name != null) {
            tvMerchantName.text =
                context.getString(R.string.merchant_name, receiptInfo.merchant_name)
            tvMerchantName.text = tvMerchantName.text.toString()
                .substring(0, tvMerchantName.text.toString().length - 2)
        } else {
            tvMerchantName.text = context.getString(R.string.merchant_name, "N/A")
            tvMerchantName.text = tvMerchantName.text.toString()
                .substring(0, tvMerchantName.text.toString().length - 2)
        }
        tvDateUploaded.text = "0000/00/00 - 00:00" // not yet implemented in Receipt Model
        if (receiptInfo.total_amount != null) {
            tvSplitAmount.text = context.getString(R.string.total, receiptInfo.total_amount)
        } else {
            tvSplitAmount.text = context.getString(R.string.total, 0.00)
        }
        if (receiptInfo.currency != null) {
            tvTotalAmountCurrency.text =
                context.getString(R.string.total_amount_currency, receiptInfo.currency)
        } else {
            tvTotalAmountCurrency.text = context.getString(R.string.total_amount_currency, "N/A")
        }

        if (receiptInfo.scan_date != null) {
            tvReceiptDate.text = context.getString(R.string.scan_date, receiptInfo.scan_date)
        } else {
            val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            tvReceiptDate.text = context.getString(R.string.scan_date, date)
        }
        tvAddedBy.text =
            context.getString(R.string.user_profile_name, " " + UserProfile.getFullName())

//      tvExpirationDate.text = receiptInfo.important_dates
//      tvReturnPeriod.text = receiptInfo.important_dates

        //TODO Glide to another thread, it's costly on the main UI thread
        imageReceipt.scaleType = ImageView.ScaleType.CENTER
        Glide.with(context).load(receiptInfo.receipt_image)
            .placeholder(R.drawable.ic_baseline_receipt_long_24).into(imageReceipt)

        receiptInfoDialog = this

        val window = window
        if (window != null) {
            // Remove the bar on top with the battery, notifications, service etc
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            window.setBackgroundDrawableResource(R.color.purple_50)
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        }
        handleListeners()
    }


    private fun handleListeners() {
        //handle delete button
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
        //Handle tool bar
        val toolBar = findViewById<Toolbar>(R.id.toolbar)
        toolBar.setNavigationOnClickListener { dismiss() }
        toolBar.setOnMenuItemClickListener {
            dismiss()
            true
        }

        // handle view items button
        val viewItems = findViewById<Button>(R.id.receipt_info_view_items)
        viewItems.setOnClickListener {
            val itemsListPage = Intent(context, ItemsListPageActivity::class.java)
            itemsListPage.putExtra("singleReceiptView", true)
            itemsListPage.putExtra("receiptID", receiptInfo.id)
            context.startActivity(itemsListPage)
        }

        //handle add important date button
        handleAddImportantDateButton()
    }

    /**
     * Handles everything related to the add important date button.
     */
    private fun handleAddImportantDateButton() {
        val addImportantDate = findViewById<Button>(R.id.receipt_info_add_important_date)

        //Encapsulate everything in a LinearLayout
        val layout = LinearLayout(context, null, 0)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 0, 50, 0)

        // An EditText Field will be used for the title
        val importantDateTitle = EditText(context)
        importantDateTitle.maxLines = 1
        //backend has a max length of 36, respect it here
        importantDateTitle.filters = arrayOf(InputFilter.LengthFilter(36))
        importantDateTitle.inputType = InputType.TYPE_CLASS_TEXT
        layout.addView(importantDateTitle)

        //Calendar to select a date
        val calendar = CalendarView(context)
        layout.addView(calendar)

        //Create the dialog
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Add Important Date")
            .setMessage("Enter a description for the date and select the date.\r\n\r\nDescription:")
            .setView(layout)
            .setNegativeButton("Cancel") { dialog, _ ->
                importantDateTitle.setText("")
                dialog.dismiss()
            }
            .setPositiveButton("Add", null)
            .create()

        // Check when a user inputs a title to enable/disable the Add button
        importantDateTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                dialog.getButton(BUTTON_POSITIVE).isEnabled = s.isNotEmpty()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        addImportantDate.setOnClickListener {
            dialog.show()
            dialog.getButton(BUTTON_POSITIVE).isEnabled = false

            dialog.getButton(BUTTON_POSITIVE).setOnClickListener {

                //TODO Either send API request before closing or after closing...

                //clean up for next time
                importantDateTitle.setText("")
                dialog.dismiss()
            }
        }
    }


    // This should ideally not be here, but for now it is until further infrastructure is done
    //TODO move to appropriate class when it can
    private fun requestReceiptDeletion(dialog: DialogInterface) {
        val url =
            "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/api/receipts/${receiptInfo.id}/"

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
                            isDeletedReceipt = true
                            dialog.dismiss()
                            receiptInfoDialog.dismiss()
                            Log.i("Successful", "Receipt ID ${receiptInfo.id} deleted.")
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

