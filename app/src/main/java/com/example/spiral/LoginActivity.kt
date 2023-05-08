package com.example.spiral

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var loginLayout: ConstraintLayout
    private lateinit var loginAppName: TextView
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var authentication: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleViewHelper: View
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginLayout = findViewById(R.id.login_layout)
        loginAppName = findViewById(R.id.login_app_name)
        emailText = findViewById(R.id.login_email)
        passwordText = findViewById(R.id.login_password)
        authentication = FirebaseAuth.getInstance()
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                loginLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                loginLayout.background = ResourcesCompat.getDrawable(resources, R.color.blue_1, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                loginLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
        }

        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.insomnia)
        loginAppName.typeface = typeface

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                emailText.setText("")
                passwordText.setText("")
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        })
    }

    fun loginEmailClick(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        if (checkLoginData(email, password)) {
            if (checkEmailAddress(email)) {
                login(email, password, view)
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

    private fun login(email: String, password: String, view: View) {
        authentication.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    emailText.setText("")
                    passwordText.setText("")
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val snackbar = Snackbar.make(view, "Error: wrong email address or password!", Snackbar.LENGTH_SHORT)
                    val snackbarView = snackbar.view
                    snackbarView.setBackgroundResource(R.drawable.item_shape)
                    snackbar.setTextColor(ResourcesCompat.getColor(resources, R.color.snackbarText, application.theme))
                    val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
                    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    snackbar.show()
                }
            }
    }

    private fun checkLoginData(email: String, password: String): Boolean {
        return !(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
    }

    private fun checkEmailAddress(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun loginGoogleClick(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        val signInIntent = googleSignInClient.signInIntent
        googleViewHelper = view
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.result

                    if (account != null) {
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        authentication.signInWithCredential(credential).addOnCompleteListener {
                            if (it.isSuccessful) {
                                database = FirebaseDatabase.getInstance().reference
                                val userId = authentication.currentUser?.uid!!
                                // TODO
//                                database.child("users").child(userId).setValue(User(userId, firstName, surname, dateOfBirth, gender,
//                                    email))
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                val snackbar = Snackbar.make(googleViewHelper, "Error: Google login failed!",
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
                } else {
                    val snackbar = Snackbar.make(googleViewHelper, "Error: Google login failed!", Snackbar.LENGTH_SHORT)
                    val snackbarView = snackbar.view
                    snackbarView.setBackgroundResource(R.drawable.item_shape)
                    snackbar.setTextColor(ResourcesCompat.getColor(resources, R.color.snackbarText, application.theme))
                    val textView: TextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text)
                    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    snackbar.show()
                }
            }
    }

    fun signUpClick(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}