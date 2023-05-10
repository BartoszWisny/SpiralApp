package com.example.spiral

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserProfileDisplayAdapter(private val data: List<TestUserData>): RecyclerView.Adapter<UserProfileDisplayAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var username: TextView = view.findViewById(R.id.user_profile_display_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profile_display_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username.text = data[position].name
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun filterProfiles(text: String) {
        // TODO
    }
}