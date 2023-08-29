package com.example.parcelpaluser;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
        import java.util.Locale;
import java.util.Map;
public class parcelListAdapter extends RecyclerView.Adapter<parcelListAdapter.ParcelViewHolder> {
    //define OnItemClick custom
    private OnItemClickListener mListener;

//define interface
    public interface OnItemClickListener {
        void onItemClick(int position, String parcelId);
        void onDeleteItemClick(String trackingId); //  this line
        void onEditItemClick(String parcelId);
        void onPayMobileItemClick(String trackingID);
    }
    //define setOnItem
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener =listener;
    }
//OnDeleteClick called from main activity - parcelList
    public void onDeleteClick(String trackingId) {
        // Find the item with the matching tracking ID and remove it
        for (int i = 0; i < parcelList.size(); i++) {
            if (parcelList.get(i).getParcelId().equals(trackingId)) {
                parcelList.remove(i);
                filteredParcelList.remove(i);
                break;
            }
        }
        deleteItemFromDatabase(trackingId); // Call the deleteItemFromDatabase method

        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public void onEditClick(String parcelId){

    }

    //delete Item from database method
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

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }

    //Define parcelListData List for filter and search
    private List<parcelListData> parcelList;
    public static List<parcelListData> filteredParcelList; // List to store filtered data

    private Context context;
    public parcelListAdapter(List<parcelListData> parcelList, Context context) {
        this.parcelList = parcelList;
        this.filteredParcelList = new ArrayList<>(parcelList); // Initialize filteredParcelList with all data
        this.context = context;
    }

    @NonNull
    @Override

    public ParcelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_parcel_list, parent, false);
        return new ParcelViewHolder(view, mListener); //Add mListener as parameter to pass data for updating


    }

    @Override
    public void onBindViewHolder(@NonNull ParcelViewHolder holder, int position) {
        parcelListData parcel = filteredParcelList.get(position);

//        holder.imageView_ParcelItem.setImageResource(parcel.getImage());
        holder.textView_ParcelId.setText(parcel.getParcelId());
        holder.textView_TrackingId.setText(parcel.getTrackingId());
        holder.textView_UserId.setText(parcel.getUserId());
        holder.textView_OrderTotal.setText(parcel.getOrderTotal());
//        holder.textView_PaymentType.setText(parcel.getPaymentType());
        holder.textView_CourierId.setText(parcel.getCourierId());
        holder.textView_ProductName.setText(parcel.getProductName());
//        holder.textView_DeliveryStatus.setText(parcel.getDeliveryStatus());
        holder.textView_OrderId.setText(parcel.getOrderId());
        holder.textView_DateReceived.setText(parcel.getDateReceived());
//        holder.textView_Compartment.setText(parcel.getPaymentCompartment());

//
//        if(parcel.getPaymentCompartment().equals("None")){
//            holder.textView_Compartment.setVisibility(View.GONE);
//        }
//        if(parcel.getPaymentType().equals("Mobile Wallet")){
//            holder.btnPayMobile.setVisibility(View.VISIBLE);
//        }

    }
    @Override
    public int getItemCount() {
        return filteredParcelList.size();
    }
//METHOD GETTING DATA AND FILTERING IT TO ONLY SHOW THE SEARCH
    public void getFilter(String searchText) {
        filteredParcelList.clear(); // Clear the filteredParcelList before applying the filter

        if (searchText.isEmpty()) {
            filteredParcelList.addAll(parcelList); // If search text is empty, show all data
        } else {
            searchText = searchText.toLowerCase(Locale.getDefault());
            for (parcelListData parcel : parcelList) {
                if (parcel.getParcelId().toLowerCase(Locale.getDefault()).contains(searchText) ||
                        parcel.getTrackingId().toLowerCase(Locale.getDefault()).contains(searchText) ||
                        parcel.getUserId().toLowerCase(Locale.getDefault()).contains(searchText) ||
                        parcel.getOrderTotal().toLowerCase(Locale.getDefault()).contains(searchText) ||
                        parcel.getPaymentType().toLowerCase(Locale.getDefault()).contains(searchText) ||
                        parcel.getCourierId().toLowerCase(Locale.getDefault()).contains(searchText) ||
                        parcel.getProductName().toLowerCase(Locale.getDefault()).contains(searchText) ||
                        parcel.getDeliveryStatus().toLowerCase(Locale.getDefault()).contains(searchText) ||
                        parcel.getOrderId().toLowerCase(Locale.getDefault()).contains(searchText) ||
                        parcel.getDateReceived().toLowerCase(Locale.getDefault()).contains(searchText)) {
                    filteredParcelList.add(parcel); // Add the parcel to filteredParcelList if it matches the search text                }
                }
            }

            notifyDataSetChanged(); // Notify the adapter that the data has changed
        }
    }


    public static class ParcelViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView_ParcelItem;
        TextView textView_ParcelId;
        TextView textView_TrackingId;
        TextView textView_UserId;
        TextView textView_OrderTotal;
        TextView textView_PaymentType;
        TextView textView_CourierId;
        TextView textView_ProductName;
        TextView textView_DeliveryStatus;
        TextView textView_OrderId;
        TextView textView_DateReceived;
        TextView textView_Compartment;
        public ImageView btnDeleteItem;
        public ImageView btnEditItem;
        public Button btnPayMobile;
        public ParcelViewHolder(@NonNull View itemView , OnItemClickListener listener ) {
            super(itemView); // OnItemclicistener is passed so add mlistener in oncreate to pass the listener
            btnPayMobile = itemView.findViewById(R.id.btnPayMobile);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteParcel); //identify button
            btnEditItem = itemView.findViewById(R.id.btnEditParcel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener !=null){
                        int position = getAdapterPosition();
                        String parcelId = filteredParcelList.get(position).getParcelId();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position, parcelId);
                        }
                    }
                }

            });
            btnDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        String trackingId = filteredParcelList.get(position).getParcelId();
                        listener.onDeleteItemClick(trackingId); // Call onDeleteItemClick to get the ID from the parcelListdATA using getparcel..

                    }
                }
            });
            btnEditItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        String parcelId = filteredParcelList.get(position).getParcelId();
                        listener.onEditItemClick(parcelId); // Call onEdit to get the ID from the parcelListdATA using getparcel.


                    }
                }

            });
            btnPayMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        String trackingID = filteredParcelList.get(position).getTrackingId();
                        listener.onPayMobileItemClick(trackingID); // Call onPay to get the ID from the parcelListdATA using getTracking.


                    }
                }

            });


//            imageView_ParcelItem = itemView.findViewById(R.id.imageView_ParcelItem);
            textView_ParcelId = itemView.findViewById(R.id.textView_ParcelId);
            textView_TrackingId = itemView.findViewById(R.id.textView_TrackingId);
            textView_UserId = itemView.findViewById(R.id.textView_UserId);
            textView_OrderTotal = itemView.findViewById(R.id.textView_OrderTotal);
//            textView_PaymentType = itemView.findViewById(R.id.textView_PaymentType);
            textView_CourierId = itemView.findViewById(R.id.textView_CourierId);
            textView_ProductName = itemView.findViewById(R.id.textView_ProductName);
//            textView_DeliveryStatus = itemView.findViewById(R.id.textView_DeliveryStatus);
            textView_OrderId = itemView.findViewById(R.id.textView_OrderId);
            textView_DateReceived = itemView.findViewById(R.id.textView_DateReceived);
//            textView_Compartment = itemView.findViewById(R.id.textView_Compartment);



        }
    }
}