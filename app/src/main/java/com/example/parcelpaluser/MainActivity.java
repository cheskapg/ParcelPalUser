package com.example.parcelpaluser;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.example.parcelpaluser.databinding.ActivityMainBinding;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class MainActivity extends AppCompatActivity {
    private parcelListAdapter mAdapter;
//    private MyBroadcastReceiver broadcastReceiver;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
//DEFINE BUTTONS ETC
    ;
    EditText etName;
    Button btnSubmit, btnGetParcelList, btnAddParcel, btnScanQr, btnUserHome, btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        String fcmToken=LoginManager.getFCMToken(this);

//        showForegroundNotification(this, "New Message", "You have a new message!", fcmToken);

        if (LoginManager.isLoggedIn(this)) {
            // User is logged in, proceed to the main screen
            Intent intent = new Intent(this, UserMainHome.class);
            startActivity(intent);
            finish();

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        //Declare Buttons etc.
        etName = findViewById(R.id.etName);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnGetParcelList = findViewById(R.id.btnGetParcelList);
        btnAddParcel = findViewById(R.id.btnAddParcel);
        btnScanQr = findViewById(R.id.btnScanQr);
        btnUserHome = findViewById(R.id.btnUserHome);
        btnLogin = findViewById(R.id.btnLogin);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserData();
            }
        });

        //Submit Button Click
        btnAddParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addParcelActivity();
            }
            public  void addParcelActivity(){
                Intent movetoAddParcel = new Intent(MainActivity.this, addParcelItem.class);
                startActivity(movetoAddParcel);
            }
        });
        btnUserHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addParcelActivity();
            }
            public  void addParcelActivity(){
                Intent movetoUserHome = new Intent(MainActivity.this, UserMainHome.class);
                startActivity(movetoUserHome);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addParcelActivity();
            }
            public  void addParcelActivity(){
                Intent moveToLogin = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(moveToLogin);
            }
        });
        //GetParcel Button CLICK
        btnGetParcelList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParcelListActivity();
            }
            public  void ParcelListActivity(){
                Intent movetoParcelListActivity = new Intent(MainActivity.this, parcelList.class);
                startActivity(movetoParcelListActivity);
            }
        });
        btnScanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScannerActivity();
            }
            public  void ScannerActivity(){
                Intent movetoScanner = new Intent(MainActivity.this, ScanQrActivity.class);
                startActivity(movetoScanner);
            }
        });

//
//        broadcastReceiver = new MyBroadcastReceiver();
//        IntentFilter intentFilter = new IntentFilter("FCM_TOKEN_RECEIVED");
//        registerReceiver(broadcastReceiver, intentFilter);
    }
//    public void showForegroundNotification(Context context, String title, String message, String token) {
//        RemoteMessage.Builder builder = new RemoteMessage.Builder(token);
//        builder.setMessageId(Integer.toString((int) (Math.random() * 10000)))
//                .addData("title", title)
//                .addData("message", message);
//
//        FirebaseMessaging.getInstance().send(builder.build());
//
//        // Create a notification channel for Android Oreo and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelName = "Foreground Notification Channel";
//            String channelId = "foreground_notification_channel";
//            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        // Create the notification
//        Notification.Builder notificationBuilder = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            notificationBuilder = new Notification.Builder(context, "foreground_notification_channel")
//                    .setContentTitle(title)
//                    .setContentText(message)
//                    .setSmallIcon(R.drawable.notification_icon);
//        }
//
//        // Display the notification
//        NotificationManager notificationManager = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            notificationManager = context.getSystemService(NotificationManager.class);
//        }
//        notificationManager.notify(0, notificationBuilder.build());
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Unregister the broadcast receiver
//        unregisterReceiver(broadcastReceiver);
//    }
//    public class MyBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String fcmToken = intent.getStringExtra("fcmToken");
//            // Store the FCM token in the LoginManager or perform any other required actions
//            LoginManager.saveFCMToken(context, fcmToken);
//            Log.d("FCM Token", fcmToken);
//        }
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (LoginManager.isLoggedIn(this)) {
//            // User is logged in, proceed to the main screen
//            Intent intent = new Intent(this, UserMainHome.class);
//            startActivity(intent);
//            finish();
//        } else {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    public void addUserData(){
        String uName = etName.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyHVL4Nq-iw1d33OPA8TFZHzPMxqvkZkWyB0YkAbXGexVFVaXcqbmoJ1l79M0igP5Jl/exec", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent intent = new Intent(getApplicationContext(), SuccessActivity.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("action","addUser");
                params.put("UserName",uName);
                return params;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



}