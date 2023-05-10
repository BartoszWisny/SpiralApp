package com.example.spiral

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserChatsAdapter(private val data: List<TestMessage>): RecyclerView.Adapter<UserChatsAdapter.ViewHolder>() {
    private var chatsData = data

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
//        var userChatsListItemLayout: ConstraintLayout = view.findViewById(R.id.user_chats_list_item_layout)
        var userImage: ImageView = view.findViewById(R.id.user_chats_image)
        var userName: TextView = view.findViewById(R.id.user_chats_name)
        var userLastMessage: TextView = view.findViewById(R.id.user_chats_last_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_chats_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userImage.setImageResource(R.drawable.default_user_profile_photo)
//        Picasso.get().load(R.drawable.spiral_logo).resize(1000, 1000).centerCrop()
//            .transform(RoundedCornersTransformation(500, 20)).into(holder.userImage)
        holder.userName.text = chatsData[position].user
        holder.userLastMessage.text = chatsData[position].text

//        if (position == 0) {
//            holder.userChatsListItemLayout.updatePadding(0, 50, 0, 0)
//        }
//
//        if (position == data.size - 1) {
//            holder.userChatsListItemLayout.updatePadding(0, 0, 0, 50)
//        }
    }

    override fun getItemCount(): Int {
        return chatsData.size
    }

    fun filterChats(text: String) {
        val filteredList = arrayListOf<TestMessage>()

        for (item in data) {
            if (item.user.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }

        chatsData = filteredList
        notifyDataSetChanged()
    }
}