package com.example.parcelpaluser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button btnLogout, btnUpdate;
    EditText user, phone, pass;
    TextView fullEmail;
    ProgressDialog loading;
    String loggedInUserEmail, user_id;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String userid, userEmail, userPass,userPhone;
    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            loggedInUserEmail = LoginManager.getUserEmail(requireContext());
            getUserIDFromDatabase(loggedInUserEmail);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnUpdate = view.findViewById(R.id.btnUpdateUser);
        user = view.findViewById(R.id.etEmail);
        phone = view.findViewById(R.id.etPhone);
        fullEmail = view.findViewById(R.id.fullname_field);
        pass = view.findViewById(R.id.etPass);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();


            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnUpdate.getText().equals("UPDATE")){
                    user.setEnabled(true);
                    phone.setEnabled(true);
                    pass.setEnabled(true);
                    btnUpdate.setText("SAVE");

                }  else if(btnUpdate.getText().equals("SAVE")){
                    user.setEnabled(false); // Disable input fields while updating
                    phone.setEnabled(false);
                    pass.setEnabled(false);

                    updateUserValue(user_id, user.getText().toString(), pass.getText().toString(), phone.getText().toString());

                    btnUpdate.setText("UPDATE");
                }
            }
        });

    return view;
    }


    public void logoutUser(){

        loading = ProgressDialog.show(requireActivity(), "Logging out", "please wait", false, false);


        String token = LoginManager.getFCMToken(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbx_bmjmMy994I_gjG32ZIhrNYwBqAMdY8hjpLp-fvgWQSq7tgq9eiDxjGLBZec9Vyh0/exec", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                    loading.dismiss();
                LoginManager.saveLoginState(requireContext(), false);
                FirebaseMessaging.getInstance().deleteToken();
                LoginManager.clearFCMToken(requireContext());
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error ",Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("action","logoutUser");
                params.put("token",token);
                return params;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut,4,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }
    private void updateUserValue( String userID,  String userEmail,  String userPass,  String userPhone){
        String url = "https://script.google.com/macros/s/AKfycbyqLpyuWlS41hlzvBiq_Gp92rZnOysUxvw6UDIjObjIKtXDJEvvCQOYu8TAyh50gfjD/exec?action=updateUserInfo";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response from the server (e.g., display a success message)
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();

                params.put("userId",userID);
                params.put("email",userEmail);
                params.put("phone",userPhone);
                params.put("pass",userPass);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }
    private void getUserIDFromDatabase(String userEmail) {
        loading = ProgressDialog.show(getActivity(), "Loading", "please wait", false, true);

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Define the URL for your server endpoint
        String url = "https://script.google.com/macros/s/AKfycbzkZOCsgn7Bf695Iv7YW2HApuNsiA0izep3idYZy_uoEGOFLZ53ngMjmczL1d6w3KEu/exec?action=getUserId&email=" + userEmail;

        // Make a GET request to the server
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // Parse the response and extract the user ID
                        String userID = response;
                        user_id = userID;
                        getUserInfo();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response
                // ...
            }
        });

        // Add the request to the RequestQueue
        queue.add(stringRequest);

    }
    private void getUserInfo() {

        String url = String.format("https://script.google.com/macros/s/AKfycbxdgaBQDzEjeQ4-C5FvqmpkbZtmMCE9QTj0wvBeOuHkDmTSIKAI3nfGDpffMmJnP4nL/exec?action=getUserInfo&userId=%s", getUserId());
        
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }

    private String getUserId() {
        return user_id;
    }
    //parse the response from the string query

    //pass string id parameter
    private void parseItems(String jsonResponse) {
        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");

            // Iterate over the items array
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);


                    userEmail = jo.getString("UserMail");
                    userPass = jo.getString("Password");
                    userPhone = jo.getString("SMSNotifNumber");

                    break; // Exit the loop after finding the matching item

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

        loading.dismiss();
        user.setText(userEmail);
        phone.setText(userPass);
        pass.setText(userPass);
        fullEmail.setText(userEmail);


    }

}