package com.example.spiral

import android.app.DatePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class EditProfileDataActivity : AppCompatActivity() {
    private lateinit var editProfileDataLayout: ConstraintLayout
    private lateinit var firstNameText: EditText
    private lateinit var surnameText: EditText
    private lateinit var dateOfBirthText: TextView
    private lateinit var genderText: Spinner
    private var dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private var calendar = Calendar.getInstance()
    private lateinit var authentication: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_data)
        editProfileDataLayout = findViewById(R.id.edit_profile_data_layout)
        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                editProfileDataLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2,
                    applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                editProfileDataLayout.background = ResourcesCompat.getDrawable(resources, R.color.blue_1,
                    applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                editProfileDataLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2,
                    applicationContext.theme)
            }
        }
        firstNameText = findViewById(R.id.edit_profile_data_first_name)
        surnameText = findViewById(R.id.edit_profile_data_surname)
        dateOfBirthText = findViewById(R.id.edit_profile_data_date_of_birth)
        genderText = findViewById(R.id.edit_profile_data_gender)
        val adapter = ArrayAdapter(this, R.layout.spinner, arrayOf(getString(R.string.gender_female), getString(R.string.gender_male), getString(R.string.gender_not_specified)))
        adapter.setDropDownViewResource(R.layout.spinner_item)
        genderText.adapter = adapter
        val selection = when (chat.currentUser.gender) {
            "Male" -> 1
            "Female" -> 0
            else -> 2
        }
        authentication = FirebaseAuth.getInstance()
        genderText.setSelection(selection)
        firstNameText.setText(chat.currentUser.firstName)
        surnameText.setText(chat.currentUser.surname)
        dateOfBirthText.text = if (chat.currentUser.dateOfBirth != "") chat.currentUser.dateOfBirth
            else LocalDate.now().minusYears(18).format(dateFormatter).toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("firstname", firstNameText.text.toString())
        outState.putString("surname", surnameText.text.toString())
        outState.putCharSequence("dateOfBirth", dateOfBirthText.text)
        outState.putInt("calendarYear", calendar.get(Calendar.YEAR))
        outState.putInt("calendarMonth", calendar.get(Calendar.MONTH))
        outState.putInt("calendarDayOfMonth", calendar.get(Calendar.DAY_OF_MONTH))
        outState.putInt("genderSelection", genderText.selectedItemPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        firstNameText.setText(savedInstanceState.getString("firstname"))
        surnameText.setText(savedInstanceState.getString("surname"))
        genderText.setSelection(savedInstanceState.getInt("genderSelection"))
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

    fun dateOfBirthClick(view: View) {
        val date = if (dateOfBirthText.text.toString() != "") {
                LocalDate.parse(dateOfBirthText.text, dateFormatter)
            } else {
                LocalDate.now().minusYears(18)
            }
        DatePickerDialog(this, datePickerDialogListener, date.year, date.month.value - 1, date.dayOfMonth).show()
    }

    fun confirmChangesClick(view: View) {
        val firstName = firstNameText.text.toString()
        val surname = surnameText.text.toString()
        val dateOfBirth = dateOfBirthText.text.toString()
        val gender = when (genderText.selectedItemId.toInt()) {
            0 -> "Female"
            1 -> "Male"
            else -> "I don't wish to specify"
        }
        val email = authentication.currentUser?.email!!
        if (checkData(firstName, surname, dateOfBirth, gender)) {
            if (!compareData(firstName, surname, dateOfBirth, gender)) {
                val userId = authentication.currentUser?.uid!!
                database = FirebaseDatabase.getInstance().reference
                database.child("users").child(userId).setValue(User(userId, firstName, surname, dateOfBirth, gender, email))
                val snackbar = Snackbar.make(view, getString(R.string.edit_profile_data_updated), Snackbar.LENGTH_SHORT)
                snackbar.duration = 2000
                snackbar.addCallback(
                    object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            finish()
                        }
                    }
                )
                val snackbarView = snackbar.view
                snackbarView.setBackgroundResource(R.drawable.item_shape)
                snackbar.setTextColor(ResourcesCompat.getColor(resources, R.color.snackbarText, application.theme))
                val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                snackbar.show()
            }
            else {
                val snackbar = Snackbar.make(view, getString(R.string.edit_profile_data_error_no_change), Snackbar.LENGTH_SHORT)
                val snackbarView = snackbar.view
                snackbarView.setBackgroundResource(R.drawable.item_shape)
                snackbar.setTextColor(ResourcesCompat.getColor(resources, R.color.snackbarText, application.theme))
                val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
                textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                snackbar.show()
            }
        } else {
            val snackbar = Snackbar.make(view, getString(R.string.edit_profile_data_error_empty_fields), Snackbar.LENGTH_SHORT)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundResource(R.drawable.item_shape)
            snackbar.setTextColor(ResourcesCompat.getColor(resources, R.color.snackbarText, application.theme))
            val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            snackbar.show()
        }
    }

    private fun compareData(firstName: String, surname: String, dateOfBirth: String, gender: String): Boolean {
        return (firstName == chat.currentUser.firstName && surname == chat.currentUser.surname && dateOfBirth == chat.currentUser.dateOfBirth
                && gender == chat.currentUser.gender)
    }

    private fun checkData(firstName: String, surname: String, dateOfBirth: String, gender: String): Boolean {
        return !(firstName == "" || surname == "" || dateOfBirth == "" || gender == "")
    }
}