package com.example.parcelpaluser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.HashMap;
import java.util.Map;
import android.content.Intent;

import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;import android.content.BroadcastReceiver;
import android.content.Context;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddParcelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddParcelFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText etTrackingId, etOrderTotal, etProductName, etOrderId;
    RadioGroup etPaymentType, paymentComp;
    RadioButton paymentRadioBT, paymentCompBT;
    TextView paymentCompTv;
    Button btnAddParcel;
    int selectedPayment, selectedPaymentComp;
    String paymentCompText  , userId;
    boolean updatepaymentchosen;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
//    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    public AddParcelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddParcelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddParcelFragment newInstance(String param1, String param2) {
        AddParcelFragment fragment = new AddParcelFragment();
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
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return the view from the fragment add parcel xml
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_parcel, container, false);

        paymentComp = view.findViewById(R.id.payment_comp_radiogroup);
        paymentCompTv = view.findViewById(R.id.tv_compartment);
        etTrackingId = view.findViewById(R.id.tracking_id_edittext);
        etOrderTotal = view.findViewById(R.id.order_total_edittext);
        etPaymentType = view.findViewById(R.id.payment_type_radio_group);
        btnAddParcel = view.findViewById(R.id.btnAddParcel);

        etProductName = view.findViewById(R.id.product_name_edittext);
        etOrderId = view.findViewById(R.id.order_id_edittext);
         selectedPayment = etPaymentType.getCheckedRadioButtonId();
         selectedPaymentComp = paymentComp.getCheckedRadioButtonId();
        paymentRadioBT = (RadioButton) view.findViewById(selectedPayment);
        paymentCompBT = (RadioButton) view.findViewById(selectedPaymentComp);
        etPaymentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.cod_radio_button:
                        paymentCompTv.setVisibility(View.VISIBLE);
                        paymentComp.setVisibility(View.VISIBLE);
                        updatepaymentchosen = false;

                        paymentComp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId)
                            {
                                switch(checkedId)
                                {
                                    case R.id.comp1_radio_button:


                                        int selectedPaymentComp = paymentComp.getCheckedRadioButtonId();
                                        paymentCompBT = (RadioButton) view.findViewById(selectedPaymentComp);

                                        paymentCompText = "1" ;                           break;
                                    case R.id.comp2_radio_button:
                                        selectedPaymentComp = paymentComp.getCheckedRadioButtonId();
                                        paymentCompBT = (RadioButton) view.findViewById(selectedPaymentComp);

                                        paymentCompText = "2";

                                }
                            }
                        });
                        break;
                    case R.id.wallet_radio_button:
                        paymentCompTv.setVisibility(View.GONE);
                        paymentComp.setVisibility(View.GONE);
                        updatepaymentchosen = true;
                        paymentCompText ="None";

                    case R.id.prepaid_radio_button:
                        paymentCompTv.setVisibility(View.GONE);
                        paymentComp.setVisibility(View.GONE);
                        updatepaymentchosen = false;
                        paymentCompText ="None";
                        break;
                }
            }
        });
        btnAddParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trackingId = etTrackingId.getText().toString().trim();
                String orderTotal = etOrderTotal.getText().toString().trim();
                String productName = etProductName.getText().toString().trim();
                String orderId = etOrderId.getText().toString().trim();

                // Validate the fields
                if (TextUtils.isEmpty(productName)) {
                    etProductName.setError("Field required");
                    return;
                }
                if (TextUtils.isEmpty(trackingId)) {
                    etTrackingId.setError("Field required");
                    return;
                }

                if (TextUtils.isEmpty(orderId)) {
                    etOrderId.setError("Field required");
                    return;
                }
                if (TextUtils.isEmpty(orderTotal)) {
                    etOrderTotal.setError("Field required");
                    return;
                }

                int selectedPaymentId = etPaymentType.getCheckedRadioButtonId();
                int selectedPaymentCompId = paymentComp.getCheckedRadioButtonId();

                if (selectedPaymentId == -1) {
                    // No payment type selected
                    Toast.makeText(getActivity(), "Please select a payment type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedPaymentCompId == -1 && paymentComp.getVisibility() == View.VISIBLE) {
                    // No payment compartment selected
                    Toast.makeText(getActivity(), "Please select a payment compartment", Toast.LENGTH_SHORT).show();
                    return;
                }

                addParcel();


            }
        });
        return view;

    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if ("getUserId".equals(intent.getAction())) {

                String receivedString = intent.getStringExtra("userId");
                userId = receivedString;
                // Handle the received string
            }
            Toast.makeText(getContext(),"getUserId : add parcel frag " +userId, Toast.LENGTH_SHORT).show();
            Log.d("Parcel", "getUserId : add parcel frag " +userId);
        }

    };

    private void addParcel() {
        String tracking_id = etTrackingId.getText().toString();
        String orderTotal = etOrderTotal.getText().toString();
        String productName = etProductName.getText().toString();
        String order_id = etOrderId.getText().toString();
        selectedPayment = etPaymentType.getCheckedRadioButtonId();
        paymentRadioBT = (RadioButton) getView().findViewById(selectedPayment);
        selectedPaymentComp = paymentComp.getCheckedRadioButtonId();
        paymentCompBT = (RadioButton) getView().findViewById(selectedPaymentComp);
        String paymentType_id = paymentRadioBT.getText().toString();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbycoJM-I4YdT2oMwlI8ZZY8a9HkqrH1N36Aux_Zqcc6MqG6dPnLiL00QODfjk_ESfEK/exec", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Notify the parent activity or fragment to replace the current fragment

                if (getParentFragment() != null) {
                    ((UserMainHome) getActivity()).replaceCurrentFragment(new ParcelListFragment(),true );
                } else if (getActivity() != null) {
                    ((UserMainHome) getActivity()).replaceCurrentFragment(new ParcelListFragment(), true);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("action","addParcelItemOnly");
                params.put("tracking_id", tracking_id);
                params.put("user_id", userId);

                params.put("orderTotal", orderTotal);
                params.put("paymentType_id", paymentType_id);
                params.put("productName", productName);
                params.put("order_id", order_id);
                params.put("payment_Comp", paymentCompText);

                return params;

            }

        };


        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);

    }
    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, new IntentFilter("getUserId"));
    }
    @Override
    public void onPause() {
        super.onPause();
//        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver);
    }
}