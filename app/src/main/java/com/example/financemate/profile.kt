package com.example.financemate

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class profile : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)


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

            if (validateForm(name, age, email)) {
                saveData(name, age, email, currency, budgetMonth)
                Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
            }
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
}
