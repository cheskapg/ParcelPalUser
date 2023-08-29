package com.example.parcelpaluser;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class addParcelItem extends AppCompatActivity {

    //Declare Buttons
    Button btnAddParcel, back_button;
    EditText etParcelId, etTrackingId, etUserId, etOrderTotal, etCourierId, etProductName, etDeliveryStat, etOrderId, etDateReceived;
    String user_id ;

    RadioGroup etPaymentType;
    RadioButton paymentRadioBT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parcel_item);

        btnAddParcel = findViewById(R.id.btnAddParcel);
        etParcelId = findViewById(R.id.parcel_id_edittext);
        etTrackingId = findViewById(R.id.tracking_id_edittext);
        etUserId = findViewById(R.id.user_id_edittext);
        etOrderTotal = findViewById(R.id.order_total_edittext);
        etPaymentType = findViewById(R.id.payment_type_radio_group);
        etCourierId = findViewById(R.id.courier_id_edittext);
        etProductName = findViewById(R.id.product_name_edittext);
        etDeliveryStat = findViewById(R.id.delivery_status_edittext);
        etOrderId = findViewById(R.id.order_id_edittext);
        etDateReceived = findViewById(R.id.date_received_edittext);
        String loggedInUserEmail = LoginManager.getUserEmail(this);


        btnAddParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getUserIDFromDatabase(loggedInUserEmail);
            }
        });
    }

    private void addParcel() {
        String parcel_id = etParcelId.getText().toString();
        String tracking_id = etTrackingId.getText().toString();
         user_id = etUserId.getText().toString();
        String orderTotal = etOrderTotal.getText().toString();
        String courier_id = etCourierId.getText().toString();
        String productName = etProductName.getText().toString();
        String deliveryStatus = etDeliveryStat.getText().toString();
        String order_id = etOrderId.getText().toString();
        String date_received = etDateReceived.getText().toString();
        int selectedPayment = etPaymentType.getCheckedRadioButtonId();
        paymentRadioBT = (RadioButton) findViewById(selectedPayment);
        String paymentType_id = paymentRadioBT.getText().toString();


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
                params.put("action","addParcelItem");
                params.put("parcel_id", parcel_id);
                params.put("tracking_id", tracking_id);
                params.put("user_id", user_id);
                params.put("orderTotal", orderTotal);
                params.put("paymentType_id", paymentType_id);
                params.put("courier_id", courier_id);
                params.put("productName", productName);
                params.put("deliveryStatus", deliveryStatus);
                params.put("order_id", order_id);
                params.put("date_received", date_received);

                return params;
            }
        };

        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void getUserIDFromDatabase(String userEmail) {
        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Define the URL for your server endpoint
        String url = "https://script.google.com/macros/s/AKfycbzkZOCsgn7Bf695Iv7YW2HApuNsiA0izep3idYZy_uoEGOFLZ53ngMjmczL1d6w3KEu/exec?action=getUserId&email=" + userEmail;

        // Make a GET request to the server
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parse the response and extract the user ID
                            String userID = response.getString("userID");
                                user_id = userID;
                            addParcel();
                            // Do something with the user ID
                            // ...

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        // ...
                    }
                });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }


}