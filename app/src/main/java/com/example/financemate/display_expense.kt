package com.example.financemate

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.TextView
import android.content.Intent
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.FileOutputStream
import android.util.Log
import android.graphics.Color
import androidx.core.view.marginBottom
import java.io.File

class display_expense : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_expense)

        val file = File(filesDir, "expenses.txt")
        val filePath = file.absolutePath
        Log.d("FilePath", "File path: $filePath")

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

        // Add ImageView for restoring deleted data
        val restoreButton: ImageView = findViewById(R.id.imageView6)
        restoreButton.setOnClickListener {
            restoreDeletedIncomes()
        }

        // LinearLayout container for income data
        val expenseListContainer: LinearLayout = findViewById(R.id.expenseListContainer)

        // Load and display stored incomes
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

            // Read each line and create a CardView for each income entry
            while (bufferedReader.readLine().also { line = it } != null) {
                // Log the line to see what we read
                Log.d("ExpenseData", "Read line: $line")

                if (line?.startsWith("Title:") == true) {
                    // Start a new entry
                    currentTitle = line.removePrefix("Title:").trim()
                } else if (line?.startsWith("Amount:") == true) {
                    currentAmount = line.removePrefix("Amount:").trim()
                }  else if (line?.startsWith("Date:") == true) {
                    currentDate = line.removePrefix("Date:").trim()
                } else if (line?.startsWith("Category:") == true) {
                    currentCategory = line.removePrefix("Category:").trim()

                    // Once we have all three fields, create a CardView
                    createExpenseCard(expenseListContainer, currentTitle, currentAmount, currentDate,currentCategory)
                }
            }

            bufferedReader.close()

        } catch (e: Exception) {
            // Handle error and display a message if no incomes are found
            Log.e("IncomeData", "Error reading file", e)
            expenseListContainer.addView(TextView(this).apply {
                text = "No Expense found."
                textSize = 18f
                setTextColor(resources.getColor(android.R.color.black))
            })
        }
    }

    private fun createExpenseCard(
        expenseListContainer: LinearLayout,
        title: String,
        amount: String,
        date: String,
        category: String
    ) {
        // Create CardView for each income entry
        val cardView = CardView(this)
        cardView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )

        cardView.radius = 25f
        cardView.setCardBackgroundColor(Color.parseColor("#dfeeff"))  // Custom background color
        cardView.setCardElevation(8f)
        cardView.setContentPadding(30, 16, 30, 70)


        // Create a horizontal LinearLayout to divide the CardView into two columns
        val horizontalLayout = LinearLayout(this)
        horizontalLayout.orientation = LinearLayout.HORIZONTAL // Horizontal orientation for two columns
        horizontalLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // First column (Title, Amount, Date)
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

        val dateTextView = TextView(this)
        dateTextView.text = "Date: $date"
        dateTextView.textSize = 16f
        dateTextView.setTextColor(resources.getColor(android.R.color.black))

        val categoryTextView = TextView(this)
        categoryTextView.text = "Category: $category"
        categoryTextView.textSize = 16f
        categoryTextView.setTextColor(resources.getColor(android.R.color.black))

        firstColumnLayout.addView(titleTextView)
        firstColumnLayout.addView(amountTextView)
        firstColumnLayout.addView(dateTextView)
        firstColumnLayout.addView(categoryTextView)

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
            // Start the EditIncomeActivity and pass the current data to edit
            val intent = Intent(this, edit_expense::class.java)
            intent.putExtra("TITLE", title)
            intent.putExtra("AMOUNT", amount)
            intent.putExtra("DATE", date)
            intent.putExtra("CATEGORY", category)
            startActivity(intent)
        }

        // Create the Delete Button
        val deleteButton = ImageView(this)
        deleteButton.setImageResource(R.drawable.ic_delete)  // Set your delete icon
        deleteButton.layoutParams = iconsize
        deleteButton.setPadding(0, 10, 0, 10)  // Padding between the buttons
        deleteButton.setOnClickListener {
            // Remove the income from the file and store it in the deleted file
            removeExpenseFromFile(title)  // Use the title as the unique identifier

            // Re-load the incomes after deletion
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

        // Add CardView to the income list container
        expenseListContainer.addView(cardView)

        // Add margin top and bottom to each CardView
        val layoutParams = cardView.layoutParams as LinearLayout.LayoutParams
        layoutParams.topMargin = 30
        layoutParams.bottomMargin = 30
        cardView.layoutParams = layoutParams
    }

    private fun removeExpenseFromFile(title: String) {
        try {
            // Read the incomes file into a StringBuilder
            val fileInputStream: FileInputStream = openFileInput("expenses.txt")
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val updatedData = StringBuilder()  // Stores remaining data
            val deletedData = StringBuilder()  // Stores deleted data

            var line: String?
            var isDeleting = false

            // Read each line and check if it is to be deleted
            while (bufferedReader.readLine().also { line = it } != null) {
                if (line?.startsWith("Title: $title") == true) {
                    isDeleting = true
                    deletedData.append(line).append("\n")  // Save deleted title
                    deletedData.append(bufferedReader.readLine()).append("\n")  // Skip the amount
                    deletedData.append(bufferedReader.readLine()).append("\n")  // Skip the date
                } else {
                    updatedData.append(line).append("\n")
                }
            }

            bufferedReader.close()

            // Write the updated data back to the incomes file
            val fileOutputStream: FileOutputStream = openFileOutput("expenses.txt", MODE_PRIVATE)
            fileOutputStream.write(updatedData.toString().toByteArray())
            fileOutputStream.close()

            // Write deleted data to the deleted_expenses.txt file
            val deletedFileOutputStream: FileOutputStream = openFileOutput("deleted_expenses.txt", MODE_APPEND)
            deletedFileOutputStream.write(deletedData.toString().toByteArray())
            deletedFileOutputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun restoreDeletedIncomes() {
        try {
            val fileInputStream: FileInputStream = openFileInput("deleted_expenses.txt")
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val restoredData = StringBuilder()
            var line: String?

            // Read the deleted incomes and restore them to the original incomes file
            while (bufferedReader.readLine().also { line = it } != null) {
                restoredData.append(line).append("\n")
            }

            bufferedReader.close()

            // Append the deleted data to expenses.txt
            val fileOutputStream: FileOutputStream = openFileOutput("expenses.txt", MODE_APPEND)
            fileOutputStream.write(restoredData.toString().toByteArray())
            fileOutputStream.close()

            // Empty the deleted_expenses.txt file after restoring
            val deletedFileOutputStream: FileOutputStream = openFileOutput("deleted_expenses.txt", MODE_PRIVATE)
            deletedFileOutputStream.write("".toByteArray())  // Clear the content of the file
            deletedFileOutputStream.close()

            // Reload the incomes after restoration
            loadExpenses(findViewById(R.id.expenseListContainer))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}








