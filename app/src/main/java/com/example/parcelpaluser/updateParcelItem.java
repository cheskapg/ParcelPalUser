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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class updateParcelItem extends AppCompatActivity {
    List<getParcelItem> list;
    ProgressDialog loading;
    Button btnUpdateParcel, back_button;
    EditText etParcelId, etTrackingId, etUserId, etOrderTotal, etCourierId, etProductName, etDeliveryStat, etOrderId, etDateReceived;
    RadioGroup etPaymentType, paymentComp;
    RadioButton rbPrepaid, rbCOD, rbWallet, paymentRadioBT, paymentCompBT, comp1_radio_button, comp2_radio_button;
    String parcelId, trackingId, userId, orderTotal, paymentType, payment_id, productName, deliveryStatus, orderId, dateReceived, paymentcomp;
    String getParcel;
    String updParcelId, updTrackingId, updUserId;
    TextView paymentCompTv;
    int selectedPaymentComp;
    RadioButton comp1, comp2, comp3, comp4;

    String paymentCompText;
    boolean updatepaymentchosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_parcel_item);
        btnUpdateParcel = findViewById(R.id.btnUpdateParcel);
//            etParcelId = findViewById(R.id.parcel_id_edittext);
        etTrackingId = findViewById(R.id.tracking_id_edittext);
//            etUserId = findViewById(R.id.user_id_edittext);
        etOrderTotal = findViewById(R.id.order_total_edittext);
        etPaymentType = findViewById(R.id.payment_type_radio_group);
        rbPrepaid = findViewById(R.id.prepaid_radio_button);
        rbCOD = findViewById(R.id.cod_radio_button);
        rbWallet = findViewById(R.id.wallet_radio_button);
//            etCourierId = findViewById(R.id.courier_id_edittext);
        etProductName = findViewById(R.id.product_name_edittext);
//            etDeliveryStat = findViewById(R.id.delivery_status_edittext);
        etOrderId = findViewById(R.id.order_id_edittext);
//            etDateReceived = findViewById(R.id.date_received_edittext);
        paymentComp = findViewById(R.id.payment_comp_radiogroup);
        paymentCompTv = findViewById(R.id.tv_compartment);
        selectedPaymentComp = paymentComp.getCheckedRadioButtonId();
        paymentCompBT = (RadioButton) findViewById(selectedPaymentComp);
        comp1_radio_button = (RadioButton) findViewById(R.id.comp1_radio_button);
        comp3 = (RadioButton) findViewById(R.id.comp3_radio_button);
        comp2 = (RadioButton) findViewById(R.id.comp2_radio_button);
        comp1 = (RadioButton) findViewById(R.id.comp1_radio_button);
        comp4 = (RadioButton) findViewById(R.id.comp4_radio_button);
        comp2_radio_button = (RadioButton) findViewById(R.id.comp2_radio_button);checkCompartmentExisting();

        btnUpdateParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (updatepaymentchosen) {
//                        deleteItemFromPaymentDatabase(getParcel);
                    updateParcelItem();

                } else {
                    updateParcelItem();

                }


            }
        });

        etPaymentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.wallet_radio_button:

                        updatepaymentchosen = false;
                        paymentCompText = "None";
                        paymentCompTv.setVisibility(View.GONE);
                        paymentComp.setVisibility(View.GONE);
                        break;
                    case R.id.prepaid_radio_button:
                        paymentCompTv.setVisibility(View.GONE);
                        paymentComp.setVisibility(View.GONE);
                        updatepaymentchosen = false;
                        paymentCompText = "None";
                        break;
                    case R.id.cod_radio_button:
                        paymentCompTv.setVisibility(View.VISIBLE);
                        paymentComp.setVisibility(View.VISIBLE);
                        updatepaymentchosen = false;

                        paymentComp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId) {
                                    case R.id.comp1_radio_button:


                                        int selectedPaymentComp = paymentComp.getCheckedRadioButtonId();
                                        paymentCompBT = (RadioButton) findViewById(selectedPaymentComp);

                                        paymentCompText = "1";
                                        break;
                                    case R.id.comp2_radio_button:
                                        selectedPaymentComp = paymentComp.getCheckedRadioButtonId();
                                        paymentCompBT = (RadioButton) findViewById(selectedPaymentComp);

                                        paymentCompText = "2";

                                }
                            }
                        });
                        break;

                }
            }
        });

        getParcelIntent();
        getParcelItemById();
    }

    public void getParcelIntent() {
        Intent intent = getIntent();
        getParcel = intent.getStringExtra("parcelID");
        Toast myToast = Toast.makeText(updateParcelItem.this, getParcel, Toast.LENGTH_LONG);
        myToast.show();

    }

    private void getParcelItemById() {
        loading = ProgressDialog.show(this, "Loading", "please wait", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxFnaLnWZyfQnHT1imN3Zg4Os0C57IYqerFUPGJkqNdyZYb1SpuyIKgJvYEDZiAzYh9/exec?action=getParcels",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseItems(response, getParcel);
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
                    //courierid is now paymentid
                    payment_id = jo.getString("payment_id");
                    productName = jo.getString("productName");
                    deliveryStatus = jo.getString("deliveryStatus");
                    orderId = jo.getString("order_id");
                    dateReceived = jo.getString("date_received");
                    paymentcomp = jo.getString("paymentCompartment");
                    updParcelId = parcelId;
                    //not necessary anymore
                    updUserId = userId;
                    break; // Exit the loop after finding the matching item
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        Toast myToast = Toast.makeText(updateParcelItem.this, updUserId, Toast.LENGTH_LONG);
        myToast.show();
        loading.dismiss();

        //        for (int i = 0; i < itemList.size(); i++) {
        //            if (itemList.get(i).getParcelId().equals(getParcel)) {
//            etParcelId.setText(getParcel);
        etTrackingId.setText(trackingId);
//            etUserId.setText(updUserId);
        etOrderTotal.setText(orderTotal);
//            etCourierId.setText(courierId);
        etProductName.setText(productName);
//            etDeliveryStat.setText(deliveryStatus);
        etOrderId.setText(orderId);
//            etDateReceived.setText(dateReceived);
        if (paymentType != null) {
            if (paymentType.equals("Cash on Delivery")) {
                rbCOD.setChecked(true);
            } else if (paymentType.equals("Prepaid")) {
                rbPrepaid.setChecked(true);
            } else if (paymentType.equals("Mobile Wallet")) {
                rbWallet.setChecked(true);
            } else {
                rbPrepaid.setChecked(false);
                rbWallet.setChecked(false);
                rbCOD.setChecked(false);
            }
        }

        if (paymentcomp != null) {
            if (paymentcomp.equals("1")) {
                comp1_radio_button.setChecked(true);
            } else if (paymentcomp.equals("2")) {
                comp2_radio_button.setChecked(true);
            } else if (paymentcomp.equals("3")) {
                comp3.setChecked(true);
            } else if (paymentcomp.equals("4")) {
                comp4.setChecked(true);
            } else {
                comp1_radio_button.setChecked(false);
                comp2_radio_button.setChecked(false);
                comp3.setChecked(false);
                comp4.setChecked(false);
            }
        }
    }

    private void checkCompartmentExisting() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxe60Ea0TWyGRRig0LemVXLYN_KWBV_QJ6gPZyfiXIIJXsrDLPOqQHk2up0B2Nv_DIu/exec?action=checkExistingComp",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("disable 1")) {
                            comp1.setEnabled(false);
                        } else if (response.equals("disable 2")) {
                            comp2.setEnabled(false);
                        } else if (response.equals("disable 3")) {
                            comp3.setEnabled(false);
                        } else if (response.equals("disable 4")) {
                            comp4.setEnabled(false);
                        } else if (response.equals("disable 1,disable 2")) {
                            comp1.setEnabled(false);
                            comp2.setEnabled(false);
                        } else if (response.equals("disable 1,disable 3")) {
                            comp1.setEnabled(false);
                            comp3.setEnabled(false);
                        } else if (response.equals("disable 1,disable 4")) {
                            comp1.setEnabled(false);
                            comp4.setEnabled(false);
                        } else if (response.equals("disable 2,disable 3")) {
                            comp2.setEnabled(false);
                            comp3.setEnabled(false);
                        } else if (response.equals("disable 2,disable 4")) {
                            comp2.setEnabled(false);
                            comp4.setEnabled(false);
                        } else if (response.equals("disable 3,disable 4")) {
                            comp3.setEnabled(false);
                            comp4.setEnabled(false);
                        } else if (response.equals("disable 1,disable 2,disable 3")) {
                            comp1.setEnabled(false);
                            comp2.setEnabled(false);
                            comp3.setEnabled(false);
                        } else if (response.equals("disable 1,disable 2,disable 4")) {
                            comp1.setEnabled(false);
                            comp2.setEnabled(false);
                            comp4.setEnabled(false);
                        } else if (response.equals("disable 1,disable 3,disable 4")) {
                            comp1.setEnabled(false);
                            comp3.setEnabled(false);
                            comp4.setEnabled(false);
                        } else if (response.equals("disable 2,disable 3,disable 4")) {
                            comp2.setEnabled(false);
                            comp3.setEnabled(false);
                            comp4.setEnabled(false);
                        } else if (response.equals("disable 1,disable 2,disable 3,disable 4")) {
                            comp1.setEnabled(false);
                            comp2.setEnabled(false);
                            comp3.setEnabled(false);
                            comp4.setEnabled(false);
                        } else {
                            comp1.setEnabled(true);
                            comp2.setEnabled(true);
                            comp3.setEnabled(true);
                            comp4.setEnabled(true);
                        }
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
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void updateParcelItem() {
//            String parcel_id = etParcelId.getText().toString();
        String tracking_id = etTrackingId.getText().toString();
//            String user_id = etUserId.getText().toString();
        String orderTotal = etOrderTotal.getText().toString();
//            String courier_id = etCourierId.getText().toString();
        String productName = etProductName.getText().toString();
//            String deliveryStatus = etDeliveryStat.getText().toString();
        String order_id = etOrderId.getText().toString();
//            String date_received = etDateReceived.getText().toString();
        int selectedPayment = etPaymentType.getCheckedRadioButtonId();
        paymentRadioBT = (RadioButton) findViewById(selectedPayment);
        String paymentType_id = paymentRadioBT.getText().toString();
//            selectedPayment = etPaymentType.getCheckedRadioButtonId();
        paymentRadioBT = (RadioButton) findViewById(selectedPayment);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwHcGGzBh6c8qYyBcqfISAFUlHLOwP4gPOoAehJSUOJwNDbbG0-r3QmVkHpQihVuNju/exec?action=updateParcelByParcelId", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent intent = new Intent(getApplicationContext(), SuccessActivity.class);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
//                    params.put("action","addParcelItem");
                params.put("parcelId", getParcel);
                params.put("parcel_id", getParcel);
                params.put("tracking_id", tracking_id);
                params.put("orderTotal", orderTotal);
                params.put("paymentType_id", paymentType_id);
                //courierid is now paymentid
                params.put("payment_id", payment_id);
                params.put("productName", productName);
                params.put("deliveryStatus", deliveryStatus);
                params.put("date_received", dateReceived);
                params.put("order_id", order_id);
                params.put("payment_Comp", paymentCompText);

                return params;
            }
        };

        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    //delete Item from database method
//        public void deleteItemFromPaymentDatabase(String parcelId) {
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxAc-LRrqO6kdwG1fC2KM0x0pgrHaZc9Vg1ijKtxzpH-vsS8sNQ1VkYqxQ4aLTTKzmz/exec", new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    // Handle the response if needed
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    // Handle the error if needed
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("action", "deleteParcelByParcelIdPayment");
//                    params.put("parcelId", parcelId); //change reference on which to delete
//                    return params;
//                }
//            };
//            int socketTimeOut = 50000;
//            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//            stringRequest.setRetryPolicy(retryPolicy);
//
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            requestQueue.add(stringRequest);
//        }
}
