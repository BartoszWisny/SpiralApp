package com.example.spiral

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage

class UserProfilesAdapter(private val context: Context, private val data: List<User>):
    RecyclerView.Adapter<UserProfilesAdapter.ViewHolder>() {
    private var profilesData = data
    private var storage = FirebaseStorage.getInstance()
    private lateinit var userPhotoBitmap: Bitmap

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var userImage: ImageView = view.findViewById(R.id.user_profiles_photo)
        var username: TextView = view.findViewById(R.id.user_profiles_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_profiles_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storageReference = storage.getReferenceFromUrl("gs://spiralapp-828a8.appspot.com")
        val photoReference =
            storageReference.child("users").child(profilesData[position].userId)

        photoReference.getBytes(10 * 1024 * 1024).addOnSuccessListener {
            userPhotoBitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.userImage.setImageBitmap(userPhotoBitmap)
        }.addOnFailureListener {
            userPhotoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.default_user_profile_photo)
            holder.userImage.setImageBitmap(userPhotoBitmap)
        }

        val name = "${profilesData[position].firstName} ${profilesData[position].surname}"
        holder.username.text = name
        holder.itemView.setOnClickListener {
            Toast.makeText(context, "${name}'s profile clicked", Toast.LENGTH_SHORT).show()
        }

//        holder.userImage.setImageResource(R.drawable.default_user_profile_photo)
//        Picasso.get().load(R.drawable.user_profile_image).resize(1000, 1000).centerCrop()
//            .transform(RoundedCornersTransformation(50, 0)).into(holder.userImage)
//        holder.username.text = profilesData[position].user
    }

    override fun getItemCount(): Int {
        return profilesData.size
    }

    fun filterProfiles(text: String) {
        val filteredList = arrayListOf<User>()

        for (item in data) {
            val name = "${item.firstName} ${item.surname}"
            if (name.lowercase().contains(text.lowercase())) {
                filteredList.add(item)
            }
        }

        profilesData = filteredList
        notifyDataSetChanged()
    }
}