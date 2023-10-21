package com.example.parcelpaluser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import com.squareup.picasso.Picasso;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParcelListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParcelListFragment extends Fragment {
    List<parcelListData> list;
    private boolean isFirstLoad = true;

    Activity activity;

    // ...

    @Override    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }
    RecyclerView recyclerView;
    ProgressDialog loading;
    EditText etParcelSearch;
    parcelListAdapter mAdapter;
    String user_id;
    RadioGroup rgFilter;

    RadioButton rbComplete, rbPending;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ParcelListFragment() {

        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParcelListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParcelListFragment newInstance(String param1, String param2) {
        ParcelListFragment fragment = new ParcelListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    String loggedInUserEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         loggedInUserEmail = LoginManager.getUserEmail(requireContext());


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return the view from the fragment add parcel xml
        View view = inflater.inflate(R.layout.fragment_parcel_list, container, false);
        rgFilter = (RadioGroup) view.findViewById(R.id.radiogrp);
        if (isFirstLoad) {
            // Execute the getParcel() method
            getUserIDFromDatabase(loggedInUserEmail);
            isFirstLoad = false;
        }
        // Inflate the layout for this fragment
        etParcelSearch = view.findViewById(R.id.etParcelSearch);
        list = new ArrayList<>();
//        list.add(new parcelListData(R.drawable.ic_user, "31232", "tracking", "user", "200", "gcash", "nowl", "Gadget", "Delivered", "2321", "today", "paymentCompartment"));
        recyclerView = view.findViewById(R.id.RecyclerView_ParcelList);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAdapter = new parcelListAdapter(list, view.getContext().getApplicationContext());
        recyclerView.setAdapter(mAdapter);
//        getUserIDFromDatabase(loggedInUserEmail);
//        getParcelItems();
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {


                        getUserIDFromDatabase(loggedInUserEmail);

                        // This line is important as it explicitly
                        // refreshes only once
                        // If "true" it implicitly refreshes forever
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        //FILTER ALL PENDING AND RECEIVED
        rgFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.rball:

                        if (etParcelSearch.getText().toString().isEmpty()) {
                            ParcelListFragment.this.mAdapter.getFilter("e");
                        }
                        break;

                    case R.id.rbpending:

                        ParcelListFragment.this.mAdapter.getFilter("Pending");
                        break;

                    case R.id.rbcomplete:
                        ParcelListFragment.this.mAdapter.getFilter("Received");
                }
            }
        });
        return view;
    }
    private void getParcelItems() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxFnaLnWZyfQnHT1imN3Zg4Os0C57IYqerFUPGJkqNdyZYb1SpuyIKgJvYEDZiAzYh9/exec?action=getParcels",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(stringRequest);
    }
    private void parseItems(String jsonResponse) {
        List<parcelListData> itemList = new ArrayList<>();
        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);

                String parcelId = jo.getString("parcel_id");
                String trackingId = jo.getString("tracking_id");
                String userId = jo.getString("user_id");
                String orderTotal = jo.getString("orderTotal");
                String paymentType = jo.getString("paymentType_id");
                String payment_id = jo.getString("payment_id");
                String productName = jo.getString("productName");
                String deliveryStatus = jo.getString("deliveryStatus");
                String orderId = jo.getString("order_id");
                String dateReceived = jo.getString("date_received");
                String paymentCompartment = jo.getString("paymentCompartment");

                if (userId.equals(user_id)) {
                    // Create a parcelListData object with the retrieved data
                    parcelListData item = new parcelListData(R.drawable.ic_user, parcelId, trackingId, userId, orderTotal, paymentType, payment_id, productName, deliveryStatus, orderId, dateReceived, paymentCompartment);
                    itemList.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loading.dismiss(); //Remove loading popup

        //update the adapter with the list populated
        mAdapter = new parcelListAdapter(itemList,activity.getApplicationContext());
        recyclerView.setAdapter(mAdapter);
        //SET DELETE CLICK ON ITEM LISTENER, WITH ONITEMCLICKLISTENER FROM PARCELLISTADAPTER INTERFACE

        mAdapter.setOnItemClickListener(new parcelListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String parcelId) {
                String getParcelId = parcelId;
                Intent moveToViewItem = new Intent(requireContext(), ViewParcelListItem.class);
                moveToViewItem.putExtra("parcelID", getParcelId);
                startActivity(moveToViewItem);
                Toast myToast = Toast.makeText(activity, "Item click", Toast.LENGTH_LONG);
                myToast.show();
            }

            @Override
            public void onEditItemClick(String parcelId) {
                String getParcelId = parcelId;
                Intent movetoUpdate = new Intent(requireContext(), updateParcelItem.class);
                movetoUpdate.putExtra("parcelID", getParcelId);
                startActivity(movetoUpdate);
            }

            @Override
            public void onPayMobileItemClick(String trackingID) {
                String getParcelId = trackingID;
                Intent moveToPayMobile = new Intent(requireContext(), PayMobileWallet.class);
                moveToPayMobile.putExtra("trackingID", getParcelId);
                startActivity(moveToPayMobile);
            }

            @Override
            public void onDeleteItemClick(String trackingId) {
                mAdapter.onDeleteClick(trackingId); //CALLING FROM PARCELLISTADAPTER

                Toast myToast = Toast.makeText(activity, "Item delete", Toast.LENGTH_LONG);
                myToast.show();
            }
        });
//SEARCH FILTER
        etParcelSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = charSequence.toString();
                ParcelListFragment.this.mAdapter.getFilter(searchText);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }
    private void getUserIDFromDatabase(String userEmail) {
        loading = ProgressDialog.show(activity, "Loading", "please wait", false, false);

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
                            // Do something with the user ID
                            Toast mytoast = Toast.makeText(activity, user_id, Toast.LENGTH_LONG);
                            mytoast.show();
                            getParcelItems();


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


}


//"https://script.google.com/macros/s/AKfycbxFnaLnWZyfQnHT1imN3Zg4Os0C57IYqerFUPGJkqNdyZYb1SpuyIKgJvYEDZiAzYh9/exec?action=getParcels"