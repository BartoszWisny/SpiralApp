package com.example.spiral

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MessageAdapter(private val context: Context, private val data: List<Message>): RecyclerView.Adapter<ViewHolder>() {
    private lateinit var authentication: FirebaseAuth
    private val itemSent = 1
    private val itemReceived = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == itemSent) {
            val view = LayoutInflater.from(context).inflate(R.layout.chat_message_sent, parent, false)
            MessageSentViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.chat_message_received, parent, false)
            MessageReceivedViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = data[position]

        if (holder.javaClass == MessageSentViewHolder::class.java) {
            holder as MessageSentViewHolder
            holder.messageSent.text = message.message
        } else if (holder.javaClass == MessageReceivedViewHolder::class.java) {
            holder as MessageReceivedViewHolder
            holder.messageReceived.text = message.message
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        authentication = FirebaseAuth.getInstance()
        val message = data[position]
        return if (authentication.currentUser?.uid == message.senderId) itemSent else itemReceived
    }

    class MessageSentViewHolder(itemView: View) : ViewHolder(itemView) {
        val messageSent: TextView = itemView.findViewById(R.id.chat_message_sent)
    }

    class MessageReceivedViewHolder(itemView: View) : ViewHolder(itemView) {
        val messageReceived: TextView = itemView.findViewById(R.id.chat_message_received)
    }
}