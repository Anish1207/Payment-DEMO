package com.perpule.pays;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import es.dmoral.toasty.Toasty;

public class Payment extends AppCompatActivity {

    protected ProgressDialog dialog;

    TextView textService;
    Button btnPay;
    Button btnReturn;
    TextView txtStatus;
    WebView webView;
    Button btnContinue;
    Button btnAddmoney;
    EditText txtAddmoney;

    private String PhoneNumber;
    private String BillAmount;
    private String Topupamount;
    private String TransactionId;
    private String MODE;


    private static final String INITIATE_URL="https://micro-s-perpule.appspot.com/initiate?";
    private static final String DEBIT_URL="https://micro-s-perpule.appspot.com/debit?";
    //private static final String GET_TRANSACTIONID_URL="https://micro-s-perpule.appspot.com/gettransactionid?";
    //private static final String VALIDATE_TOKEN_URL="https://micro-s-perpule.appspot.com/validatetoken?";
    //private static final String CHECK_BALANCE_URL="https://micro-s-perpule.appspot.com/checkbalance?";
    //private static final String AUTO_DEBIT_URL="https://micro-s-perpule.appspot.com/autodebit?";
    private static final String ADD_MONEY_URL="https://micro-s-perpule.appspot.com/addmoney?";

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
        TransactionId=extras.getString("transactionId");
        MODE=extras.getString("mode");

        btnPay =findViewById(R.id.btnPay);
        webView=findViewById(R.id.addMoney);
        btnContinue=findViewById(R.id.btnContinue);
        txtStatus=findViewById(R.id.txtStatus);
        btnReturn=findViewById(R.id.btnReturn);
        btnAddmoney=findViewById(R.id.btnAddmoney);
        txtAddmoney=findViewById(R.id.txtAddmoney);


        //validateToken
        textService.setText("Validation...");
        new Payment.Initiate().execute();


    }



    class Initiate extends AsyncTask<Void, Integer, String> {


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
                URL url = new URL(INITIATE_URL+"mode="+MODE+"&number=" + PhoneNumber+"&totalamount="+BillAmount+"&transid="+TransactionId);
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
                            //Wait 20 milliseconds
                            this.wait(20);
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
                dialog.dismiss();
                Toasty.error(Payment.this, "null response!", Toast.LENGTH_SHORT, true).show();
            } else {

                try {
                    Log.i("response:", response);
                    if (response.trim().equalsIgnoreCase("true")) {
                        dialog.dismiss();
                        textService.setVisibility(View.GONE);
                        Toasty.success(Payment.this, "You can proceed to payment", Toast.LENGTH_SHORT, true).show();
                        btnPay.setVisibility(View.VISIBLE);
                        btnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new Payment.Debit().execute();
                            }
                        });



                    } else {
                        //dismiss loading dialogue
                        dialog.dismiss();
                        textService.setVisibility(View.GONE);
                        Toasty.info(Payment.this, "You have LESS BALANCE!", Toast.LENGTH_SHORT, true).show();
                        //TODO add money api
                        txtAddmoney.setVisibility(View.VISIBLE);
                        btnAddmoney.setVisibility(View.VISIBLE);
                        btnAddmoney.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Topupamount=txtAddmoney.getText().toString();
                                new Payment.AddMoney().execute();
                            }
                        });


                    }

                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }

    }

    class AddMoney extends AsyncTask<Void, Integer, String> {


        protected void onPreExecute() {
            txtAddmoney.setVisibility(View.GONE);
            btnAddmoney.setVisibility(View.GONE);
            btnPay.setVisibility(View.GONE);
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
                URL url = new URL(ADD_MONEY_URL+"number=" + PhoneNumber+"&topupamt="+Topupamount+"&transid="+TransactionId);
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
                            //Wait 20 milliseconds
                            this.wait(20);
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
                dialog.dismiss();
                Toasty.error(Payment.this, "Null Response", Toast.LENGTH_SHORT, true).show();
            } else {

                try {
                    dialog.dismiss();

                    btnContinue.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.VISIBLE);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadDataWithBaseURL("", response, "text/html", "UTF-8", "");



                    btnContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toasty.success(Payment.this, "You can proceed to payment", Toast.LENGTH_SHORT, true).show();
                            webView.setVisibility(View.GONE);
                            btnContinue.setVisibility(View.GONE);
                            btnPay.setVisibility(View.VISIBLE);
                        }
                    });

                    btnPay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Payment.Debit().execute();
                        }
                    });




                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }

    }

    class Debit extends AsyncTask<Void, Integer, String> {


        protected void onPreExecute() {
            btnPay.setVisibility(View.GONE);
            btnContinue.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
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
                URL url = new URL(DEBIT_URL+"mode="+MODE+"&number=" + PhoneNumber+"&totalamount="+BillAmount+"&transid="+TransactionId);
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
                            //Wait 20 milliseconds
                            this.wait(20);
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
                dialog.dismiss();
                Toasty.error(Payment.this, "Null Response", Toast.LENGTH_SHORT, true).show();
            } else {

                try {
                    Log.i("response:", response);

                        dialog.dismiss();
                        Toasty.success(Payment.this, response, Toast.LENGTH_SHORT, true).show();

                        txtStatus.setVisibility(View.VISIBLE);
                        txtStatus.setText(response);

                        btnReturn.setVisibility(View.VISIBLE);
                        btnReturn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Payment.this,MainActivity.class);
                                startActivity(intent);
                            }
                        });



                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }

    }



}
