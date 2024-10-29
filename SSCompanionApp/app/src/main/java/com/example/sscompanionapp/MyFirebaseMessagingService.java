package com.example.sscompanionapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "default_channel_id";
    private static final String CHANNEL_NAME = "Default Channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Extract title and body from notification payload, if present
        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : "Notification";
        String body = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "You've received a notification";

        // Extract image URL from data payload, if present
        String imageUrl = remoteMessage.getData().get("image");

        // Show notification with or without image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Bitmap image = getBitmapFromUrl(imageUrl);
            if (image != null) {
                showNotificationWithImage(title, body, image);
            } else {
                showNotification(title, body); // Fallback to text-only if image retrieval fails
            }
        } else {
            showNotification(title, body); // Text-only notification
        }
    }

    private void showNotification(String title, String message) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Create NotificationChannel for Android 8.0+ (API level 26 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Default Channel for App Notifications");
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification without an image
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Make sure this drawable exists
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Check if notifications are enabled before showing
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            Log.w(TAG, "Notification permission is not granted");
        }
    }

    private void showNotificationWithImage(String title, String message, Bitmap image) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Create NotificationChannel for Android 8.0+ (API level 26 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Default Channel for App Notifications");
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification with an image
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Make sure this drawable exists
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image)) // Display the image
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Check if notifications are enabled before showing
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            Log.w(TAG, "Notification permission is not granted");
        }
    }

    private Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            Log.e(TAG, "Error fetching image", e);
            return null;
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Send token to your server if needed
    }
}
