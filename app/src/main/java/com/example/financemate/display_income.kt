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

class display_income : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_income)

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

        // LinearLayout container for income data
        val incomeListContainer: LinearLayout = findViewById(R.id.incomeListContainer)

        // Load and display stored incomes
        loadIncomes(incomeListContainer)
    }

    private fun loadIncomes(incomeListContainer: LinearLayout) {
        incomeListContainer.removeAllViews()  // Clear the previous list

        try {
            val fileInputStream: FileInputStream = openFileInput("incomes.txt")
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var line: String?
            var currentTitle = ""
            var currentAmount = ""
            var currentDate = ""

            // Read each line and create a CardView for each income entry
            while (bufferedReader.readLine().also { line = it } != null) {
                // Log the line to see what we read
                Log.d("IncomeData", "Read line: $line")

                if (line?.startsWith("Title:") == true) {
                    // Start a new entry
                    currentTitle = line.removePrefix("Title:").trim()
                } else if (line?.startsWith("Amount:") == true) {
                    currentAmount = line.removePrefix("Amount:").trim()
                } else if (line?.startsWith("Date:") == true) {
                    currentDate = line.removePrefix("Date:").trim()

                    // Once we have all three fields, create a CardView
                    createIncomeCard(incomeListContainer, currentTitle, currentAmount, currentDate)
                }
            }

            bufferedReader.close()

        } catch (e: Exception) {
            // Handle error and display a message if no incomes are found
            Log.e("IncomeData", "Error reading file", e)
            incomeListContainer.addView(TextView(this).apply {
                text = "No incomes found."
                textSize = 18f
                setTextColor(resources.getColor(android.R.color.black))
            })
        }
    }

    private fun createIncomeCard(
        incomeListContainer: LinearLayout,
        title: String,
        amount: String,
        date: String
    ) {
        // Create CardView for each income entry
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

        firstColumnLayout.addView(titleTextView)
        firstColumnLayout.addView(amountTextView)
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
            // Start the EditIncomeActivity and pass the current data to edit
            val intent = Intent(this, edit_income::class.java)
            intent.putExtra("TITLE", title)
            intent.putExtra("AMOUNT", amount)
            intent.putExtra("DATE", date)
            startActivity(intent)
        }

        // Create the Delete Button
        val deleteButton = ImageView(this)
        deleteButton.setImageResource(R.drawable.ic_delete)  // Set your delete icon
        deleteButton.layoutParams = iconsize
        deleteButton.setPadding(0, 10, 0, 10)  // Padding between the buttons
        deleteButton.setOnClickListener {
            // Remove the income from the file
            removeIncomeFromFile(title)  // Use the title as the unique identifier

            // Re-load the incomes after deletion
            loadIncomes(incomeListContainer)
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
        incomeListContainer.addView(cardView)

        // Add margin top and bottom to each CardView
        val layoutParams = cardView.layoutParams as LinearLayout.LayoutParams
        layoutParams.topMargin = 30
        layoutParams.bottomMargin = 30
        cardView.layoutParams = layoutParams
    }

    private fun removeIncomeFromFile(title: String) {
        try {
            // Read the file contents into a StringBuilder
            val fileInputStream: FileInputStream = openFileInput("incomes.txt")
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            val updatedData = StringBuilder()
            var line: String?
            var isDeleting = false

            while (bufferedReader.readLine().also { line = it } != null) {
                // If the line contains the title to delete, skip the next two lines (Amount and Date)
                if (line?.startsWith("Title: $title") == true) {
                    isDeleting = true
                }

                if (isDeleting) {
                    // Skip the next two lines: Amount and Date
                    bufferedReader.readLine() // Skip Amount line
                    bufferedReader.readLine() // Skip Date line
                    isDeleting = false
                } else {
                    updatedData.append(line).append("\n")
                }
            }

            bufferedReader.close()

            // Write the updated data back to the file, excluding the deleted income entry
            val fileOutputStream: FileOutputStream = openFileOutput("incomes.txt", MODE_PRIVATE)
            fileOutputStream.write(updatedData.toString().toByteArray())
            fileOutputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


