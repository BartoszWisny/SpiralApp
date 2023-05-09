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
        var userImage: ImageView = view.findViewById(R.id.user_profile_image)
        var userFirstName: TextView = view.findViewById(R.id.user_profile_first_name)
        var userSurname: TextView = view.findViewById(R.id.user_profile_surname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profile_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userImage.setImageResource(R.drawable.user_profile_image)
//        Picasso.get().load(R.drawable.user_profile_image).resize(1000, 1000).centerCrop()
//            .into(holder.userImage)
        holder.userFirstName.text = data[position].firstName
        holder.userSurname.text = data[position].surname
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