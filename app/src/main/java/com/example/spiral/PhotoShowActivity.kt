package com.example.spiral

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.storage.FirebaseStorage
import com.ortiz.touchview.TouchImageView
import com.squareup.picasso.Picasso

class PhotoShowActivity : AppCompatActivity() {
    private lateinit var photoShowLayout: ConstraintLayout
    private lateinit var photoShowPhoto: TouchImageView
    private var storage = FirebaseStorage.getInstance()
    private val storageReference = storage.getReferenceFromUrl(chat.storageUrl)
    private var photoType: String? = null
    private var senderRoom: String? = null
    private var photoId: String? = null
    private lateinit var photoBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_show)
        photoShowLayout = findViewById(R.id.photo_show_layout)
        photoShowPhoto = findViewById(R.id.photo_show_photo)
        photoShowPhoto.maxZoom = 5F

        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                photoShowLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_dark_mode,
                    applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                photoShowLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_light_mode,
                    applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                photoShowLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_light_mode,
                    applicationContext.theme)
            }
        }

        photoType = intent.getStringExtra("photoType")
        senderRoom = intent.getStringExtra("senderRoom")
        photoId = intent.getStringExtra("photoId")

        if (photoType == "chat") {
            val photoReference = storageReference.child("chats").child(senderRoom!!).child("photos")
                .child(photoId!!)
            photoReference.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it.toString()).into(photoShowPhoto)
            }.addOnFailureListener {
                photoBitmap = BitmapFactory.decodeResource(resources, R.drawable.default_photo)
                photoShowPhoto.setImageBitmap(photoBitmap)
            }
        } else if (photoType == "profile") {
            val photoReference = storageReference.child("users").child(photoId!!)
            photoReference.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it.toString()).into(photoShowPhoto)
            }.addOnFailureListener {
                photoBitmap = BitmapFactory.decodeResource(resources, R.drawable.default_user_profile_photo)
                photoShowPhoto.setImageBitmap(photoBitmap)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }
}