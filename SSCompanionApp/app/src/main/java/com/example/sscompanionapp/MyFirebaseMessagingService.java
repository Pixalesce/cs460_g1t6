package com.example.sscompanionapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "default_channel_id";
    private static final String CHANNEL_NAME = "Default Channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle FCM messages
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if the message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            showNotification(title, body);
        }
    }

    private void showNotification(String title, String message) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Create NotificationChannel for Android 8.0+ (API level 26 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH // Importance set to High for more visibility
            );
            channel.setDescription("Default Channel for App Notifications");

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Make sure this drawable exists
                .setContentTitle(title != null ? title : "Default Title")
                .setContentText(message != null ? message : "Default Message")
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for visibility
                .setAutoCancel(true); // Automatically dismiss the notification when clicked

        // Check if notifications are enabled before showing
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            Log.w(TAG, "Notification permission is not granted");
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Send token to your server if needed
    }
}