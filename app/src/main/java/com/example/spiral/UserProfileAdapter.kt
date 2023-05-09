package com.example.spiral

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserProfileAdapter(private val data: List<TestUserData>): RecyclerView.Adapter<UserProfileAdapter.ViewHolder>() {
    // private var profilesData = arrayListOf<TestUserData>()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var userName: TextView = view.findViewById(R.id.user_profile_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profile_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = data[position].name
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun filterProfiles(text: String) {
        /* val filteredList = arrayListOf<TestUserData>()

        for (item in data) {
            if (item.user.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }

        data = filteredList
        notifyDataSetChanged() */
    }
}