package com.example.parcelpaluser;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.squareup.picasso.Picasso;

public class PayMobileWallet extends AppCompatActivity {
    Bitmap selectedImage;
//    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivityResultLauncher<String> imagePickerLauncher;

    String getTrackingID;
    Button uploadBtn, btnsendToDb;
    ImageView chosenImage;
    private ProgressDialog progressDialog;
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
        setContentView(R.layout.activity_pay_mobile_wallet);
        uploadBtn = (Button) findViewById(R.id.btnUpload);
        getParcelIntent();
        btnsendToDb = (Button) findViewById(R.id.btnSendToDb);
        chosenImage = (ImageView) findViewById(R.id.iv_uphoto);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }

        });
        btnsendToDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToSheet();
            }
        });
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            try {
                                // Get the bitmap from the selected image
                                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                                // Set the bitmap to the ImageView
                                chosenImage.setImageBitmap(selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }



    private void showFileChooser() {
        imagePickerLauncher.launch("image/*");
    }
    public void displayAlertUploadOk(Context context,
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
                        Toast.makeText(context, "YOU Uploaded Proof of Payment for  " + getTrackingID,Toast.LENGTH_SHORT).show();
//
                        dialog.dismiss();

                        Intent movetoUserHome = new Intent(context, UserMainHome.class);
                        startActivity(movetoUserHome);

                    }
                });
//        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, CANCEL,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog,
//                                        int which) {
//                        Toast.makeText(context, "NAWP", Toast.LENGTH_SHORT).show();
//
//                        dialog.dismiss();
//                    }
//                });
        alertDialog.show();
    }

    public void getParcelIntent() {
        Intent intent = getIntent();
        getTrackingID = intent.getStringExtra("trackingID");
        Toast myToast = Toast.makeText(PayMobileWallet.this, getTrackingID, Toast.LENGTH_LONG);
        myToast.show();

    }

    private void updateGoogleSheets(String imageBlob) {
        // Call the Google Apps Script function to update the Google Sheets
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwS2Is-wrG3aCzV9C9EwA7dHFn8EvrJWfSnBoqe_t7Sf8gm9VxJJ5KZ4GFXmRnD2NVF/exec?action=addProofPaymentImage", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog();
                Toast.makeText(PayMobileWallet.this, response.toString(), Toast.LENGTH_SHORT).show();
                displayAlertUploadOk(PayMobileWallet.this, "Uploaded","Proof of Payment Uploaded", DISMISSER );

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(PayMobileWallet.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("action","addParcelItem");
                params.put("trackingID", getTrackingID);
                params.put("imageBlob", imageBlob);

                return params;
            }
        };

        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


        // Show the progress dialog here
        showProgressDialog();


    }

    private void showProgressDialog() {
        // Create and show the progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        // Dismiss the progress dialog if it is showing
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void uploadImageToSheet() {
        if (selectedImage != null) {
            // Convert the bitmap to a base64-encoded string
            String encodedImage = getStringImage(selectedImage);

            // Call the method to update the Google Sheets
            updateGoogleSheets(encodedImage);
        }
    }


// Helper method to convert a bitmap to a base64-encoded stringprivate
            public String getStringImage(Bitmap bmp) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            return encodedImage;
            }



}
//    private void updateGoogleSheets(String imageBlob) {
//        // Call the Google Apps Script function to update the Google Sheets
//        String url = "https://script.google.com/macros/s/AKfycbwwVjxJvO7CjUsrIErl7iQ6k1FfHswiOZ2Hf9fkUQNqtVvVyGvHiavRsXmb3yL0lw2X/exec?action=addProofPaymentImage";
//
//        // Create a JSON object with the tracking ID and image blob
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("trackingID", getTrackingID);
//            jsonObject.put("imageBlob", imageBlob);
//            Toast.makeText(PayMobileWallet.this, "You uploaded " +imageBlob, Toast.LENGTH_SHORT).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        // Create a Volley request to send the JSON object to the server
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Toast .makeText(PayMobileWallet.this, "You uploaded " +imageBlob, Toast.LENGTH_SHORT).show();                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast .makeText(PayMobileWallet.this, "You error     " +error, Toast.LENGTH_SHORT).show();                      }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                // Add any headers if required
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//
//        // Add the request to the Volley request queue
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(request);
//    }

//    private void uploadImageToSheet(){
//        if (selectedImage != null) {
//            // Convert the bitmap to a base64-encoded string
//            String encodedImage = getStringImage(selectedImage);
//
//            // Create a JSON object with the encoded image
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("image", encodedImage);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            // Create a Volley request to send the JSON object to the server
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "YOUR_UPLOAD_URL", jsonObject,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            // Handle the response if needed
//                        }
//                    },
//
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            // Handle the error if needed
//                        }
//                    }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError
//                {
//                    // Add any headers if required
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Content-Type", "application/json");
//                    return headers;
//                }
//            };
//
//            // Add the request to the Volley request queue
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            requestQueue.add(request);
//        }
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri filePath = data.getData();
//            try {
//                // Get the bitmap from the selected image
//                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                // Set the bitmap to the ImageView
//                chosenImage.setImageBitmap(selectedImage);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//        private void showFileChooser() {
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//        }
//        private void showFileChooser() {
//            imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
//                    new ActivityResultCallback<Uri>() {
//                        @Override
//                        public void onActivityResult(Uri result) {
//                            if (result != null) {
//                                try {
//                                    // Get the bitmap from the selected image
//                                    selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
//                                    // Set the bitmap to the ImageView
//                                    chosenImage.setImageBitmap(selectedImage);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    });
//        }
//Image processing
//    public String getStringImage(Bitmap bmp) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] imageBytes = baos.toByteArray();
//        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
//        return encodedImage;
//    }
////Image processing
//public String getStringImage(Bitmap bmp) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] imageBytes = baos.toByteArray();
//        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
//        return encodedImage;
//        }
//public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        float bitmapRatio = (float)width / (float) height;
//        if (bitmapRatio > 1) {
//        width = maxSize;
//        height = (int) (width / bitmapRatio);
//        } else {
//        height = maxSize;
//        width = (int) (height * bitmapRatio);
//        }
//        return Bitmap.createScaledBitmap(image, width, height, true);
//
//        }
//public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        float bitmapRatio = (float)width / (float) height;
//        if (bitmapRatio > 1) {
//            width = maxSize;
//            height = (int) (width / bitmapRatio);
//        } else {
//            height = maxSize;
//            width = (int) (height * bitmapRatio);
//        }
//        return Bitmap.createScaledBitmap(image, width, height, true);
//
//    }

//
//    private void uploadImageToSheet() {
//        if (selectedImage != null) {
//            // Convert the bitmap to a base64-encoded string
//            String encodedImage = getStringImage(selectedImage);
//
//            // Create a JSON object with the encoded image
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("proofPayImage", encodedImage);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            // Create a Volley request to send the JSON object to the server
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "YOUR_UPLOAD_URL", jsonObject,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            // Handle the response if needed
//                            // Extract the imageBlob from the response
//                            try {
//                                String imageBlob = response.getString("imageBlob");
//                                // Call the method to update the Google Sheets
//                                updateGoogleSheets(imageBlob);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            // Handle the error if needed
//                        }
//                    }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    // Add any headers if required
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Content-Type", "application/json");
//                    return headers;
//                }
//            };
//
//            // Add the request to the Volley request queue
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            requestQueue.add(request);
//        }
//    }


//===================================
//private void updateGoogleSheets(String imageBlob) {
//    // Call the Google Apps Script function to update the Google Sheets
//    String url = "https://script.google.com/macros/s/AKfycbwS2Is-wrG3aCzV9C9EwA7dHFn8EvrJWfSnBoqe_t7Sf8gm9VxJJ5KZ4GFXmRnD2NVF/exec?action=addProofPaymentImage";
//
//    // Create a JSON object with the tracking ID and image blob
//    JSONObject jsonObject = new JSONObject();
//    try {
//        jsonObject.put("trackingID", getTrackingID);
//        jsonObject.put("imageBlob", imageBlob);
//        Toast.makeText(PayMobileWallet.this, "You uploaded " + imageBlob, Toast.LENGTH_SHORT).show();
//    } catch (JSONException e) {
//        e.printStackTrace();
//    }
//
//    Create a Volley request to send the JSON object to the server
//    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
//            new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        // Handle the response from the server here
//                        String message = response.getString("message");
//                        Toast.makeText(PayMobileWallet.this, message, Toast.LENGTH_SHORT).show();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Toast.makeText(PayMobileWallet.this, "Error parsing response", Toast.LENGTH_SHORT).show();
// Hide the progress dialog here
//                    hideProgressDialog();
//                    }
//                    // Hide the progress dialog here
//                    hideProgressDialog();
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(PayMobileWallet.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.d("VOLLEY CHECK", "Volley Error " + error.toString());
//                    // Hide the progress dialog here
//                    hideProgressDialog();
//                }
//            }) {
//        @Override
//        public Map<String, String> getHeaders() throws AuthFailureError {
//            // Add any headers if required
//            Map<String, String> headers = new HashMap<>();
//            headers.put("Content-Type", "application/json");
//            return headers;
//        }
//    };
//
//    // Show the progress dialog here
//    showProgressDialog();
//
//    // Add the request to the Volley request queue
//    RequestQueue requestQueue = Volley.newRequestQueue(this);
//    requestQueue.add(request);
//}
