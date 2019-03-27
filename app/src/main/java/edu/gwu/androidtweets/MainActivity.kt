package edu.gwu.androidtweets

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import android.content.Intent
import android.support.v7.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    // lateinit means we will assign it later
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var signUp: Button
    private lateinit var progressBar : ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth

    // TextWatcher is an interface
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //.text shorthand for getText()
            val inputtedUsername: String = username.text.toString().trim()
            val inputtedPassword: String = password.text.toString().trim()
            val enableButton: Boolean = inputtedUsername.isNotEmpty() && inputtedPassword.isNotEmpty()

            login.isEnabled = enableButton
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()

        // Pass the name and the file create mode
        val sharedPrefs = getSharedPreferences("user_settings", Context.MODE_PRIVATE)

        Log.d("MainActivity", "onCreate called")
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        signUp = findViewById(R.id.signUp)
        progressBar = findViewById(R.id.progressBar)

        username.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

        username.setText(sharedPrefs.getString("username", ""))

        // curly braces shorthand for interface with only one method
        login.setOnClickListener {
            Log.d("MainActivity", "Login clicked!")
            progressBar.visibility = View.VISIBLE

            val inputtedUsername: String = username.text.toString().trim()
            val inputtedPassword: String = password.text.toString().trim()

            firebaseAuth.signInWithEmailAndPassword(
                inputtedUsername,
                inputtedPassword
            ).addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    currentUser!!.sendEmailVerification()
                    Toast.makeText(this, "Logged in: ${currentUser!!.email}", Toast.LENGTH_LONG).show()
                } else {
                    val exception = task.exception
                    Toast.makeText(this, "Failed to login: $exception", Toast.LENGTH_LONG).show()
                }
            }

//            val intent: Intent = Intent(this, TweetsActivity::class.java)
//            intent.putExtra("location", "Washington, D.C.")
            val intent: Intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)

            // Short message that pops near bottom of screen
            val toast = Toast.makeText(this, "This is a Toast message", Toast.LENGTH_SHORT)
            toast.show()

            AlertDialog.Builder(this)
                .setTitle("Sorry")
                .setMessage("Could not find any tweets")
                .setPositiveButton("OK") { dialog, which ->

                }
                .show()

            // Writing to preferences (make sure you call apply)
            sharedPrefs.edit().putString("username", username.text.toString()).apply()

//            // Using ACTION_SEND to send plain text content
//            val sendIntent = Intent()
//            intent.action = Intent.ACTION_SEND
//            sendIntent.type = "text/plain"
//            intent.putExtra(Intent.EXTRA_TEXT, "Android Tweets is a great app!")
//            startActivity(sendIntent)

        }

        signUp.setOnClickListener {
            val inputtedUsername: String = username.text.toString().trim()
            val inputtedPassword: String = password.text.toString().trim()

            firebaseAuth.createUserWithEmailAndPassword(
                inputtedUsername,
                inputtedPassword
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // If sign up is succesfful, Firebase automatically logs in as that user too
                    // (e.g. currentUser is set)
                    val currentUser = firebaseAuth.currentUser
                    currentUser!!.sendEmailVerification()
                    Toast.makeText(this, "Created user: ${currentUser!!.email}", Toast.LENGTH_LONG).show()
                } else {
                    val exception = task.exception
                    Toast.makeText(this, "Failed to register: $exception", Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart called")
    }

    override fun onResume() {
        super.onStart()
        Log.d("MainActivity", "onResume called")
    }

    override fun onPause() {
        super.onStart()
        Log.d("MainActivity", "onPause called")
    }

    override fun onStop() {
        super.onStart()
        Log.d("MainActivity", "onStop called")
    }

    override fun onDestroy() {
        super.onStart()
        Log.d("MainActivity", "onDestroy called")
    }
}

