package com.perpule.pays;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeMap;


public class MainActivity extends AppCompatActivity {


    Button btnSendOtp;
    EditText phNum;
    EditText enterotp;
    ProgressBar progressBar;
    Button btnValidate;

    static final String SEND_OTP_URL = "https://20190529t163424-dot-paymentdemo-242005.appspot.com/sendotp?number=";
    static final String VALIDATE_OTP_URL = "https://20190529t163424-dot-paymentdemo-242005.appspot.com/validateotp?otp=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        phNum = findViewById(R.id.phnum);
        progressBar=findViewById(R.id.progressBar);
        enterotp=findViewById(R.id.enterotp);

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ReceiveOTP().execute();
            }
        });

        btnValidate =findViewById(R.id.btnValidate);
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ValidateOtp().execute();
            }
        });

    }


    class ReceiveOTP extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(Void... urls) {
            String phoneNumber = phNum.getText().toString();
            // TODO some validation here


            try {
                URL url = new URL(SEND_OTP_URL + phoneNumber);
                //URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }


        progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            // TODO check this.exception
            // TODO something with the feed

//             .
////                .
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
//                try {
//                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
//                String requestID = object.getString("requestId");
//                int likelihood = object.getInt("likelihood");
//                JSONArray photos = object.getJSONArray("photos");
//                .
//                .
//       }
        }


    }

    class ValidateOtp extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(Void... urls) {
            String otp = enterotp.getText().toString();
            // TODO some validation here


            try {
                URL url = new URL(VALIDATE_OTP_URL + otp);
                //URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            else {

                TextView status=findViewById(R.id.txtValidate);
                try {
                    JSONObject jobj= new JSONObject(response);
                    if(jobj.has("status") && jobj.getString("status").equalsIgnoreCase("failure")) {
                        status.setText("OTP Failed!");
                    }
                    else {
                        status.setText("OTP verified!!");
                    }

                } catch (Exception e) {

                }
            }

            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

        }


    }

}
