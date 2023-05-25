package com.example.spiral

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import androidx.core.app.NotificationCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class Chat {
    var tabAdapter: MainActivity.TabAdapter? = null
    var viewPager: ViewPager2? = null
    var usersList = arrayListOf<User>()
    var messagesList = arrayListOf<Message>()
    val storageUrl = "gs://spiralapp-828a8.appspot.com"
    lateinit var currentUser: User
    var roomSelected: String? = null
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.getReferenceFromUrl(storageUrl)
    private var authentication = FirebaseAuth.getInstance()

    fun showNotification(context: Context, id: Int, title: String?, content: String?, sender: String?,
        room_id: String?, intent: Intent?) {
        var pendingIntent: PendingIntent? = null

        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT
                or PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationChannelId = "com.example.spiral"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
        val notificationChannel = NotificationChannel(notificationChannelId, "Spiral", NotificationManager
            .IMPORTANCE_HIGH)
        notificationChannel.description = "Spiral"
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
        notificationChannel.enableVibration(true)
        (notificationManager as NotificationManager).createNotificationChannel(notificationChannel)
        val notificationBuilder = NotificationCompat.Builder(context, notificationChannelId)
        val photoReference = storageReference.child("users").child(sender!!)
        photoReference.getBytes(5 * 1024 * 1024).addOnSuccessListener {
            val photo = getSquaredBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            val photoByteArrayOutputStream = ByteArrayOutputStream()
            photo?.compress(CompressFormat.JPEG, 100, photoByteArrayOutputStream)
            val photoByteArray = photoByteArrayOutputStream.toByteArray()
            notificationBuilder.setContentTitle(title).setContentText(content).setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round).setLargeIcon(BitmapFactory.decodeByteArray(photoByteArray, 0,
                photoByteArray.size))

            if (pendingIntent != null) {
                notificationBuilder.setContentIntent(pendingIntent)
            }

            val notification = notificationBuilder.build()

            if (!authentication.currentUser?.uid.equals(sender)) {
                notificationManager.notify(id, notification)
            }
        }.addOnFailureListener {
            val photo = getSquaredBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.default_user_profile_photo))
            val photoByteArrayOutputStream = ByteArrayOutputStream()
            photo?.compress(CompressFormat.JPEG, 100, photoByteArrayOutputStream)
            val photoByteArray = photoByteArrayOutputStream.toByteArray()
            notificationBuilder.setContentTitle(title).setContentText(content).setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round).setLargeIcon(BitmapFactory.decodeByteArray(photoByteArray, 0,
                photoByteArray.size))

            if (pendingIntent != null) {
                notificationBuilder.setContentIntent(pendingIntent)
            }

            val notification = notificationBuilder.build()

            if (!authentication.currentUser?.uid.equals(sender)) {
                notificationManager.notify(id, notification)
            }
        }
    }

    private fun getSquaredBitmap(bitmap: Bitmap): Bitmap? {
        val dim = bitmap.width.coerceAtMost(bitmap.height)
        val dstBmp = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dstBmp)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, (dim - bitmap.width) / 2F, (dim - bitmap.height) / 2F, null)
        return dstBmp
    }
}