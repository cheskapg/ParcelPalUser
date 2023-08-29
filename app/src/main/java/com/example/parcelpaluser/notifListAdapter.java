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

public class notifListAdapter extends RecyclerView.Adapter<notifListAdapter.ParcelViewHolder> {
    //define OnItemClick custom
    private OnItemClickListener mListener;

    //define interface
    public interface OnItemClickListener {
        void onItemClick(int position, String parcelId);
    }


    //Define parcelListData List for filter and search
    private List<parcelListData> parcelList;
    public static List<parcelListData> filteredParcelList; // List to store filtered data    private Context context;

    private Context context;
    public notifListAdapter(List<parcelListData> parcelList, Context context) {
        this.parcelList = parcelList;
        this.filteredParcelList = new ArrayList<>(parcelList); // Initialize filteredParcelList with all data
        this.context = context;
    }

    @NonNull
    @Override

    public ParcelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_notif_list, parent, false);
        return new ParcelViewHolder(view, mListener); //Add mListener as parameter to pass data for updating


    }

    @Override
    public void onBindViewHolder(@NonNull ParcelViewHolder holder, int position) {
        parcelListData parcel = filteredParcelList.get(position);

//        holder.imageView_ParcelItem.setImageResource(parcel.getImage());
        holder.textView_TrackingId.setText(parcel.getTrackingId());
        holder.textView_ProductName.setText(parcel.getProductName());
        holder.textView_DeliveryStatus.setText(parcel.getDeliveryStatus());


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
                if ( parcel.getDeliveryStatus().toLowerCase(Locale.getDefault()).contains(searchText)) {
                    filteredParcelList.add(parcel); // Add the parcel to filteredParcelList if it matches the search text                }
                }
            }

            notifyDataSetChanged(); // Notify the adapter that the data has changed
        }
    }


    public static class ParcelViewHolder extends RecyclerView.ViewHolder {

        TextView textView_TrackingId;

        TextView textView_ProductName;
        TextView textView_DeliveryStatus;


        public ParcelViewHolder(@NonNull View itemView , OnItemClickListener listener ) {
            super(itemView); // OnItemclicistener is passed so add mlistener in oncreate to pass the listener
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




            textView_TrackingId = itemView.findViewById(R.id.textView_TrackingId);
            textView_ProductName = itemView.findViewById(R.id.tvProductName);
            textView_DeliveryStatus = itemView.findViewById(R.id.textView_DeliveryStatus);



        }
    }
}