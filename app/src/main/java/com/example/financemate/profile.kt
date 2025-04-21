package com.example.financemate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.channels.FileChannel
import java.text.NumberFormat
import java.util.Locale

class profile : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var userAge: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)


        // Find the ImageView by ID
        val imageView33: ImageView = findViewById(R.id.imageView33)

        // Set an OnClickListener to trigger the download when clicked
        imageView33.setOnClickListener {
            // Trigger the download when the ImageView is clicked
            downloadFiles()
        }


        imageView33.setOnLongClickListener {
            sendFilesViaEmail()
            true  // Indicate that the long-click was handled
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

        // Initialize the input fields
        val nameEditText: EditText = findViewById(R.id.editTextName3)
        val ageEditText: EditText = findViewById(R.id.editTextAge2)
        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val currencySpinner: Spinner = findViewById(R.id.spinnerCurrency)
        val budgetMonthSpinner: Spinner = findViewById(R.id.spinnerBudgetMonth)
        val saveButton: Button = findViewById(R.id.buttonSave)

        // Set up the spinners
        val currencyOptions = arrayOf("LKR", "USD")
        val budgetMonthOptions = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        val currencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencyOptions)
        currencySpinner.adapter = currencyAdapter

        val budgetMonthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, budgetMonthOptions)
        budgetMonthSpinner.adapter = budgetMonthAdapter

        // Load saved data into the form or set default values if not found
        loadData(nameEditText, ageEditText, emailEditText, currencySpinner, budgetMonthSpinner)

        // Set up the save button
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString()
            val email = emailEditText.text.toString()
            val currency = currencySpinner.selectedItem.toString()
            val budgetMonth = budgetMonthSpinner.selectedItem.toString()

            val ages = ageEditText.text.toString().toIntOrNull() ?: 0
            userAge = ages
            Log.d("ProfileActivity", "The user age is: $userAge")

            // Calculate and display total expense
            val totalExpense = calculateTotalExpense()

            if (totalExpense > userAge) {
                Log.d("ProfileActivity", "The user age is: $totalExpense")
                Log.d("ProfileActivity", "The user age is: $userAge")
                sendNegativebudgetlimitNotification()
            }

            if (validateForm(name, age, email)) {
                saveData(name, age, email, currency, budgetMonth)
                Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
            }
        }


    }





    private fun sendFilesViaEmail() {
        try {
            // Define the paths of the files to be emailed
            val incomeFile = File(filesDir, "incomes.txt")
            val expenseFile = File(filesDir, "expenses.txt")

            // Ensure the files exist
            if (incomeFile.exists() && expenseFile.exists()) {
                // Get the URIs of the files using FileProvider
                val incomeUri: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", incomeFile)
                val expenseUri: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", expenseFile)

                // Create an email intent
                val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
                emailIntent.type = "application/octet-stream"

                // Add the email recipient
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("jthivyanp@gmail.com"))

                // Add the email subject
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Backup Files")

                // Add the email body text
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find the backup files attached.")

                // Add both files as attachments
                val uris = ArrayList<Uri>()
                uris.add(incomeUri)
                uris.add(expenseUri)

                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                // Start the activity to send the email
                startActivity(Intent.createChooser(emailIntent, "Send email"))
            } else {
                Toast.makeText(this, "Files do not exist.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to send the files via email.", Toast.LENGTH_SHORT).show()
        }
    }






    private fun downloadFiles() {
        try {
            // Define the paths of the files to be downloaded
            val incomeFile = File(filesDir, "incomes.txt")
            val expenseFile = File(filesDir, "expenses.txt")

            // Print the file paths in the console (Logcat)
            Log.d("DownloadFiles", "Income file path: ${incomeFile.absolutePath}")
            Log.d("DownloadFiles", "Expense file path: ${expenseFile.absolutePath}")

            // Check if the files exist
            if (incomeFile.exists() && expenseFile.exists()) {
                // Log if the files exist
                Log.d("DownloadFiles", "Both files exist, proceeding with download.")

                // Get the URIs of the files using FileProvider
                val incomeUri: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", incomeFile)
                val expenseUri: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", expenseFile)

                // Create an Intent to trigger the download of both files
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                intent.type = "application/octet-stream"

                // Add both files to the intent
                val uris = ArrayList<Uri>()
                uris.add(incomeUri)
                uris.add(expenseUri)

                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                // Start the activity to allow the user to download both files
                startActivity(Intent.createChooser(intent, "Download Backup Files"))
            } else {
                // Log that the files do not exist
                Log.d("DownloadFiles", "One or both files do not exist.")
                Toast.makeText(this, "Files do not exist.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to share the files.", Toast.LENGTH_SHORT).show()
        }
    }





    // Function to validate the form
    private fun validateForm(name: String, age: String, email: String): Boolean {
        // Validate name: Name should not contain numbers
        if (name.isEmpty() || name.matches(".*\\d.*".toRegex())) {
            Toast.makeText(this, "Name cannot contain numbers", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate age: Age must be a valid number
        if (age.isEmpty() || !age.matches("\\d+".toRegex())) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate email: Email should be in correct format
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Function to save data into SharedPreferences
    private fun saveData(name: String, age: String, email: String, currency: String, budgetMonth: String) {
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.putString("age", age)
        editor.putString("email", email)
        editor.putString("currency", currency)
        editor.putString("budgetMonth", budgetMonth)
        editor.apply()
    }

    // Function to load saved data into the form or set default values if no saved data exists
    private fun loadData(
        nameEditText: EditText,
        ageEditText: EditText,
        emailEditText: EditText,
        currencySpinner: Spinner,
        budgetMonthSpinner: Spinner
    ) {
        val name = sharedPreferences.getString("name", "Thivyan") // Default: Thivyan
        val age = sharedPreferences.getString("age", "23") // Default: 23
        val email = sharedPreferences.getString("email", "thivyan@example.com") // Default: thivyan@example.com
        val currency = sharedPreferences.getString("currency", "LKR") // Default: LKR
        val budgetMonth = sharedPreferences.getString("budgetMonth", "April") // Default: April

        nameEditText.setText(name)
        ageEditText.setText(age)
        emailEditText.setText(email)

        // Set the spinner values
        val currencyOptions = arrayOf("LKR", "USD")
        currencySpinner.setSelection(currencyOptions.indexOf(currency))

        val budgetMonthOptions = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        budgetMonthSpinner.setSelection(budgetMonthOptions.indexOf(budgetMonth))
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


    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(amount)
    }


    private fun sendNegativebudgetlimitNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android 8.0 and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "budgetlimit_alert_channel"
            val channelName = "budgetlimit Alert"
            val channelDescription = "Notifies when budgetlimit is aboved"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val notification = NotificationCompat.Builder(this, "budgetlimit_alert_channel")
            .setSmallIcon(R.drawable.ic_notification)  // Set your own icon
            .setContentTitle("budgetlimit Alert")
            .setContentText("Your budgetlimit has gone above!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Show the notification
        notificationManager.notify(1, notification)
    }



}
