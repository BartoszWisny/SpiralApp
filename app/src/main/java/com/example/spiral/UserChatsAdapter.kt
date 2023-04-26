package com.example.spiral

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

class UserChatsAdapter(private val data: List<TestMessage>): RecyclerView.Adapter<UserChatsAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var userChatsListItemLayout: ConstraintLayout = view.findViewById(R.id.user_chats_list_item_layout)
        var userImage: ImageView = view.findViewById(R.id.user_chats_image)
        var userName: TextView = view.findViewById(R.id.user_chats_name)
        var userLastMessage: TextView = view.findViewById(R.id.user_chats_last_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_chats_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userImage.setImageResource(R.drawable.user_profile_image)
//        Picasso.get().load(R.drawable.spiral_logo).resize(1000, 1000).centerCrop()
//            .transform(RoundedCornersTransformation(500, 20)).into(holder.userImage)
        holder.userName.text = data[position].user
        holder.userLastMessage.text = data[position].text

        if (position == 0) {
            holder.userChatsListItemLayout.updatePadding(0, 50, 0, 0)
        }

        if (position == data.size - 1) {
            holder.userChatsListItemLayout.updatePadding(0, 0, 0, 50)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}