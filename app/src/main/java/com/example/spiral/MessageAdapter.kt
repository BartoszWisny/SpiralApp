package com.example.spiral

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class MessageAdapter(private val context: Context, private val data: List<Message>, private val senderRoom: String):
    RecyclerView.Adapter<ViewHolder>() {
    private lateinit var authentication: FirebaseAuth
    private var storage = FirebaseStorage.getInstance()
    private val storageReference = storage.getReferenceFromUrl(chat.storageUrl)
    private val messageSent = 1
    private val messageReceived = 2
    private val photoSent = 3
    private val photoReceived = 4
    private lateinit var photoBitmap: Bitmap

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            messageSent -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_message_sent, parent, false)
                MessageSentViewHolder(view)
            }
            messageReceived -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_message_received, parent, false)
                MessageReceivedViewHolder(view)
            }
            photoSent -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_photo_sent, parent, false)
                PhotoSentViewHolder(view)
            }
            photoReceived -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_photo_received, parent, false)
                PhotoReceivedViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.chat_message_sent, parent, false)
                MessageSentViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = data[position]

        when (holder.javaClass) {
            MessageSentViewHolder::class.java -> {
                holder as MessageSentViewHolder
                holder.messageSent.text = message.message
                holder.setIsRecyclable(false)
            }
            MessageReceivedViewHolder::class.java -> {
                holder as MessageReceivedViewHolder
                holder.messageReceived.text = message.message
                holder.setIsRecyclable(false)
            }
            PhotoSentViewHolder::class.java -> {
                holder as PhotoSentViewHolder
                val photoId = message.message
                val photoReference = storageReference.child("chats").child(senderRoom).child("photos")
                    .child(photoId)
                photoReference.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it.toString()).into(holder.photoSent)
                }.addOnFailureListener {
                    photoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_photo)
                    holder.photoSent.setImageBitmap(photoBitmap)
                }
            }
            PhotoReceivedViewHolder::class.java -> {
                holder as PhotoReceivedViewHolder
                val photoId = message.message
                val photoReference = storageReference.child("chats").child(senderRoom).child("photos")
                    .child(photoId)
                photoReference.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it.toString()).into(holder.photoReceived)
                }.addOnFailureListener {
                    photoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_photo)
                    holder.photoReceived.setImageBitmap(photoBitmap)
                }
            }
            else -> {
                holder as MessageSentViewHolder
                holder.messageSent.text = message.message
                holder.setIsRecyclable(false)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        authentication = FirebaseAuth.getInstance()
        val message = data[position]
        return if (message.type == "text") {
            if (authentication.currentUser?.uid == message.senderId) messageSent else messageReceived
        } else if (message.type == "photo") {
            if (authentication.currentUser?.uid == message.senderId) photoSent else photoReceived
        } else {
            0
        }
    }

    class MessageSentViewHolder(itemView: View) : ViewHolder(itemView) {
        val messageSent: TextView = itemView.findViewById(R.id.chat_message_sent)
    }

    class MessageReceivedViewHolder(itemView: View) : ViewHolder(itemView) {
        val messageReceived: TextView = itemView.findViewById(R.id.chat_message_received)
    }

    class PhotoSentViewHolder(itemView: View) : ViewHolder(itemView) {
        val photoSent: ImageView = itemView.findViewById(R.id.chat_photo_sent)
    }

    class PhotoReceivedViewHolder(itemView: View) : ViewHolder(itemView) {
        val photoReceived: ImageView = itemView.findViewById(R.id.chat_photo_received)
    }
}