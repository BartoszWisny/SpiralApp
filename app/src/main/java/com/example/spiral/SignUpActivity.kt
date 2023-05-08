package com.example.spiral

import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var signUpLayout: ConstraintLayout
    private lateinit var firstNameText: EditText
    private lateinit var surnameText: EditText
    private lateinit var dateOfBirthText: TextView
    private lateinit var genderText: Spinner
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var confirmPasswordText: EditText
    private var calendar = Calendar.getInstance()
    private var dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private lateinit var authentication: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { wrap(it, "en") })
    }

    private fun wrap(context: Context, language: String?): ContextWrapper {
        var context = context
        val resources = context.resources
        val configuration = resources.configuration
        val newLocale = Locale(language)
        configuration.setLocale(newLocale)
        val localeList = LocaleList(newLocale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)
        context = context.createConfigurationContext(configuration)
        return ContextWrapper(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signUpLayout = findViewById(R.id.sign_up_layout)
        firstNameText = findViewById(R.id.sign_up_first_name)
        surnameText = findViewById(R.id.sign_up_surname)
        dateOfBirthText = findViewById(R.id.sign_up_date_of_birth)
        genderText = findViewById(R.id.sign_up_gender)
        emailText = findViewById(R.id.sign_up_email)
        passwordText = findViewById(R.id.sign_up_password)
        confirmPasswordText = findViewById(R.id.sign_up_confirm_password)
        dateOfBirthText.text = LocalDate.now().minusYears(18).format(dateFormatter).toString()
        authentication = FirebaseAuth.getInstance()
        val adapter = ArrayAdapter(this, R.layout.spinner, arrayOf("Female", "Male", "I don't wish to specify"))
        adapter.setDropDownViewResource(R.layout.spinner_item)
        genderText.adapter = adapter

        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                signUpLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                signUpLayout.background = ResourcesCompat.getDrawable(resources, R.color.blue_1, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                signUpLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                firstNameText.setText("")
                surnameText.setText("")
                dateOfBirthText.text = ""
                genderText.setSelection(0)
                emailText.setText("")
                passwordText.setText("")
                confirmPasswordText.setText("")
                finish()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence("dateOfBirth", dateOfBirthText.text)
        outState.putInt("calendarYear", calendar.get(Calendar.YEAR))
        outState.putInt("calendarMonth", calendar.get(Calendar.MONTH))
        outState.putInt("calendarDayOfMonth", calendar.get(Calendar.DAY_OF_MONTH))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        dateOfBirthText.text = savedInstanceState.getCharSequence("dateOfBirth")
        calendar.set(savedInstanceState.getInt("calendarYear"), savedInstanceState.getInt("calendarMonth"),
            savedInstanceState.getInt("calendarDayOfMonth"))
    }

    private var datePickerDialogListener: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            dateOfBirthText.text = formattedDate(dayOfMonth, month, year)
        }

    private fun formattedDate(dayOfMonth: Int, month: Int, year: Int): String {
        val formattedTime: String = when {
            month < 10 -> {
                if (dayOfMonth < 10) {
                    "0${dayOfMonth}.0${month + 1}.${year}"
                } else {
                    "${dayOfMonth}.0${month + 1}.${year}"
                }
            }
            else -> {
                if (dayOfMonth < 10) {
                    "0${dayOfMonth}.${month + 1}.${year}"
                } else {
                    "${dayOfMonth}.${month + 1}.${year}"
                }
            }
        }

        return formattedTime
    }

    fun newSignUpClick(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        val firstName = firstNameText.text.toString()
        val surname = surnameText.text.toString()
        val dateOfBirth = dateOfBirthText.text.toString()
        val gender = genderText.selectedItem.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()
        val confirmPassword = confirmPasswordText.text.toString()

        if (checkSignUpData(firstName, surname, dateOfBirth, gender, email, password, confirmPassword)) {
            if (checkEmailAddress(email)) {
                if (confirmPassword == password) {
                    if (password.length >= 6) {
                        signUp(firstName, surname, dateOfBirth, gender, email, password, view)
                    } else {
                        val snackbar = Snackbar.make(view, "Error: the password set is too short!", Snackbar.LENGTH_SHORT)
                        val snackbarView = snackbar.view
                        snackbarView.setBackgroundResource(R.drawable.item_shape)
                        snackbar.setTextColor(ResourcesCompat.getColor(resources, R.color.snackbarText, application.theme))
                        val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
                        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        snackbar.show()
                    }
                } else {
                    val snackbar = Snackbar.make(view, "Error: the passwords given are different!", Snackbar.LENGTH_SHORT)
                    val snackbarView = snackbar.view
                    snackbarView.setBackgroundResource(R.drawable.item_shape)
                    snackbar.setTextColor(ResourcesCompat.getColor(resources, R.color.snackbarText, application.theme))
                    val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
                    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    snackbar.show()
                }
            } else {
                val snackbar = Snackbar.make(view, "Error: the specified email address is invalid!", Snackbar.LENGTH_SHORT)
                val snackbarView = snackbar.view
                snackbarView.setBackgroundResource(R.drawable.item_shape)
                snackbar.setTextColor(ResourcesCompat.getColor(resources, R.color.snackbarText, application.theme))
                val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                snackbar.show()
            }
        } else {
            val snackbar = Snackbar.make(view, "Error: not all the fields have been filled in!", Snackbar.LENGTH_SHORT)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundResource(R.drawable.item_shape)
            snackbar.setTextColor(ResourcesCompat.getColor(resources, R.color.snackbarText, application.theme))
            val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            snackbar.show()
        }
    }

    private fun signUp(firstName: String, surname: String, dateOfBirth: String, gender: String, email: String, password: String,
        view: View) {
        authentication.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    firstNameText.setText("")
                    surnameText.setText("")
                    dateOfBirthText.text = ""
                    genderText.setSelection(0)
                    emailText.setText("")
                    passwordText.setText("")
                    confirmPasswordText.setText("")
                    database = FirebaseDatabase.getInstance().reference
                    val userId = authentication.currentUser?.uid!!
                    database.child("users").child(userId).setValue(User(userId, firstName, surname, dateOfBirth, gender,
                        email))
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val snackbar = Snackbar.make(view, "Error: the user with the specified email address already exists!",
                        Snackbar.LENGTH_SHORT)
                    val snackbarView = snackbar.view
                    snackbarView.setBackgroundResource(R.drawable.item_shape)
                    snackbar.setTextColor(ResourcesCompat.getColor(resources, R.color.snackbarText, application.theme))
                    val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
                    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    snackbar.show()
                }
            }
    }

    private fun checkSignUpData(firstName: String, surname: String, dateOfBirth: String, gender: String, email: String,
        password: String, confirmPassword: String): Boolean {
        return !(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(dateOfBirth)
            || TextUtils.isEmpty(gender) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
            || TextUtils.isEmpty(confirmPassword))
    }

    private fun checkEmailAddress(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun dateOfBirthClick(view: View) {
        var date = LocalDate.parse(dateOfBirthText.text, dateFormatter)
        DatePickerDialog(this, datePickerDialogListener, date.year, date.month.value - 1, date.dayOfMonth).show()
    }
}