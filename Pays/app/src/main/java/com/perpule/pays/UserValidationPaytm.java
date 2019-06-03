package com.perpule.pays;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import es.dmoral.toasty.Toasty;

public class UserValidationPaytm extends AppCompatActivity {


    private String PhoneNumber;
    private String BillAmount;
    EditText enterotp;
    ProgressBar progressBar;
    Button btnValidate;
    protected ProgressDialog dialog;

    static final String SEND_OTP_URL = "https://micro-s-perpule.appspot.com/sendotp?number=";
    static final String VALIDATE_OTP_URL = "https://micro-s-perpule.appspot.com/validateotp?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_validation);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        PhoneNumber = extras.getString("number");
        BillAmount = extras.getString("bill");


        progressBar=findViewById(R.id.progressBar);
        enterotp=findViewById(R.id.enterotp);

        Toasty.info(UserValidationPaytm.this, "An OTP has been sent to your mobile number!", Toast.LENGTH_SHORT, true).show();

        new UserValidationPaytm.ReceiveOTP().execute();

        btnValidate =findViewById(R.id.btnValidate);
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UserValidationPaytm.ValidateOtp().execute();
            }
        });
    }

    class ReceiveOTP extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(Void... urls) {

            // TODO some validation here


            try {
                URL url = new URL(SEND_OTP_URL + PhoneNumber);
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

    class ValidateOtp extends AsyncTask<Void, Integer, String> {

        private Exception exception;

        protected void onPreExecute() {
            super.onPreExecute();
            dialog = Utility.getProgressDialog(UserValidationPaytm.this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            if (dialog != null) {
                dialog.show();
            }

        }

        protected String doInBackground(Void... urls) {
            String otp = enterotp.getText().toString();
            // TODO some validation here


            try {
                URL url = new URL(VALIDATE_OTP_URL + "number=" + PhoneNumber +"&otp=" + otp);
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
                    synchronized (this)
                    {
                        //Initialize an integer (that will act as a counter) to zero
                        int counter = 0;
                        //While the counter is smaller than four
                        while(counter <= 4)
                        {
                            //Wait 850 milliseconds
                            this.wait(850);
                            //Increment the counter
                            counter++;
                            //Set the current progress.
                            //This value is going to be passed to the onProgressUpdate() method.
                            publishProgress(counter*25);
                        }
                    }
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
                //TODO move ahead with flow of paytm flow
                TextView status=findViewById(R.id.txtValidate);
                try {
                    JSONObject jobj= new JSONObject(response);
                    if(jobj.has("status") && jobj.getString("status").equalsIgnoreCase("failure")) {
                        dialog.dismiss();
                        Toasty.error(UserValidationPaytm.this, "Verification failed!", Toast.LENGTH_SHORT, true).show();

                    }
                    else {
                        //dismiss loading page
                        dialog.dismiss();
                        Toasty.success(UserValidationPaytm.this, "You are now a registered customer!", Toast.LENGTH_SHORT, true).show();
                        Intent intent =new Intent(UserValidationPaytm.this, Payment.class);
                        Bundle extras= new Bundle();
                        extras.putString("number",PhoneNumber);
                        extras.putString("bill",BillAmount);
                        intent.putExtras(extras);
                        startActivity(intent);
                        //TODO new intent to proceed for payment!
                    }

                } catch (Exception e) {

                }
            }


            Log.i("INFO", response);

        }


    }
}
