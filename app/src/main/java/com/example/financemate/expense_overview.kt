package com.example.financemate

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.NumberFormat
import java.util.Locale



class expense_overview : AppCompatActivity() {


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_expense_overview)

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

            // Get references to the TextViews for displaying the totals
            val housingTextView: TextView = findViewById(R.id.textViewhouse)
            val foodTextView: TextView = findViewById(R.id.textViewfood)
            val entertainmentTextView: TextView = findViewById(R.id.textViewentertain)
            val healthTextView: TextView = findViewById(R.id.textViewhealth)
            val educationTextView: TextView = findViewById(R.id.textViewedu) // Add Education TextView

            // Calculate totals for each category
            val housingTotal = calculateTotalForCategory("Housing")
            val foodTotal = calculateTotalForCategory("Food")
            val entertainmentTotal = calculateTotalForCategory("Entertainment")
            val healthTotal = calculateTotalForCategory("Health")
            val educationTotal = calculateTotalForCategory("Education") // Calculate Education total

            // Update the TextViews with the totals
            housingTextView.text = formatCurrency(housingTotal)
            foodTextView.text = formatCurrency(foodTotal)
            entertainmentTextView.text = formatCurrency(entertainmentTotal)
            healthTextView.text = formatCurrency(healthTotal)
            educationTextView.text = formatCurrency(educationTotal) // Display Education total
        }

        // Function to calculate total for a specific category
        private fun calculateTotalForCategory(category: String): Double {
            var totalAmount = 0.0
            try {
                val fileInputStream = openFileInput("expenses.txt")
                val inputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader = BufferedReader(inputStreamReader)

                var line: String?
                var currentCategory: String? = null
                var currentAmount: String?

                // Read each line in the file
                while (bufferedReader.readLine().also { line = it } != null) {
                    // Safely handle null values
                    if (line?.startsWith("Category:") == true) {
                        currentCategory = line.removePrefix("Category:").trim()
                    }
                    if (line?.startsWith("Amount:") == true) {
                        currentAmount = line.removePrefix("Amount:").trim()
                        // Only process the amount if the category matches
                        if (currentCategory == category) {
                            totalAmount += currentAmount.toDoubleOrNull() ?: 0.0
                        }
                    }
                }

                bufferedReader.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return totalAmount
        }

        // Function to format the amount with currency symbol
        private fun formatCurrency(amount: Double): String {
            return NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(amount)
        }
}

