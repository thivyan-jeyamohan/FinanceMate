package com.example.financemate

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import android.content.Intent

class Expenseform : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenseform)

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

        // Initialize views
        val titleEditText: EditText = findViewById(R.id.transaction_name)
        val amountEditText: EditText = findViewById(R.id.amount)
        val categorySpinner: Spinner = findViewById(R.id.category_spinner)
        val dateEditText: EditText = findViewById(R.id.date)
        val addExpenseButton: ImageView = findViewById(R.id.imageView16)
        val viewStoredIncomeButton: ImageView = findViewById(R.id.viewStoredExpenseButton)

        // Set DatePickerDialog for the Date EditText
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            // Set the date in the format "MM/dd/yyyy"
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

        // Set the button click listener to handle form submission
        addExpenseButton.setOnClickListener {
            // Get the user inputs from the EditText and Spinner
            val title = titleEditText.text.toString().trim()
            val amount = amountEditText.text.toString().trim()
            val category = categorySpinner.selectedItem.toString()
            val date = dateEditText.text.toString().trim()

            // Form validation: Check if any field is empty
            if (title.isEmpty() || amount.isEmpty() || category == "Select Category" || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Prepare the expense data to be stored
                val expenseData = "Title: $title\nAmount: $amount\nCategory: $category\nDate: $date\n\n"

                // Store the data in internal storage (append mode)
                try {
                    val fileOutputStream: FileOutputStream = openFileOutput("expenses.txt", MODE_APPEND) // Use MODE_APPEND to add data
                    val outputStreamWriter = OutputStreamWriter(fileOutputStream)
                    outputStreamWriter.write(expenseData) // Write the expense data
                    outputStreamWriter.close()

                    // Clear the input fields after saving
                    titleEditText.text.clear()
                    amountEditText.text.clear()
                    dateEditText.text.clear()

                    // Show a success message
                    Toast.makeText(this, "Expense Added Successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // When the "View Stored Expense" button is clicked, navigate to DisplayExpenseActivity
        viewStoredIncomeButton.setOnClickListener {
            val intent = Intent(this, display_expense::class.java)  // Change this to the appropriate activity for viewing expenses
            startActivity(intent)
        }
    }
}
