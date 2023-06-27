package com.example.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            logIn()
        }
    }

    fun loginClicked(view: View) {
        auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    logIn()
                } else {
                    signUp()
                }
            }
    }

    private fun logIn() {
        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)
    }

    private fun signUp() {
        auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(task.result)
                    logIn()
                } else {
                    Toast.makeText(
                        this,
                        "Login failed. Try Again.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    Log.i("error>>", task.exception.toString())
                }
            }
    }

    private fun addUserToDatabase(result: AuthResult) {
        FirebaseDatabase.getInstance("https://snapchat-clone-ad495-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child(
            "users"
        ).child(result.user?.uid!!)
            .child("email").setValue(etEmail.text.toString())
    }
}