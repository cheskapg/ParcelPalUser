package com.example.parcelpaluser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.parcelpaluser.databinding.ActivityUserMainHomeBinding;
import com.squareup.picasso.Picasso;
import android.content.BroadcastReceiver;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class UserMainHome extends AppCompatActivity {
    private UserMainHome.MyBroadcastReceiver broadcastReceiver;
    ParcelListFragment parcelListFragment;
    ActivityUserMainHomeBinding binding;
    private boolean isParcelListFragmentLoaded = false;
    private Fragment currentFragment;

    String loggedInUserEmail, fcmToken , userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserMainHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        broadcastReceiver = new UserMainHome.MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("FCM_TOKEN_RECEIVED");
        registerReceiver(broadcastReceiver, intentFilter);

        loggedInUserEmail = LoginManager.getUserEmail(this);
        getUserIDbyEmail();

        replaceFragment(new AddParcelFragment(), loggedInUserEmail);
        Toast myToast = Toast.makeText(this,  loggedInUserEmail + " has logged In", Toast.LENGTH_LONG);
        myToast.show();


        String token = LoginManager.getFCMToken(this);
//        Toast toast = Toast.makeText(this, "TOKEN " + token, Toast.LENGTH_LONG);
//        toast.show();
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.addParcelMenu:
                    replaceFragment(new AddParcelFragment(), loggedInUserEmail);
                    break;
                case R.id.parcelListMenu:
                    replaceFragment(new ParcelListFragment(), loggedInUserEmail);
                    break;
                case R.id.notifMenu:
                    replaceFragment(new NotifFragment(), loggedInUserEmail);
                    break;
                case R.id.accountMenu:
                    replaceFragment(new AccountFragment(), loggedInUserEmail);
                    break;
            }
            return true;
        });
    }
//        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
//            Fragment selectedFragment = null;
//
//            switch (item.getItemId()) {
//                case R.id.addParcelMenu:
//                    selectedFragment = new AddParcelFragment();
//                    break;
//                case R.id.parcelListMenu:
//                    selectedFragment = new ParcelListFragment();
//                    break;
//                case R.id.notifMenu:
//                    selectedFragment = new NotifFragment();
//                    break;
//                case R.id.accountMenu:
//                    selectedFragment = new AccountFragment();
//                    break;
//            }
//
//            // Check if the selected fragment is already the current fragment
//            if (selectedFragment != null && currentFragment != null && selectedFragment.getClass().equals(currentFragment.getClass())) {
//                return true; // Do nothing if the selected fragment is already displayed
//            }
//
//            replaceFragment(selectedFragment, loggedInUserEmail);
//            return true;
//        });
//    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            fcmToken = intent.getStringExtra("fcmToken");
            // Store the FCM token in the LoginManager or perform any other required actions
        }
    }
    public void replaceCurrentFragment(Fragment fragment, boolean setParcelListActive) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
        if (setParcelListActive) {
            binding.bottomNavigationView.getMenu().findItem(R.id.parcelListMenu).setChecked(true);
        }
    }
//    private void replaceFragment(Fragment fragment, String loggedInUserEmail) {
//        boolean isParcelListFragmentCurrent = currentFragment instanceof ParcelListFragment;
//        boolean isParcelListFragment = fragment instanceof ParcelListFragment;
//
//        Bundle args = new Bundle();
//        args.putString("loggedInUserEmail", loggedInUserEmail);
//        fragment.setArguments(args);
//        currentFragment = fragment;
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        if (isParcelListFragment) {
//            if (isParcelListFragmentCurrent) {
//                // Do nothing if the ParcelListFragment is already the current fragment
//                return;
//            }
//
//            if (isFirstLoad) {
//                // Execute the getParcel() method
//                isFirstLoad = false;
//            }
//
//            if (isParcelListFragmentLoaded) {
//                // Show the already loaded ParcelListFragment
//                fragmentTransaction.show(parcelListFragment);
//            } else {
//                // Create a new instance of the ParcelListFragment and add it
//                parcelListFragment = new ParcelListFragment();
//                fragmentTransaction.add(R.id.flFragment, parcelListFragment, "ParcelListFragment");
//                isParcelListFragmentLoaded = true;
//            }
//        } else {
//            // If the fragment being replaced is not the ParcelListFragment, hide the ParcelListFragment
//            if (parcelListFragment != null && parcelListFragment.isAdded()) {
//                fragmentTransaction.hide(parcelListFragment);
//            }
//        }
//
//        fragmentTransaction.replace(R.id.flFragment, fragment);
//        fragmentTransaction.commit();
//    }
        private void replaceFragment(Fragment fragment, String loggedInUserEmail) {
            Bundle args = new Bundle();
            args.putString("loggedInUserEmail", loggedInUserEmail);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flFragment, fragment); // try to add so no reload
        //    fragmentTransaction.add(R.id.flFragment, fragment);
         //   fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();
    }
    private String getUserEmail() {
        return loggedInUserEmail.toString();
    }
    public void getUserIDbyEmail(){
        String url = String.format("https://script.google.com/macros/s/AKfycbz2GSZM8IQ7UU4x4BZh5I2LZhbO79qYEFwb5IEFT94ovsUWPoCG58U4XsSuWYBzyQ/exec?action=getUserIdByEmail&email=%s", getUserEmail());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response (file URL)
                        userId = response;
//                        Intent intent = new Intent("userId received");
//                        intent.putExtra("userId", userId);
//                        sendBroadcast(intent);
                        String userIdString = userId;

                        Intent intent = new Intent("getUserId");
                        intent.putExtra("userId", userIdString);
                        LocalBroadcastManager.getInstance(UserMainHome.this).sendBroadcast(intent);
                        Toast.makeText(getApplicationContext(),userId, Toast.LENGTH_SHORT).show();


                    }//Use the file URL as needed




                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
                Toast.makeText(getApplicationContext(),"Error" + userId, Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }




}
//        private void replaceFragment(Fragment fragment){
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.flFragment,fragment);
//            fragmentTransaction.commit();
//        }