package com.perpule.pays;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

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
    EditText emailId;
    ProgressBar progressBar;

    static final String API_URL = "https://accounts-uat.paytm.com/signin/otp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        phNum = findViewById(R.id.phnum);
        emailId = findViewById(R.id.editText2);
        progressBar=findViewById(R.id.progressBar);
        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RetrieveFeedTask().execute();
            }
        });

    }


    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(Void... urls) {
            String phoneNumber = phNum.getText().toString();
            String email = emailId.getText().toString();
            // TODO some validation here

            /*
            try {
                //URL url = new URL(API_URL + "phone=" + phoneNumber);
                URL url = new URL(API_URL);
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
            */


            String clientId = "merchant-perpule-stg";
            String scope = "wallet";
            String responseType = "token";
            String merchantKey = "&!vj74@Ri&g6U1TI";

            TreeMap<String, String> paytmParams = new TreeMap<String, String>();
            paytmParams.put("email", email);
            paytmParams.put("phone", phoneNumber);
            paytmParams.put("clientId", clientId);
            paytmParams.put("scope", scope);
            paytmParams.put("responseType", responseType);

            try {
                URL transactionURL = new URL(API_URL);
                JSONObject obj = new JSONObject(paytmParams);
                String postData = obj.toString();

                HttpURLConnection connection = (HttpURLConnection) transactionURL.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setUseCaches(false);
                connection.setDoOutput(true);

                DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
                requestWriter.writeBytes(postData);
                requestWriter.close();
                String responseData = "";
                InputStream is = connection.getInputStream();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
                if ((responseData = responseReader.readLine()) != null) {
                    System.out.append("Response Json = " + responseData);
                }
                System.out.append("Requested Json = " + postData + " ");
                responseReader.close();
                return responseData;

            }

            catch (Exception exception) {
                exception.printStackTrace();
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

}
