package com.example.snapchatclone

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID


class CreateSnapActivity : AppCompatActivity() {

    private lateinit var ivSnap: ImageView
    private lateinit var etMessage: EditText
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private val snapName by lazy {
        UUID.randomUUID().toString() + ".jpeg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        ivSnap = findViewById(R.id.ivSnap)
        etMessage = findViewById(R.id.etMessage)

        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    ivSnap.setImageBitmap(bitmap)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
    }

    fun onChooseSnapClicked(view: View) {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun onNextClicked(view: View) {
        // Get the data from an ImageView as bytes
        ivSnap.isDrawingCacheEnabled = true
        ivSnap.buildDrawingCache()
        val bitmap = (ivSnap.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask =
            FirebaseStorage.getInstance().reference.child("snaps").child(snapName).putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnCompleteListener {
                Log.i("URL", it.result.toString())

                val intent = Intent(this, ChooseUserActivity::class.java)
                intent.putExtra("url", it.result.toString())
                intent.putExtra("name", snapName)
                intent.putExtra("message", etMessage.text.toString())
                startActivity(intent)
            }
        }
    }
}