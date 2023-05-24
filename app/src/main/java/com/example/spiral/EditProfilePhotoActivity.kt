package com.example.spiral

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.io.ByteArrayOutputStream

class EditProfilePhotoActivity : AppCompatActivity() {

    private lateinit var userSettingsLayout: ConstraintLayout
    private lateinit var takePhotoButton: Button
    private lateinit var openGalleryButton: Button
    private lateinit var confirmPhotoButton: Button
    private lateinit var cancelPhotoButton: Button
    private lateinit var profilePhotoImageView: ImageView
    private lateinit var profilePhotoBitmap: Bitmap
    private lateinit var photoPath: String
    private var storage = FirebaseStorage.getInstance()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_photo)
        database = FirebaseDatabase.getInstance().reference
        userSettingsLayout = findViewById(R.id.edit_profile_photo_layout)
        profilePhotoImageView = findViewById(R.id.edit_profile_photo_imageview)
        takePhotoButton = findViewById(R.id.edit_profile_photo_camera)
        openGalleryButton = findViewById(R.id.edit_profile_photo_gallery)
        confirmPhotoButton = findViewById(R.id.edit_profile_photo_confirm)
        cancelPhotoButton = findViewById(R.id.edit_profile_photo_cancel)
        photoPath = ""
        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                userSettingsLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                userSettingsLayout.background = ResourcesCompat.getDrawable(resources, R.color.blue_1, applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                userSettingsLayout.background = ResourcesCompat.getDrawable(resources, R.color.gray_2, applicationContext.theme)
            }
        }
        profilePhotoImageView.visibility = View.GONE
        confirmPhotoButton.visibility = View.GONE
        cancelPhotoButton.visibility = View.GONE
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    fun openCamera(view: View) {
        Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show()
    }

    fun openGallery(view: View) {
        val permissionListener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                gallery.launch(intent)
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {}
        }
        TedPermission.create().setScreenOrientation(resources.configuration.orientation)
            .setPermissionListener(permissionListener).setDeniedMessage("Permission denied. If you want to select images from"
                    + " the gallery, grant permission in the settings.").setGotoSettingButtonText("Settings")
            .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE).check()
    }

    private var gallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> if (result.resultCode == Activity.RESULT_OK) {
                profilePhotoBitmap = BitmapFactory.decodeStream(baseContext.contentResolver.openInputStream(result.data?.data!!))
                profilePhotoImageView.setImageBitmap(profilePhotoBitmap)
                photoPath = result.data?.data!!.toString()
                takePhotoButton.visibility = View.GONE
                openGalleryButton.visibility = View.GONE
                confirmPhotoButton.visibility = View.VISIBLE
                cancelPhotoButton.visibility = View.VISIBLE
                profilePhotoImageView.visibility = View.VISIBLE
            }
    }

    fun confirmPhotoClick(view: View) {
        if (photoPath != "") {
            val storageReference = storage.getReferenceFromUrl(chat.storageUrl)
            val byteArrayOutputStream = ByteArrayOutputStream()
            profilePhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val data: ByteArray = byteArrayOutputStream.toByteArray()
            val photoReference = storageReference.child("users").child(chat.currentUser.userId)
            photoReference.putBytes(data).addOnCompleteListener {
                val snackbar = Snackbar.make(view, "Profile photo updated", Snackbar.LENGTH_SHORT)
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
            }.addOnFailureListener {
                val snackbar = Snackbar.make(view, "Unable to upload photo", Snackbar.LENGTH_SHORT)
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
        }
    }

    fun cancelPhotoClick(view: View) {
        photoPath = ""
        profilePhotoImageView.setImageResource(R.drawable.default_user_profile_photo)
        profilePhotoImageView.visibility = View.GONE
        takePhotoButton.visibility = View.VISIBLE
        openGalleryButton.visibility = View.VISIBLE
        confirmPhotoButton.visibility = View.GONE
        cancelPhotoButton.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("photoPath", photoPath)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        photoPath = savedInstanceState.getString("photoPath").toString()
        if (photoPath != "") {
            profilePhotoBitmap = BitmapFactory.decodeStream(baseContext.contentResolver.openInputStream(photoPath.toUri()))
            profilePhotoImageView.setImageBitmap(profilePhotoBitmap)
            takePhotoButton.visibility = View.GONE
            openGalleryButton.visibility = View.GONE
            confirmPhotoButton.visibility = View.VISIBLE
            cancelPhotoButton.visibility = View.VISIBLE
            profilePhotoImageView.visibility = View.VISIBLE
        }
    }

}