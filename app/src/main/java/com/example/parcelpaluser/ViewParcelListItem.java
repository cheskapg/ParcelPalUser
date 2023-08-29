package com.example.parcelpaluser;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.view.MenuItem;
import android.view.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ViewParcelListItem extends AppCompatActivity {
    List<getParcelItem> list;
    TextView textView_Proofpay;
    Button btnPayMobile;
    TextView tvTrackingId,uploadstatus, tvOrderTotal, tvProductName, tvOrderId, tvPaymentMethod, tvCompartment, tvDeliveryStatus, tvDateReceived;
    String trackingId, userId, orderTotal, paymentType, payment_id, productName, deliveryStatus, orderId, dateReceived, paymentComp;
    public String getParcel;
    public String getTrackingId;
    String updParcelId, updUserId;
    ImageView viewMenu, imageProof;
    ProgressDialog loading;
    String fileUrl;
    public static final String OK = "OK";
    public static final String CANCEL = "Cancel";
    private static final DialogInterface.OnClickListener DISMISSER = new DialogInterface.OnClickListener() {

        @Override//from  w w w .  j  a  v  a  2 s  . co m
        public void onClick(DialogInterface dialog, int which) {

            dialog.dismiss();
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_parcel_list_item);
        btnPayMobile = findViewById(R.id.btnPayMobile);
        uploadstatus = (TextView) findViewById(R.id.txtUploadNone);
         textView_Proofpay = (TextView) findViewById(R.id.textView_Proofpay);
        imageProof = (ImageView) findViewById(R.id.iv_uphoto);
        tvTrackingId = findViewById(R.id.tracking_id_edittext);
        tvOrderTotal = findViewById(R.id.order_total_edittext);
        tvProductName = findViewById(R.id.product_name_edittext);
        tvOrderId = findViewById(R.id.order_id_edittext);
        tvPaymentMethod = findViewById(R.id.tvPaymentMeth);
        tvCompartment = findViewById(R.id.tv_compartment);
        tvDeliveryStatus = findViewById(R.id.tvDeliveryStatus);
        tvDateReceived = findViewById(R.id.textView_DateReceived);
        viewMenu = findViewById(R.id.viewMenu);
        Toast myToast = Toast.makeText(ViewParcelListItem.this, "Test", Toast.LENGTH_LONG);
        myToast.show();
        getParcelIntent();


        getParcelItemById();
//        getPaymentImageUrl();
        viewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(ViewParcelListItem.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.viewitemmenu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    Toast .makeText(ViewParcelListItem.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();

                    if (menuItem.getItemId() == R.id.edit) {
                        Toast.makeText(ViewParcelListItem.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        String getParcelId = getParcel;
                        Intent movetoUpdate = new Intent(ViewParcelListItem.this, updateParcelItem.class);
                        movetoUpdate.putExtra("parcelID", getParcelId);
                        startActivity(movetoUpdate);

                    } else if (menuItem.getItemId() == R.id.delete) {
                        Toast.makeText(ViewParcelListItem.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();

                        displayAlertOKCancel(ViewParcelListItem.this, "Delete Parcel","Are you sure you want to delete " + productName +" ?", DISMISSER );
                    }
                    return true;
        });
                popupMenu.show();
         }

         });

        btnPayMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getTracking = trackingId;
                Intent moveToPayMobile = new Intent(ViewParcelListItem.this, PayMobileWallet.class);
                moveToPayMobile.putExtra("trackingID", getTracking);

                startActivity(moveToPayMobile);
            }
        });
    }
    private String getTracking() {
       return tvTrackingId.getText().toString();
    }
    public void getPaymentImageUrl(){
        String url = String.format("https://script.google.com/macros/s/AKfycbzsZ3xrbuMqUNSqr2mfAO-Ul9RgukopL9eGpAWpXxQB7ZMqFfBvYzRs9UVgU0AME5Rv/exec?action=getFileUrlByTrackingID&trackingID=%s", getTracking());
//        String url = "https://script.google.com/macros/s/AKfycbx9mRJksTUMDsQZ1Qvf6Kvh7AECQG3e1ufe9au3hIXYgkzVVUe98hfxS8F0VrfsrEHo/exec?action=getFileUrlByTrackingID&trackingID="+ trackingId;// Replace with your AppScript URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response (file URL)
                         fileUrl = response;
                        Toast.makeText(getApplicationContext(),"file" +url, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),getTracking(), Toast.LENGTH_SHORT).show();
                        if(paymentType.equals("Mobile Wallet")) {

                            if (tvDeliveryStatus.getText().toString().equals("attempting to deliver") && fileUrl == null) {
                                imageProof.setVisibility(View.GONE);
                                btnPayMobile.setVisibility(View.VISIBLE);
                                uploadstatus.setVisibility(View.VISIBLE);
                                loading.dismiss();
                            } else if (tvDeliveryStatus.getText().toString().equals("pending")) {
                                imageProof.setVisibility(View.GONE);
                                btnPayMobile.setVisibility(View.GONE);
                                uploadstatus.setVisibility(View.VISIBLE);
                                loading.dismiss();
                            } else {
                                imageProof.setVisibility(View.VISIBLE);
                                uploadstatus.setVisibility(View.GONE);
                                btnPayMobile.setVisibility(View.GONE);

                                String imageUrl = fileUrl; // Replace with your file URL

                                if (!TextUtils.isEmpty(imageUrl)) {
                                    Picasso.get().load(imageUrl).into(imageProof);
                                    loading.dismiss();
                                } else {
                                    imageProof.setVisibility(View.GONE);
                                    btnPayMobile.setVisibility(View.VISIBLE);
                                    uploadstatus.setVisibility(View.VISIBLE);
                                    loading.dismiss();
                                }
                            }


                        }//Use the file URL as needed
                        loading.dismiss();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
                Toast.makeText(getApplicationContext(),"Error" + fileUrl, Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    public void displayAlertOKCancel(Context context,
                                            String alertTitle, String alertMessage,
                                            DialogInterface.OnClickListener okListener) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(alertTitle);
        alertDialog.setMessage(alertMessage);
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //DELETE FROM DB BUT FOR TESTING WE COMMENT IT OUT
//                        deleteItemFromDatabase(getParcel);
                        Toast.makeText(context, "YOU Deleted  " + getParcel + " - " + productName, Toast.LENGTH_SHORT).show();

                        dialog.dismiss();

                            Intent movetoUserHome = new Intent(context, UserMainHome.class);
                            startActivity(movetoUserHome);

                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, CANCEL,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(context, "NAWP", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void getParcelIntent() {
        Intent intent = getIntent();
        getParcel = intent.getStringExtra("parcelID");
        Toast myToast = Toast.makeText(ViewParcelListItem.this, getParcel, Toast.LENGTH_LONG);
        myToast.show();

    }
    public void deleteItemFromDatabase(String trackingId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbz_PcQDCRVGDyPiHlXkMHofdwjTG-F5zL9047Ol79uuIoxaS0ob_XdQY0kh4UYudvtY/exec", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Handle the response if needed
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error if needed
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "deleteParcelByParcelId");
                params.put("parcelId", trackingId); //change reference on which to delete
                return params;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getParcelItemById() {
        loading = ProgressDialog.show(this, "Loading", "please wait", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxFnaLnWZyfQnHT1imN3Zg4Os0C57IYqerFUPGJkqNdyZYb1SpuyIKgJvYEDZiAzYh9/exec?action=getParcels",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response, getParcel);

                        getPaymentImageUrl();

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response                }
                    }

                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);


    }
    //parse the response from the string query

    //pass string id parameter
    private void parseItems(String jsonResponse, String desiredParcelId) {
        List<getParcelItem> itemList = new ArrayList<>();
        try {
            JSONObject jobj = new JSONObject(jsonResponse);
            JSONArray jarray = jobj.getJSONArray("items");

            // Iterate over the items array
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                //declare a temporary id value to match
                String parcelId = jo.getString("parcel_id");

                // Check if the parcel_id matches the desiredParcelId
                if (parcelId.equals(desiredParcelId)) {
                    trackingId = jo.getString("tracking_id");
                    userId = jo.getString("user_id");
                    orderTotal = jo.getString("orderTotal");
                    paymentType = jo.getString("paymentType_id");
                    payment_id = jo.getString("payment_id");
                    productName = jo.getString("productName");
                    deliveryStatus = jo.getString("deliveryStatus");
                    orderId = jo.getString("order_id");
                    dateReceived = jo.getString("date_received");
                    paymentComp = jo.getString("paymentCompartment");
                    updParcelId = parcelId;
                    //not necessary anymore
                    updUserId = userId;
                    break; // Exit the loop after finding the matching item
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

        Toast myToast = Toast.makeText(ViewParcelListItem.this, updUserId, Toast.LENGTH_LONG);
        myToast.show();


        tvProductName.setText(productName);
        tvTrackingId.setText(trackingId);
        tvOrderId.setText(orderId);
        tvOrderTotal.setText(orderTotal);
        tvPaymentMethod.setText(paymentType);
        tvCompartment.setText(paymentComp);
        tvDeliveryStatus.setText(deliveryStatus);
        tvDateReceived.setText(dateReceived);
//        getTracking = trackingId;

        if(paymentType.equals("Mobile Wallet")) {
            btnPayMobile.setVisibility(View.VISIBLE);
            if (fileUrl == null && tvDeliveryStatus.getText().toString().equals("pending") ){
                uploadstatus.setVisibility(View.VISIBLE);
                imageProof.setVisibility(View.GONE);
                btnPayMobile.setVisibility(View.VISIBLE);

            }
           else{
                imageProof.setVisibility(View.VISIBLE);
                uploadstatus.setVisibility(View.GONE);
                btnPayMobile.setVisibility(View.VISIBLE);

                String imageUrl = fileUrl; // Replace with your file ID

                Picasso.get().load(imageUrl).into(imageProof);
            }// Use the file URL as needed
            textView_Proofpay.setVisibility(View.VISIBLE);
        }
        else{
            btnPayMobile.setVisibility(View.INVISIBLE);
            textView_Proofpay.setVisibility(View.GONE);

        }

        if(paymentType.equals("Cash on Delivery")) {
            tvCompartment.setVisibility(View.VISIBLE);
            TextView tvCompartment2 = findViewById(R.id.tv_compartment1);
            tvCompartment2.setVisibility(View.VISIBLE);
            btnPayMobile.setVisibility(View.GONE);


        }
        else{
            btnPayMobile.setVisibility(View.GONE);

            tvCompartment.setVisibility(View.GONE);
            TextView tvCompartment2 = findViewById(R.id.tv_compartment1);
            tvCompartment2.setVisibility(View.GONE);
        }



    }
}