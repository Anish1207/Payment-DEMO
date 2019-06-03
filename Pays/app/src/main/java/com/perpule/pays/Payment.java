package com.perpule.pays;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import es.dmoral.toasty.Toasty;

public class Payment extends AppCompatActivity {

    protected ProgressDialog dialog;
    private String PhoneNumber;
    private String BillAmount;
    TextView textService;
    Button btnPay;

    private static final String VALIDATE_TOKEN_URL="https://micro-s-perpule.appspot.com/validatetoken?";
    private static final String CHECK_BALANCE_URL="https://micro-s-perpule.appspot.com/checkbalance?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        textService=findViewById(R.id.textService);

        //TODO Get phone number and bill amount;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        PhoneNumber = extras.getString("number");
        BillAmount = extras.getString("bill");
        btnPay =findViewById(R.id.btnPay);

        //validateToken
        textService.setText("Validating Token...");
        new Payment.ValidateToken().execute();


    }


    class ValidateToken extends AsyncTask<Void, Integer, String> {


        protected void onPreExecute() {
            super.onPreExecute();
            dialog = Utility.getProgressDialog(Payment.this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            if (dialog != null) {
                dialog.show();
            }

        }

        protected String doInBackground(Void... urls) {


            try {
                URL url = new URL(VALIDATE_TOKEN_URL+"number=" + PhoneNumber );
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

                    //To show loading spinner and grey out screen
                    synchronized (this) {
                        //Initialize an integer (that will act as a counter) to zero
                        int counter = 0;
                        //While the counter is smaller than four
                        while (counter <= 4) {
                            //Wait 850 milliseconds
                            this.wait(850);
                            //Increment the counter
                            counter++;
                            //Set the current progress.
                            //This value is going to be passed to the onProgressUpdate() method.
                            publishProgress(counter * 25);
                        }
                    }

                    //return response
                    return stringBuilder.toString();

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }

        protected void onPostExecute(String response) {
            if (response == null) {

                Toasty.error(Payment.this, " Null response!", Toast.LENGTH_SHORT, true).show();
            } else {

                try {
                    Log.i("response:", response);
                    if (response.trim().equalsIgnoreCase("exists")) {
                        dialog.dismiss();
                        Toasty.success(Payment.this, "Your access token is valid!", Toast.LENGTH_SHORT, true).show();
                        textService.setText("Checking balance...");
                        new Payment.CheckBalance().execute();

                    } else if (response.trim().equalsIgnoreCase("no")) {
                        //dismiss loading dialogue
                        Toasty.error(Payment.this, "Your access token is invalid!", Toast.LENGTH_SHORT, true).show();
                        //TODO handle resend otp
                    }

                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }

    }

    class CheckBalance extends AsyncTask<Void, Integer, String> {


        protected void onPreExecute() {
            super.onPreExecute();
            dialog = Utility.getProgressDialog(Payment.this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            if (dialog != null) {
                dialog.show();
            }

        }

        protected String doInBackground(Void... urls) {


            try {
                URL url = new URL(CHECK_BALANCE_URL+"number=" + PhoneNumber+"&totalamount="+BillAmount);
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

                    //To show loading spinner and grey out screen
                    synchronized (this) {
                        //Initialize an integer (that will act as a counter) to zero
                        int counter = 0;
                        //While the counter is smaller than four
                        while (counter <= 4) {
                            //Wait 850 milliseconds
                            this.wait(850);
                            //Increment the counter
                            counter++;
                            //Set the current progress.
                            //This value is going to be passed to the onProgressUpdate() method.
                            publishProgress(counter * 25);
                        }
                    }

                    //return response
                    return stringBuilder.toString();

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
                dialog.dismiss();
                Toasty.error(Payment.this, "null response!", Toast.LENGTH_SHORT, true).show();
            } else {

                try {
                    Log.i("response:", response);
                    if (Float.parseFloat(response.trim())<=0) {
                        dialog.dismiss();
                        textService.setVisibility(View.GONE);
                        Toasty.success(Payment.this, "You can proceed to payment", Toast.LENGTH_SHORT, true).show();
                        btnPay.setVisibility(View.VISIBLE);
                        btnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //TODO pay - auto debit api
                            }
                        });



                    } else {
                        //dismiss loading dialogue
                        dialog.dismiss();
                        textService.setVisibility(View.GONE);
                        Toasty.info(Payment.this, "You have LESS BALANCE, \nAdd at least "+Float.parseFloat(response.trim())+" to your wallet", Toast.LENGTH_SHORT, true).show();
                        //TODO add money api

                    }

                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }

    }



}
