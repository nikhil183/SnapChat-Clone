package com.example.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChooseUserActivity : AppCompatActivity() {

    private lateinit var lvUsers: ListView
    private var emails = ArrayList<String>()
    private var keys = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        lvUsers = findViewById(R.id.lvUsers)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        lvUsers.adapter = adapter

        FirebaseDatabase.getInstance("https://snapchat-clone-ad495-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child(
            "users"
        ).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newUserEmail = snapshot.child("email").value as String
                emails.add(newUserEmail)
                keys.add(snapshot.key!!)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        lvUsers.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val snap: Map<String, String> = mapOf(
                    "from" to FirebaseAuth.getInstance().currentUser?.email!!,
                    "name" to intent.getStringExtra("name")!!,
                    "url" to intent.getStringExtra("url")!!,
                    "message" to intent.getStringExtra("message")!!
                )
                FirebaseDatabase.getInstance("https://snapchat-clone-ad495-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child(
                    "users"
                ).child(
                    keys[position]
                ).child("snaps").push().setValue(snap)

                val intent = Intent(this, SnapsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
    }
}