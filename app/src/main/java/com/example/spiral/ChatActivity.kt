package com.example.spiral

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class ChatActivity : AppCompatActivity() {
    private lateinit var chatLayout: ConstraintLayout
    private lateinit var chatPhoto: ImageView
    private lateinit var chatUsername: TextView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageTextView: EditText
    private lateinit var userPhoto: Bitmap
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
        val storageReference = storage.getReferenceFromUrl("gs://spiralapp-828a8.appspot.com")
        val userId = intent.getStringExtra("userId")!!
        val photoReference = storageReference.child("users").child(userId)
        photoReference.getBytes(10 * 1024 * 1024).addOnSuccessListener {
            userPhoto = BitmapFactory.decodeByteArray(it, 0, it.size)
            chatPhoto.setImageBitmap(userPhoto)
        }.addOnFailureListener {}
        chatUsername.text = intent.getStringExtra("username")!!
        messageAdapter = MessageAdapter(this, messagesList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter
        senderRoom = authentication.currentUser?.uid + userId
        receiverRoom = userId + authentication.currentUser?.uid

        database.child("chats").child(senderRoom!!).child("messages").addValueEventListener(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messagesList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messagesList.add(message!!)
                    }

                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun chatSendMessageClick(view: View) {
        val messageText = messageTextView.text.toString()
        val message = Message(authentication.currentUser?.uid, "text", messageText)
        database.child("chats").child(senderRoom!!).child("messages").push().setValue(message)
            .addOnSuccessListener {
                database.child("chats").child(receiverRoom!!).child("messages").push().setValue(message)
            }.addOnFailureListener {}
        messageTextView.setText("")
    }

    fun chatAttachmentClick(view: View) {
        // TODO
    }
}