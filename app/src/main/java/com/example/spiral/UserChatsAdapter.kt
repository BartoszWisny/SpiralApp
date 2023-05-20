package com.example.spiral

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class UserChatsAdapter(private val context: Context, private val data: List<User>):
    RecyclerView.Adapter<UserChatsAdapter.ViewHolder>() {
    private var chatsData = data
    private var storage = FirebaseStorage.getInstance()
    private lateinit var userPhotoBitmap: Bitmap
    private var authentication = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance().reference

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var userPhoto: ImageView = view.findViewById(R.id.user_chats_photo)
        var username: TextView = view.findViewById(R.id.user_chats_name)
        var userLastMessage: TextView = view.findViewById(R.id.user_chats_last_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_chats_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storageReference = storage.getReferenceFromUrl("gs://spiralapp-828a8.appspot.com")
        val photoReference = storageReference.child("users").child(chatsData[position].userId)
        photoReference.getBytes(10 * 1024 * 1024).addOnSuccessListener {
            userPhotoBitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.userPhoto.setImageBitmap(userPhotoBitmap)
        }.addOnFailureListener {
            userPhotoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_user_profile_photo)
            holder.userPhoto.setImageBitmap(userPhotoBitmap)
        }
        val name = "${chatsData[position].firstName} ${chatsData[position].surname}"
        holder.username.text = name
        val senderRoom = authentication.currentUser?.uid + chatsData[position].userId
        database.child("chats").child(senderRoom).child("messages").addValueEventListener(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val message = snapshot.children.last().getValue(Message::class.java)
                        val sender = if (message?.senderId == authentication.currentUser?.uid) "You: " else
                            chatsData[position].firstName + ": "
                        holder.userLastMessage.text = sender + message?.message
                    } else {
                        holder.userLastMessage.text = "Start a conversation"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId", chatsData[position].userId)
            intent.putExtra("username", name)
            context.startActivity(intent)
        }
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int {
        return chatsData.size
    }

    fun filterChats(text: String) {
        val filteredList = arrayListOf<User>()

        for (item in data) {
            val name = "${item.firstName} ${item.surname}"

            if (name.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }

        chatsData = filteredList
        notifyDataSetChanged()
    }
}