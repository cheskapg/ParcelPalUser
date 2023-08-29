package com.example.parcelpaluser;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import android.os.Bundle;


public class ScanQrActivity extends AppCompatActivity {
    String scannedData;
    String checkData;
    String urlString;
    String sampleScannedData;
    private BarcodeDetector barcodeDetector;
    Context context;
    int brightness;
    boolean scanbuttonClicked, checkbuttonClicked;
    Button scanBtn, checkBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        final Activity activity =this;
        //barcode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
        scanBtn = (Button)findViewById(R.id.scan_btn);
        checkBtn = (Button) findViewById(R.id.check_btn);

        scanbuttonClicked =false;
        checkbuttonClicked=false;
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        context = getApplicationContext();
        brightness =
                Settings.System.getInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 0);
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 100);
        if (!barcodeDetector.isOperational()) {
            Toast.makeText(this, "Could not set up the barcode detector", Toast.LENGTH_SHORT).show();
            return;
        }

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanbuttonClicked =true;
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setBeepEnabled(true);
                integrator.setCameraId(1);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkbuttonClicked =true;
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Check");
                integrator.setBeepEnabled(true);
                integrator.setCameraId(1);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null) {
            scannedData = result.getContents();
            if (scannedData != null) {
                if(scanbuttonClicked) {
                    // Here we need to handle scanned data...
                    new SendRequest().execute();
                }
                if(checkbuttonClicked){
                    new CheckRequest().execute();
                }

            }else {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class CheckRequest extends AsyncTask<String, Void, String>
    {


        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {
                // Enter script URL Here
                String baseUrl = "https://script.google.com/macros/s/AKfycbzo4AcjGSjstcuwYja8-SEAG6ot4xhKY6CaSTQq9rKw-s_rmZpF54aqJMiZpevEzdbg/exec";
                String action = "checkQR";
                String trackingId = scannedData;
                sampleScannedData = scannedData;
                // Construct the URL with the parameters
                 urlString = baseUrl + "?action=" + action + "&trackingId=" + trackingId;
                URL url = new URL(urlString);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000 /* milliseconds */);
                conn.setConnectTimeout(30000 /* milliseconds */);
                conn.setRequestMethod("GET");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();
                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }
        @Override
        protected void onPostExecute(String result) {
            int dur = 1000000;
            Toast.makeText(getApplicationContext(), result,Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Checking Barcode",Toast.LENGTH_LONG).show();
           ;
            Log.i("Info", urlString);
            Toast.makeText(getApplicationContext(), sampleScannedData,Toast.LENGTH_LONG).show();
        }
    }


    public class SendRequest extends AsyncTask<String, Void, String>
    {


        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{

                //Enter script URL Here
                URL url = new URL("https://script.google.com/macros/s/AKfycbwsaC5SjNgbXN9K29GmI15DwDFIbKUwbWDYHYP5NmzNMH1MXLmZcL-aTPlx8NaL1Zrw/exec");

                JSONObject postDataParams = new JSONObject();

                //int i;
                //for(i=1;i<=70;i++)
                //    String usn = Integer.toString(i);
                //Passing scanned code as parameter
                postDataParams.put("action","addQrItem");

                postDataParams.put("sdata",scannedData);


                Log.e("params",postDataParams.toString());

                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(30000 /* milliseconds */);
                conn.setConnectTimeout(30000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();

        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
