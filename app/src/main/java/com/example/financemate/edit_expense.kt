package com.example.financemate

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class edit_expense : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)

        val home: ImageView = findViewById(R.id.imageView8)
        home.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }

        val budget: ImageView = findViewById(R.id.imageView9)
        budget.setOnClickListener {
            val intent = Intent(this, expense_overview::class.java)
            startActivity(intent)
        }

        val trans: ImageView = findViewById(R.id.imageView10)
        trans.setOnClickListener {
            val intent = Intent(this, transication::class.java)
            startActivity(intent)
        }

        val profileu: ImageView = findViewById(R.id.imageView11)
        profileu.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }

        val viewex: ImageView = findViewById(R.id.imageView34)
        viewex.setOnClickListener {
            val intent = Intent(this, display_expense::class.java)
            startActivity(intent)
        }



        val titleEditText: EditText = findViewById(R.id.titleEditText)
        val amountEditText: EditText = findViewById(R.id.amountEditText)
        val categorySpinner: Spinner = findViewById(R.id.categorySpinner)
        val dateEditText: EditText = findViewById(R.id.dateEditText)
        val saveButton: ImageView = findViewById(R.id.saveButton)

        // Get the current values passed from the previous activity
        val title = intent.getStringExtra("TITLE") ?: ""
        val amount = intent.getStringExtra("AMOUNT") ?: ""
        val category = intent.getStringExtra("CATEGORY") ?: ""
        val date = intent.getStringExtra("DATE") ?: ""

        // Set the current values in the EditTexts and Spinner
        titleEditText.setText(title)
        amountEditText.setText(amount)
        categorySpinner.setSelection(getCategoryIndex(category))
        dateEditText.setText(date)

        // Set DatePickerDialog for the Date EditText
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            dateEditText.setText(dateFormat.format(calendar.time))
        }

        // When user clicks the date input field, show the DatePickerDialog
        dateEditText.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // When the Save button is clicked, update the expense data and return
        saveButton.setOnClickListener {
            val newTitle = titleEditText.text.toString()
            val newAmount = amountEditText.text.toString()
            val newCategory = categorySpinner.selectedItem.toString()
            val newDate = dateEditText.text.toString()

            // Update the expense data in the file
            updateExpenseInFile(title, newTitle, newAmount, newCategory, newDate)

            // Navigate back to the display_expense activity
            val intent = Intent(this, display_expense::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // To clear the previous activity stack
            startActivity(intent)
        }
    }

    private fun getCategoryIndex(category: String): Int {
        val categories = resources.getStringArray(R.array.category_array)
        return categories.indexOf(category)
    }

    private fun updateExpenseInFile(oldTitle: String, newTitle: String, newAmount: String, newCategory: String, newDate: String) {
        try {
            val fileInputStream = openFileInput("expenses.txt")
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val updatedData = StringBuilder()
            var line: String?
            var isUpdating = false

            // Loop through all lines to find the matching Title and update it
            while (bufferedReader.readLine().also { line = it } != null) {
                if (line?.startsWith("Title: $oldTitle") == true) {
                    updatedData.append("Title: $newTitle\n")
                    updatedData.append("Amount: $newAmount\n")
                    updatedData.append("Category: $newCategory\n")
                    updatedData.append("Date: $newDate\n")
                    isUpdating = true
                } else if (!isUpdating) {
                    updatedData.append(line).append("\n")
                } else {
                    bufferedReader.readLine() // Skip the next lines (Amount, Category, and Date)
                    bufferedReader.readLine()
                    bufferedReader.readLine()
                    isUpdating = false
                }
            }

            bufferedReader.close()

            val fileOutputStream = openFileOutput("expenses.txt", MODE_PRIVATE)
            fileOutputStream.write(updatedData.toString().toByteArray())
            fileOutputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
