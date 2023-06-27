package com.example.snapchatclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ViewSnapActivity : AppCompatActivity() {

    private lateinit var tvMessage: TextView
    private lateinit var ivSnap: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        tvMessage = findViewById(R.id.tvMessage)
        ivSnap = findViewById(R.id.ivSnap)

        tvMessage.text = intent.getStringExtra("message")

        Glide.with(this)
            .load(intent.getStringExtra("url"))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(ivSnap)

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                FirebaseDatabase.getInstance("https://snapchat-clone-ad495-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child(
                    "users"
                ).child(Firebase.auth.currentUser?.uid!!).child("snaps")
                    .child(intent.getStringExtra("key")!!).removeValue()

                FirebaseStorage.getInstance().reference.child("snaps").child(intent.getStringExtra("name")!!).delete()

                finish()
            }
        })
    }
}