//package com.example.parcelpaluser;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.RetryPolicy;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.firebase.messaging.FirebaseMessaging;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
//public class LoginActivity extends AppCompatActivity {
//    String userEmail;
//    String password ;
//    EditText etEmail, etPass;
//    Button btnLogin, btnReg;
//    ProgressDialog loading;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        etEmail = (EditText) findViewById(R.id.emailEditText);
//        etPass = (EditText) findViewById(R.id.passwordEditText);
//        btnLogin = (Button) findViewById(R.id.loginButton);
//
//        btnReg = (Button) findViewById(R.id.registerButton);
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loading = ProgressDialog.show(LoginActivity.this, "Logging in", "please wait", false, false);
//
//                Executor executor = Executors.newSingleThreadExecutor();
//                executor.execute(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        checkLoginCredentials();
//
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        });
//                    }
//
//                });
//            }
//        });
//        btnReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
//                startActivity(intent);
//
//            }
//        });
//    }
//    public void insertUserToken(){
//
//        String token = LoginManager.getFCMToken(this);
//        String email = etEmail.getText().toString();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwnVn_Zcw3dWr8FlhJKNoSEdzLg7ghd6LUedOCjXt1p6NlecB9PTSWnyOsaFmkM8yNW/exec", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Toast.makeText(LoginActivity.this, token,Toast.LENGTH_LONG).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams(){
//                Map<String,String> params = new HashMap<>();
//                params.put("action","insertUserToken");
//                params.put("userEmail",email);
//                params.put("token",token);
//                return params;
//            }
//        };
//        int socketTimeOut = 50000;
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(retryPolicy);
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }
//    public void checkLoginCredentials() {
//
//        userEmail = etEmail.getText().toString();
//        password = etPass.getText().toString();
//
//        // Construct the URL with query parameters
//        Uri.Builder builder = Uri.parse("https://script.google.com/macros/s/AKfycbxy6Kh0DiRldbvX6MZv-WxXRe56Zz3fbC7OqKE47uZaQKhGoOoZf2lKB_NvP2lo27_J/exec").buildUpon();
//        builder.appendQueryParameter("action", "checkLogin");
//        builder.appendQueryParameter("email", userEmail);
//        builder.appendQueryParameter("pass", password);
//        String url = builder.build().toString();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        if (response.equals("success")) {
//                            // User email and password are correct
//                            Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
//                            LoginManager.saveLoginState(LoginActivity.this, true);
//                            FirebaseMessaging.getInstance().getToken();
//
//                            insertUserToken();
//
//                            loading.dismiss();
//
//
//
//                            Intent intent = new Intent(getApplicationContext(), UserMainHome.class);
//
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            String email = userEmail; // Replace with the actual user's email
//                            LoginManager.saveUserEmail(LoginActivity.this, email);
//                            startActivity(intent);
//                        } else {
//                            // User email and password are incorrect
//                            loading.dismiss();
//                            Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // Handle error response
//                        Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//        int socketTimeOut = 50000;
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(retryPolicy);
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }
//}

package com.example.parcelpaluser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    String userEmail;
    String password, fcmToken ;
    EditText etEmail, etPass;
    Button btnLogin, btnReg;
    ProgressDialog loading;
    private LoginActivity.MyBroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = (EditText) findViewById(R.id.emailEditText);
        etPass = (EditText) findViewById(R.id.passwordEditText);
        btnLogin = (Button) findViewById(R.id.loginButton);
        btnReg = (Button) findViewById(R.id.registerButton);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loading = ProgressDialog.show(LoginActivity.this, "Loading", "please wait", false, true);

                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {

                        checkLoginCredentials();


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }

                });
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);

            }
        });
    }
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String fcmToken = intent.getStringExtra("fcmToken");
            // Store the FCM token in the LoginManager or perform any other required actions
            Log.d("FCM Token", fcmToken);
        }
    }
    public void insertUserToken(){

        String email = etEmail.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxy_lrlQK2jmbAJHAzUe_uBolSMv_Tt7sMB2nHdeZ-9a8Ha-sj4JEGEao5t_AoTDlXx/exec", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



                Toast.makeText(LoginActivity.this, fcmToken,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(LoginActivity.this,"NO INSERT"+ fcmToken,Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("action","insertUserToken");
                params.put("userEmail",email);
                params.put("token", fcmToken);
                params.put("device",getDeviceName());
                return params;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut,5,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void retrieveFCMTokenWithRetry() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String getToken = task.getResult();
                    fcmToken = getToken;
                    insertUserToken();
                    LoginManager.saveFCMToken(LoginActivity.this, fcmToken);
                    Toast.makeText(getApplicationContext(), "Login Token Success " + fcmToken, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Token GET FAILED", Toast.LENGTH_SHORT).show();
                    // Retry after a delay
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            retrieveFCMTokenWithRetry();
                        }
                    }, 2000); // Retry after 5 seconds (adjust the delay as needed)
                }
            }
        });
    }
    public void checkLoginCredentials() {
        broadcastReceiver = new LoginActivity.MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("FCM_TOKEN_RECEIVED");
        registerReceiver(broadcastReceiver, intentFilter);
        userEmail = etEmail.getText().toString();
        password = etPass.getText().toString();

        // Construct the URL with query parameters
        Uri.Builder builder = Uri.parse("https://script.google.com/macros/s/AKfycbxy6Kh0DiRldbvX6MZv-WxXRe56Zz3fbC7OqKE47uZaQKhGoOoZf2lKB_NvP2lo27_J/exec").buildUpon();
        builder.appendQueryParameter("action", "checkLogin");
        builder.appendQueryParameter("email", userEmail);
        builder.appendQueryParameter("pass", password);
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            retrieveFCMTokenWithRetry();
                            // User email and password are correct
                            LoginManager.saveLoginState(LoginActivity.this, true);
                            Intent intent = new Intent(getApplicationContext(), UserMainHome.class);
                            String email = userEmail; // Replace with the actual user's email
                            loading.dismiss();

                            LoginManager.saveUserEmail(LoginActivity.this, email);
                            startActivity(intent);

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);


                        } else {
                            // User email and password are incorrect
                            loading.dismiss();

                            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        if (loading != null && loading.isShowing()) {
                            loading.dismiss();
                        }

                        Toast.makeText(getApplicationContext(), "INTERNET Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}