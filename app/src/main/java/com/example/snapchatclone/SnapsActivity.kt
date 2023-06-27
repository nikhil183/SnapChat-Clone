package com.example.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SnapsActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private lateinit var lvSnaps: ListView
    private var emails = ArrayList<String>()
    private var snaps = ArrayList<DataSnapshot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        lvSnaps = findViewById(R.id.lvSnaps)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        lvSnaps.adapter = adapter

        FirebaseDatabase.getInstance("https://snapchat-clone-ad495-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child(
            "users"
        ).child(auth.currentUser?.uid!!).child("snaps")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    emails.add(snapshot.child("from").value as String)
                    snaps.add(snapshot)
                    adapter.notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    var index = 0
                    for (snap in snaps) {
                        if (snap.key == snapshot.key) {
                            snaps.removeAt(index)
                            emails.removeAt(index)
                        }
                        index+=1
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })

        lvSnaps.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val snapshot =  snaps[position]

            val intent = Intent(this, ViewSnapActivity::class.java)
            intent.putExtra("name", snapshot.child("name").value as String)
            intent.putExtra("url", snapshot.child("url").value as String)
            intent.putExtra("message", snapshot.child("message").value as String)
            intent.putExtra("key", snapshot.key)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.snaps, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.createSnap -> {
                startActivity(Intent(this, CreateSnapActivity::class.java))
            }

            R.id.logout -> {
                auth.signOut()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
    }
}