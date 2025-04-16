package com.example.financemate

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.content.Intent
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.FileOutputStream
import android.util.Log
import android.graphics.Color

class display_expense : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_expense)

        // Button to add new expense (navigate to ExpenseForm)
        val viewStoredExpenseButton: ImageView = findViewById(R.id.addingExpenseImageView)

        viewStoredExpenseButton.setOnClickListener {
            val intent = Intent(this, Expenseform::class.java)
            startActivity(intent)
        }

        // LinearLayout container for expense data
        val expenseListContainer: LinearLayout = findViewById(R.id.expenseListContainer)

        // Load and display stored expenses
        loadExpenses(expenseListContainer)
    }

    private fun loadExpenses(expenseListContainer: LinearLayout) {
        expenseListContainer.removeAllViews()  // Clear the previous list

        try {
            val fileInputStream: FileInputStream = openFileInput("expenses.txt")
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var line: String?
            var currentTitle = ""
            var currentAmount = ""
            var currentCategory = ""
            var currentDate = ""

            // Read each line and create a CardView for each expense entry
            while (bufferedReader.readLine().also { line = it } != null) {
                // Log the line to see what we read
                Log.d("ExpenseData", "Read line: $line")

                if (line?.startsWith("Title:") == true) {
                    // Start a new entry
                    currentTitle = line.removePrefix("Title:").trim()
                } else if (line?.startsWith("Amount:") == true) {
                    currentAmount = line.removePrefix("Amount:").trim()
                } else if (line?.startsWith("Category:") == true) {
                    currentCategory = line.removePrefix("Category:").trim()
                } else if (line?.startsWith("Date:") == true) {
                    currentDate = line.removePrefix("Date:").trim()

                    // Once we have all four fields, create a CardView
                    createExpenseCard(expenseListContainer, currentTitle, currentAmount, currentCategory, currentDate)
                }
            }

            bufferedReader.close()

        } catch (e: Exception) {
            // Handle error and display a message if no expenses are found
            Log.e("ExpenseData", "Error reading file", e)
            expenseListContainer.addView(TextView(this).apply {
                text = "No expenses found."
                textSize = 18f
                setTextColor(resources.getColor(android.R.color.black))
            })
        }
    }

    private fun createExpenseCard(
        expenseListContainer: LinearLayout,
        title: String,
        amount: String,
        category: String,
        date: String
    ) {
        // Create CardView for each expense entry
        val cardView = CardView(this)
        cardView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        cardView.radius = 25f
        cardView.setCardBackgroundColor(Color.parseColor("#dfeeff"))  // Custom background color
        cardView.setCardElevation(8f)
        cardView.setContentPadding(30, 16, 30, 16)

        // Create a horizontal LinearLayout to divide the CardView into two columns
        val horizontalLayout = LinearLayout(this)
        horizontalLayout.orientation = LinearLayout.HORIZONTAL // Horizontal orientation for two columns
        horizontalLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // First column (Title, Amount, Category, Date)
        val firstColumnLayout = LinearLayout(this)
        firstColumnLayout.orientation = LinearLayout.VERTICAL
        firstColumnLayout.layoutParams = LinearLayout.LayoutParams(
            0,  // Use weight for proportional width
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f  // 50% width of the parent layout
        )

        val titleTextView = TextView(this)
        titleTextView.text = "Title: $title"
        titleTextView.textSize = 18f
        titleTextView.setTextColor(resources.getColor(android.R.color.black))
        titleTextView.setPadding(0, 0, 0, 10)

        val amountTextView = TextView(this)
        amountTextView.text = "Amount: $amount"
        amountTextView.textSize = 16f
        amountTextView.setTextColor(resources.getColor(android.R.color.black))
        amountTextView.setPadding(0, 0, 0, 10)

        val categoryTextView = TextView(this)
        categoryTextView.text = "Category: $category"
        categoryTextView.textSize = 16f
        categoryTextView.setTextColor(resources.getColor(android.R.color.black))

        val dateTextView = TextView(this)
        dateTextView.text = "Date: $date"
        dateTextView.textSize = 16f
        dateTextView.setTextColor(resources.getColor(android.R.color.black))

        firstColumnLayout.addView(titleTextView)
        firstColumnLayout.addView(amountTextView)
        firstColumnLayout.addView(categoryTextView)
        firstColumnLayout.addView(dateTextView)

        // Second column (Edit, Delete buttons)
        val secondColumnLayout = LinearLayout(this)
        secondColumnLayout.orientation = LinearLayout.VERTICAL
        secondColumnLayout.layoutParams = LinearLayout.LayoutParams(
            0,  // Use weight for proportional width
            LinearLayout.LayoutParams.WRAP_CONTENT,
            0.3f  // 30% width of the parent layout for buttons
        )

        // Create the Edit Button
        val editButton = ImageView(this)
        editButton.setImageResource(R.drawable.edit)  // Set your edit icon
        val iconsize = LinearLayout.LayoutParams(100, 100)
        editButton.layoutParams = iconsize
        editButton.setPadding(0, 10, 0, 10)  // Padding between the buttons
        editButton.setOnClickListener {
            // Start the EditExpenseActivity and pass the current data to edit
            val intent = Intent(this, edit_expense::class.java)
            intent.putExtra("TITLE", title)
            intent.putExtra("AMOUNT", amount)
            intent.putExtra("CATEGORY", category)
            intent.putExtra("DATE", date)
            startActivity(intent)
        }

        // Create the Delete Button
        val deleteButton = ImageView(this)
        deleteButton.setImageResource(R.drawable.ic_delete)  // Set your delete icon
        deleteButton.layoutParams = iconsize
        deleteButton.setPadding(0, 10, 0, 10)  // Padding between the buttons
        deleteButton.setOnClickListener {
            // Remove the expense from the file
            removeExpenseFromFile(title)  // Use the title as the unique identifier

            // Re-load the expenses after deletion
            loadExpenses(expenseListContainer)
        }

        // Add both buttons to the second column (vertical layout)
        secondColumnLayout.addView(editButton)
        secondColumnLayout.addView(deleteButton)

        // Add both columns to the horizontal layout
        horizontalLayout.addView(firstColumnLayout)
        horizontalLayout.addView(secondColumnLayout)

        // Add the horizontal layout to the card view
        cardView.addView(horizontalLayout)

        // Add CardView to the expense list container
        expenseListContainer.addView(cardView)

        // Add margin top and bottom to each CardView
        val layoutParams = cardView.layoutParams as LinearLayout.LayoutParams
        layoutParams.topMargin = 30
        layoutParams.bottomMargin = 30
        cardView.layoutParams = layoutParams
    }

    private fun removeExpenseFromFile(title: String) {
        try {
            // Read the file contents into a StringBuilder
            val fileInputStream: FileInputStream = openFileInput("expenses.txt")
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val updatedData = StringBuilder()
            var line: String?
            var isDeleting = false

            while (bufferedReader.readLine().also { line = it } != null) {
                // If the line contains the title to delete, skip the next three lines (Amount, Category, Date)
                if (line?.startsWith("Title: $title") == true) {
                    isDeleting = true
                }

                if (isDeleting) {
                    // Skip the next three lines: Amount, Category, and Date
                    bufferedReader.readLine() // Skip Amount line
                    bufferedReader.readLine() // Skip Category line
                    bufferedReader.readLine() // Skip Date line
                    isDeleting = false
                } else {
                    updatedData.append(line).append("\n")
                }
            }

            bufferedReader.close()

            // Write the updated data back to the file, excluding the deleted expense entry
            val fileOutputStream: FileOutputStream = openFileOutput("expenses.txt", MODE_PRIVATE)
            fileOutputStream.write(updatedData.toString().toByteArray())
            fileOutputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
