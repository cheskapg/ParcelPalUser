package com.example.parcelpaluser;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        // Called when a new token is generated or refreshed
        // Save the token to your server or perform any other required actions

        LoginManager.saveFCMToken(this, token);

        Log.d("FCM Token from Myfire", token);
        // Send the FCM token to the MainActivity using a broadcast
        Intent intent = new Intent("FCM_TOKEN_RECEIVED");
        intent.putExtra("fcmToken", token);
        sendBroadcast(intent);


    }
    public void onTokenRefresh() {
        // Handle token refresh
        String refreshedToken = FirebaseMessaging.getInstance().getToken().getResult();
        Log.d("FCM Token Refreshed", refreshedToken);
        // Store or send the refreshed token to your server
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle the incoming push notification
        // Display it as a system notification using NotificationManager and NotificationCompat.Builder
    }
}