package com.example.parcelpaluser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class parcelList extends AppCompatActivity {
    List<parcelListData> list;
    RecyclerView recyclerView;
    ProgressDialog loading;
    EditText etParcelSearch;
    parcelListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_list);
        etParcelSearch = findViewById(R.id.etParcelSearch);
        list = new ArrayList<>();
        list.add(new parcelListData(R.drawable.ic_user, "31232", "tracking", "user", "200", "gcash", "nowl", "Gadget", "Delivered", "2321", "today","paymentCompartment"));
        recyclerView = findViewById(R.id.RecyclerView_ParcelList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new parcelListAdapter(list, getApplicationContext());
        recyclerView.setAdapter(mAdapter);
        getParcelItems();

    }
    //GET PARCEL LIST
    private void getParcelItems() {
        loading = ProgressDialog.show(this, "Loading", "please wait", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxFnaLnWZyfQnHT1imN3Zg4Os0C57IYqerFUPGJkqNdyZYb1SpuyIKgJvYEDZiAzYh9/exec?action=getParcels?action=getParcels",
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
        RequestQueue queue = Volley.newRequestQueue(this);
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
                //courierid is paymentid
                String payment_id = jo.getString("payment_id");
                String productName = jo.getString("productName");
                String deliveryStatus = jo.getString("deliveryStatus");
                String orderId = jo.getString("order_id");
                String dateReceived = jo.getString("date_received");
                String paymentCompartment = jo.getString("paymentCompartment");
                // Create a parcelListData object with the retrieved data
                parcelListData item = new parcelListData(R.drawable.ic_user, parcelId, trackingId, userId, orderTotal, paymentType, payment_id, productName, deliveryStatus, orderId, dateReceived, paymentCompartment);
                itemList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loading.dismiss(); //Remove loading popup

        //update the adapter with the list populated
        mAdapter = new parcelListAdapter(itemList, getApplicationContext());
        recyclerView.setAdapter(mAdapter);
        //SET DELETE CLICK ON ITEM LISTENER, WITH ONITEMCLICKLISTENER FROM PARCELLISTADAPTER INTERFACE

        mAdapter.setOnItemClickListener(new parcelListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String parcelId) {
                String getParcelId = parcelId;
                Intent moveToViewItem = new Intent(parcelList.this, updateParcelItem.class);
                moveToViewItem.putExtra("parcelID", getParcelId);
                startActivity(moveToViewItem);
                    Toast myToast = Toast.makeText(parcelList.this , "Item click", Toast.LENGTH_LONG);
                    myToast.show();
            }

            @Override
            public void onEditItemClick(String parcelId){
                    String getParcelId = parcelId;
                    Intent movetoUpdate = new Intent(parcelList.this, updateParcelItem.class);
                movetoUpdate.putExtra("parcelID", getParcelId);

                    startActivity(movetoUpdate);



            }

            @Override
            public void onPayMobileItemClick(String trackingID) {
                String getTracking = trackingID;
                Intent moveToPayMobile = new Intent(parcelList.this, updateParcelItem.class);
                moveToPayMobile.putExtra("trackingID", getTracking);

                startActivity(moveToPayMobile);

            }


            @Override
            public void onDeleteItemClick(String trackingId) {
                mAdapter.onDeleteClick(trackingId); //CALLING FROM PARCELLISTADAPTER

                Toast myToast = Toast.makeText(parcelList.this, "Item delete", Toast.LENGTH_LONG);
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
                parcelList.this.mAdapter.getFilter(searchText);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



    }

}

