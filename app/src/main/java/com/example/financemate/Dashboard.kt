package com.example.financemate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financemate.Expenseform
import com.example.financemate.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.NumberFormat
import java.util.*

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)




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




        // Adjust window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the ImageView by its ID and set the OnClickListener
        val imageView: ImageView = findViewById(R.id.imageView3)
        imageView.setOnClickListener {
            val intent = Intent(this, Expenseform::class.java)
            startActivity(intent)
        }

        val imageViewin: ImageView = findViewById(R.id.imageView2)
        imageViewin.setOnClickListener {
            val intent = Intent(this, Incomeform::class.java)
            startActivity(intent)
        }

        // Calculate and display total expense
        val totalExpense = calculateTotalExpense()
        val expenseTextView: TextView = findViewById(R.id.expense_value)
        expenseTextView.text = formatCurrency(totalExpense)

        // Calculate and display total income
        val totalIncome = calculateTotalIncome()
        val incomeTextView: TextView = findViewById(R.id.income_value)
        incomeTextView.text = formatCurrency(totalIncome)

        // Calculate cashflow
        val cashflow = totalIncome - totalExpense
        val cashflowTextView: TextView = findViewById(R.id.monthly_cash_flow)
        cashflowTextView.text = formatCurrency(cashflow)

        // Check if cashflow is negative and send a notification
        if (cashflow < 0) {
            sendNegativeCashflowNotification()
        }
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

    // Function to send a notification when cashflow is negative
    private fun sendNegativeCashflowNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android 8.0 and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "cashflow_alert_channel"
            val channelName = "Cashflow Alert"
            val channelDescription = "Notifies when cashflow is negative"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val notification = NotificationCompat.Builder(this, "cashflow_alert_channel")
            .setSmallIcon(R.drawable.ic_notification)  // Set your own icon
            .setContentTitle("Cashflow Alert")
            .setContentText("Your cashflow has gone negative!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Show the notification
        notificationManager.notify(1, notification)
    }


}
