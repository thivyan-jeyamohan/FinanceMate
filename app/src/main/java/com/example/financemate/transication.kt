package com.example.financemate

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.NumberFormat
import java.util.Locale

class transication : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transication)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val addin: ImageView = findViewById(R.id.imageView19)
        addin.setOnClickListener {
            val intent = Intent(this, Incomeform::class.java)
            startActivity(intent)
        }

        val viewin: ImageView = findViewById(R.id.imageView18)
        viewin.setOnClickListener {
            val intent = Intent(this, display_income::class.java)
            startActivity(intent)
        }

        val addex: ImageView = findViewById(R.id.imageView20)
        addex.setOnClickListener {
            val intent = Intent(this, Expenseform::class.java)
            startActivity(intent)
        }

        val viewex: ImageView = findViewById(R.id.imageView21)
        viewex.setOnClickListener {
            val intent = Intent(this, display_expense::class.java)
            startActivity(intent)
        }


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

        // Calculate and display total expense
        val totalExpense = calculateTotalExpense()
        val expenseTextView: TextView = findViewById(R.id.textView9)
        expenseTextView.text = formatCurrency(totalExpense)

        // Calculate and display total income
        val totalIncome = calculateTotalIncome()
        val incomeTextView: TextView = findViewById(R.id.textView8)
        incomeTextView.text = formatCurrency(totalIncome)

        // Calculate cashflow
        val cashflow = totalIncome - totalExpense
        val cashflowTextView: TextView = findViewById(R.id.textView6)
        cashflowTextView.text = formatCurrency(cashflow)
    }

    // Function to calculate total expense from the file
    private fun calculateTotalExpense(): Double {
        var totalAmount = 0.0
        try {
            val fileInputStream = openFileInput("expenses.txt")
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                if (line?.startsWith("Amount:") == true) {
                    val currentAmount = line.removePrefix("Amount:").trim()
                    totalAmount += currentAmount.toDoubleOrNull() ?: 0.0
                }
            }
            bufferedReader.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return totalAmount
    }

    // Function to calculate total income from the file
    private fun calculateTotalIncome(): Double {
        var totalAmount = 0.0
        try {
            val fileInputStream = openFileInput("incomes.txt")
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                if (line?.startsWith("Amount:") == true) {
                    val currentAmount = line.removePrefix("Amount:").trim()
                    totalAmount += currentAmount.toDoubleOrNull() ?: 0.0
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
        return NumberFormat.getCurrencyInstance(Locale("si", "LK")).format(amount)
    }
}