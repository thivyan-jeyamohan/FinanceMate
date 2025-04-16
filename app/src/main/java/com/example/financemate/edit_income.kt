package com.example.financemate

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class edit_income : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_income)

        val titleEditText: EditText = findViewById(R.id.titleEditText)
        val amountEditText: EditText = findViewById(R.id.amountEditText)
        val dateEditText: EditText = findViewById(R.id.dateEditText)
        val saveButton: ImageView = findViewById(R.id.saveButton)

        // Get the current values passed from the main activity
        val title = intent.getStringExtra("TITLE") ?: ""
        val amount = intent.getStringExtra("AMOUNT") ?: ""
        val date = intent.getStringExtra("DATE") ?: ""

        // Set the current values in the EditTexts
        titleEditText.setText(title)
        amountEditText.setText(amount)
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

        // When the Save button is clicked, update the file and return
        saveButton.setOnClickListener {
            val newTitle = titleEditText.text.toString()
            val newAmount = amountEditText.text.toString()
            val newDate = dateEditText.text.toString()

            // Update the file with the new data
            updateIncomeInFile(title, newTitle, newAmount, newDate)

            // Start the display_income activity explicitly
            val intent = Intent(this, display_income::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // To clear the previous activity stack
            startActivity(intent)
        }
    }

    private fun updateIncomeInFile(oldTitle: String, newTitle: String, newAmount: String, newDate: String) {
        try {
            // Read the file contents into a StringBuilder
            val fileInputStream = openFileInput("incomes.txt")
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val updatedData = StringBuilder()
            var line: String?
            var isUpdating = false

            // Loop through all lines to find the matching Title and update it
            while (bufferedReader.readLine().also { line = it } != null) {
                // If this line matches the old title, update it with the new data
                if (line?.startsWith("Title: $oldTitle") == true) {
                    // Add updated data to StringBuilder with the new title
                    updatedData.append("Title: $newTitle\n")
                    updatedData.append("Amount: $newAmount\n")
                    updatedData.append("Date: $newDate\n")
                    isUpdating = true
                } else if (!isUpdating) {
                    // Append lines that are not being updated
                    updatedData.append(line).append("\n")
                } else {
                    // Skip the next two lines (Amount and Date) and then stop updating
                    bufferedReader.readLine() // Skip Amount line
                    bufferedReader.readLine() // Skip Date line
                    isUpdating = false
                }
            }

            bufferedReader.close()

            // Write the updated data back to the file, excluding the old entry
            val fileOutputStream = openFileOutput("incomes.txt", MODE_PRIVATE)
            fileOutputStream.write(updatedData.toString().toByteArray())
            fileOutputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
