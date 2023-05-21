package com.example.spiral

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

class ChatActivity : AppCompatActivity() {
    private lateinit var chatLayout: ConstraintLayout
    private lateinit var chatPhoto: ImageView
    private lateinit var chatUsername: TextView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageTextView: EditText
    private lateinit var bottomMessageBar: LinearLayout
    private lateinit var bottomAttachmentBar: LinearLayout
    private lateinit var bottomPhotoBar: LinearLayout
    private lateinit var photoMessageImageView: ImageView
    private lateinit var chatListLayout: ConstraintLayout
    private lateinit var userPhoto: Bitmap
    private lateinit var photoMessageBitmap: Bitmap
    private var photoPath: String? = null
    private var bottomMessageBarVisibility = true
    private var bottomAttachmentBarVisibility = false
    private var bottomPhotoBarVisibility = false
    private lateinit var messageAdapter: MessageAdapter
    private var messagesList = arrayListOf<Message>()
    private lateinit var authentication: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var storage = FirebaseStorage.getInstance()
    private var senderRoom: String? = null
    private var receiverRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        authentication = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        chatLayout = findViewById(R.id.chat_layout)

        when (applicationContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                chatLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_dark_mode,
                    applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                chatLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_light_mode,
                    applicationContext.theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                chatLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.background_light_mode,
                    applicationContext.theme)
            }
        }

        chatPhoto = findViewById(R.id.chat_photo)
        chatUsername = findViewById(R.id.chat_username)
        chatRecyclerView = findViewById(R.id.chat_list)
        messageTextView = findViewById(R.id.chat_type_message)
        bottomMessageBar = findViewById(R.id.bottom_message_bar)
        bottomAttachmentBar = findViewById(R.id.bottom_attachment_bar)
        bottomPhotoBar = findViewById(R.id.bottom_photo_bar)
        photoMessageImageView = findViewById(R.id.chat_photo_message)
        chatListLayout = findViewById(R.id.chat_list_layout)
        val storageReference = storage.getReferenceFromUrl(chat.storageUrl)
        val userId = intent.getStringExtra("userId")!!
        val photoReference = storageReference.child("users").child(userId)
        photoReference.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it.toString()).placeholder(R.drawable.default_user_profile_photo).into(chatPhoto)
        }.addOnFailureListener {
            userPhoto = BitmapFactory.decodeResource(resources, R.drawable.default_user_profile_photo)
            chatPhoto.setImageBitmap(userPhoto)
        }
        chatUsername.text = intent.getStringExtra("username")!!
        senderRoom = authentication.currentUser?.uid + userId
        receiverRoom = userId + authentication.currentUser?.uid
        messageAdapter = MessageAdapter(this, messagesList, senderRoom!!)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter
        chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        database.child("chats").child(senderRoom!!).child("messages").addValueEventListener(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messagesList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messagesList.add(message!!)
                    }

                    chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        chatRecyclerView.setHasFixedSize(true)
        chatRecyclerView.setItemViewCacheSize(20)
        chatRecyclerView.addOnLayoutChangeListener {
            _: View, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int ->
                chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val windowMetrics = windowManager.currentWindowMetrics
            val height = (0.7 * windowMetrics.bounds.height()).toInt()
            bottomPhotoBar.layoutParams.height = height
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("bottomMessageBarVisibility", bottomMessageBar.isVisible)
        outState.putBoolean("bottomAttachmentBarVisibility", bottomAttachmentBar.isVisible)
        outState.putBoolean("bottomPhotoBarVisibility", bottomPhotoBar.isVisible)
        outState.putString("photoPath", photoPath)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomMessageBarVisibility = savedInstanceState.getBoolean("bottomMessageBarVisibility")
        bottomAttachmentBarVisibility = savedInstanceState.getBoolean("bottomAttachmentBarVisibility")
        bottomPhotoBarVisibility = savedInstanceState.getBoolean("bottomPhotoBarVisibility")
        bottomMessageBar.visibility = if (bottomMessageBarVisibility) View.VISIBLE else View.INVISIBLE
        bottomAttachmentBar.visibility = if (bottomAttachmentBarVisibility) View.VISIBLE else View.INVISIBLE
        bottomPhotoBar.visibility = if (bottomPhotoBarVisibility) View.VISIBLE else View.INVISIBLE

        if (bottomPhotoBarVisibility) {
            chatListLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToTop = bottomPhotoBar.id
            }
        }

        photoPath = savedInstanceState.getString("photoPath")

        if (photoPath != null) {
            photoMessageBitmap = BitmapFactory.decodeStream(baseContext.contentResolver.openInputStream(Uri.parse(photoPath)))
            photoMessageImageView.setImageBitmap(photoMessageBitmap)
        }
    }

    fun chatSendMessageClick(view: View) {
        val messageText = messageTextView.text.toString()

        if (messageText != "") {
            val message = Message(authentication.currentUser?.uid, "text", messageText)
            database.child("chats").child(senderRoom!!).child("messages").push().setValue(message)
                .addOnSuccessListener {
                    database.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(message)
                }.addOnFailureListener {}
            messageTextView.setText("")
        }
    }

    fun chatAttachmentClick(view: View?) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        bottomMessageBar.visibility = View.INVISIBLE
        bottomAttachmentBar.visibility = View.VISIBLE
        bottomPhotoBar.visibility = View.INVISIBLE
        messageTextView.setText("")
    }

    fun chatGalleryClick(view: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.launch(intent)
    }

    private var gallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> if (result.resultCode == Activity.RESULT_OK) {
            chatListLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToTop = bottomPhotoBar.id
            }
            bottomMessageBar.visibility = View.INVISIBLE
            bottomAttachmentBar.visibility = View.INVISIBLE
            bottomPhotoBar.visibility = View.VISIBLE
            photoPath = result.data?.data!!.toString()
            photoMessageBitmap = BitmapFactory.decodeStream(baseContext.contentResolver.openInputStream(result.data?.data!!))
            photoMessageImageView.setImageBitmap(photoMessageBitmap)
        }
    }

    fun chatTakePhotoClick(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/photo.jpg"
        val cameraFile = photoPath?.let { File(it) }
        val fileUri = cameraFile?.let { FileProvider.getUriForFile(applicationContext,
            "com.example.spiral.file_provider", it) }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        camera.launch(intent)
    }

    private var camera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result -> if (result.resultCode == Activity.RESULT_OK) {
            chatListLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToTop = bottomPhotoBar.id
            }
            bottomMessageBar.visibility = View.INVISIBLE
            bottomAttachmentBar.visibility = View.INVISIBLE
            bottomPhotoBar.visibility = View.VISIBLE
            photoPath = "file://$photoPath"
            photoMessageBitmap = BitmapFactory.decodeStream(baseContext.contentResolver.openInputStream(
                Uri.parse(photoPath)))
            photoMessageImageView.setImageBitmap(photoMessageBitmap)
        }
    }

    fun chatVoiceClick(view: View) {

    }

    fun closeClick(view: View) {
        bottomMessageBar.visibility = View.VISIBLE
        bottomAttachmentBar.visibility = View.INVISIBLE
        bottomPhotoBar.visibility = View.INVISIBLE
    }

    fun photoCloseClick(view: View) {
        chatListLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
            bottomToTop = bottomMessageBar.id
        }
        bottomMessageBar.visibility = View.VISIBLE
        bottomAttachmentBar.visibility = View.INVISIBLE
        bottomPhotoBar.visibility = View.INVISIBLE
        photoMessageBitmap = BitmapFactory.decodeResource(resources, R.drawable.default_photo)
        photoMessageImageView.setImageBitmap(photoMessageBitmap)
    }

    fun chatSendPhotoClick(view: View) {
        val storageReference = storage.getReferenceFromUrl(chat.storageUrl)
        val byteArrayOutputStream = ByteArrayOutputStream()
        photoMessageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val data: ByteArray = byteArrayOutputStream.toByteArray()
        val hash = MessageDigest.getInstance("SHA-256").digest(data)
        val photoId = BigInteger(1, hash).toString(16)
        val photoReferenceSender = storageReference.child("chats").child(senderRoom!!).child("photos")
            .child(photoId)
        val photoReferenceReceiver = storageReference.child("chats").child(receiverRoom!!).child("photos")
            .child(photoId)
        val message = Message(authentication.currentUser?.uid, "photo", photoId)
        photoReferenceSender.putBytes(data).addOnCompleteListener {
            database.child("chats").child(senderRoom!!).child("messages").push().setValue(message)
        }
        photoReferenceReceiver.putBytes(data).addOnCompleteListener {
            database.child("chats").child(receiverRoom!!).child("messages").push().setValue(message)
        }
        chatListLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
            bottomToTop = bottomMessageBar.id
        }
        bottomMessageBar.visibility = View.VISIBLE
        bottomAttachmentBar.visibility = View.INVISIBLE
        bottomPhotoBar.visibility = View.INVISIBLE
        photoMessageBitmap = BitmapFactory.decodeResource(resources, R.drawable.default_photo)
        photoMessageImageView.setImageBitmap(photoMessageBitmap)
    }
}