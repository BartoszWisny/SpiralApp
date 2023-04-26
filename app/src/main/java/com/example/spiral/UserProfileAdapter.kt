package com.example.spiral

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class UserProfileAdapter(private val data: List<TestUserData>): RecyclerView.Adapter<UserProfileAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var userImage: ImageView = view.findViewById(R.id.user_profile_image)
        var userName: TextView = view.findViewById(R.id.user_profile_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profile_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(R.drawable.user_profile_image).resize(1000, 1000).centerCrop()
            .into(holder.userImage)
        holder.userName.text = data[position].user
    }

    override fun getItemCount(): Int {
        return data.size
    }
}