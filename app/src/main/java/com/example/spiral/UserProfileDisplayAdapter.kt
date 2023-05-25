package com.example.spiral

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class UserProfileDisplayAdapter(private val context: Context, private val data: List<User>):
    RecyclerView.Adapter<UserProfileDisplayAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var usernameTextView: TextView = view.findViewById(R.id.user_profile_display_name)
        var birthdayTextView: TextView = view.findViewById(R.id.user_profile_display_date_of_birth)
        var genderTextView: TextView = view.findViewById(R.id.user_profile_display_gender)
        var editProfilePhotoButton: Button = view.findViewById(R.id.edit_profile_photo_button)
        var editProfileDataButton: Button = view.findViewById(R.id.edit_profile_data_button)
        var sendMessageButton: Button = view.findViewById(R.id.send_message_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profile_display_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.usernameTextView.text = data[position].firstName + " " + data[position].surname

        if (data[position].dateOfBirth.toString() != "") {
            holder.birthdayTextView.text = data[position].dateOfBirth
        } else {
            holder.birthdayTextView.text = "[no-data]"
        }

        if (data[position].gender.toString() != "") {
            holder.genderTextView.text = data[position].gender
        } else {
            holder.genderTextView.text = "[no-data]"
        }

        // Setting proper buttons to visible
        if (data[position].userId == FirebaseAuth.getInstance().currentUser?.uid) {
            holder.editProfilePhotoButton.visibility = View.VISIBLE
            holder.editProfileDataButton.visibility = View.VISIBLE
            holder.sendMessageButton.visibility = View.GONE
        } else {
            holder.editProfilePhotoButton.visibility = View.GONE
            holder.editProfileDataButton.visibility = View.GONE
            holder.sendMessageButton.visibility = View.VISIBLE
        }
        holder.sendMessageButton.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId", data[position].userId)
            intent.putExtra("username", holder.usernameTextView.text.toString())
            (context as Activity).startActivity(intent)
        }
        holder.editProfileDataButton.setOnClickListener {
            val intent = Intent(context, EditProfileDataActivity::class.java)
            context.startActivity(intent)
        }
        holder.editProfilePhotoButton.setOnClickListener {
            val intent = Intent(context, EditProfilePhotoActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}