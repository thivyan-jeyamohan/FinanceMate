package com.example.financemate

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import android.content.Intent

class Incomeform : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incomeform)


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
        val dateEditText: EditText = findViewById(R.id.date)
        val addIncomeButton: ImageView = findViewById(R.id.imageView16)

        val viewStoredIncomeButton: ImageView = findViewById(R.id.viewStoredIncomeButton)

        viewStoredIncomeButton.setOnClickListener {
            // Start the DisplayIncomeActivity to show the stored data
            val intent = Intent(this, display_income::class.java)
            startActivity(intent)
        }

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

        // Set the button click listener to handle form submission
        addIncomeButton.setOnClickListener {
            // Get the user inputs from the EditText
            val title = titleEditText.text.toString().trim()
            val amount = amountEditText.text.toString().trim()
            val date = dateEditText.text.toString().trim()

            // Form validation: Check if any field is empty
            if (title.isEmpty() || amount.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Prepare the income data to be stored
                val incomeData = "Title: $title\nAmount: $amount\nDate: $date\n\n"

                // Store the data in internal storage (append mode)
                try {
                    val fileOutputStream: FileOutputStream = openFileOutput("incomes.txt", MODE_APPEND) // Use MODE_APPEND to add data
                    val outputStreamWriter = OutputStreamWriter(fileOutputStream)
                    outputStreamWriter.write(incomeData) // Write the income data
                    outputStreamWriter.close()

                    // Clear the input fields after saving
                    titleEditText.text.clear()
                    amountEditText.text.clear()
                    dateEditText.text.clear()

                    // Show a success message
                    Toast.makeText(this, "Income Added Successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error saving income", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
