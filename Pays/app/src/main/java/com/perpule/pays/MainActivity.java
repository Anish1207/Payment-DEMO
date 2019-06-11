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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity {


    private  static String MODE=null;
    protected ProgressDialog dialog;
    Button btnPaytm;
    EditText PhoneNumber;
    EditText Bill;
    private String transactionId;
    static final String CHECK_EXISTENCE_URL = "https://micro-s-perpule.appspot.com/checklinking?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPaytm =findViewById(R.id.btnPaytm);
        PhoneNumber =findViewById(R.id.EntPhoneNumber);
        Bill=findViewById(R.id.EntBill);

        btnPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //For PayTM
                MODE = "paytm";
                new MainActivity.CheckLinking().execute();
            }
        });

    }

    class CheckLinking extends AsyncTask<Void, Integer, String> {



        protected void onPreExecute() {
            super.onPreExecute();
            dialog = Utility.getProgressDialog(MainActivity.this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            if (dialog != null) {
                dialog.show();
            }

        }

        protected String doInBackground(Void... urls) {
            String Number = PhoneNumber.getText().toString();
            // TODO some validation here


            try {
                URL url = new URL(CHECK_EXISTENCE_URL + "mode=" + MODE + "&number=" + Number + "&totalamount="+Bill.getText().toString());
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
                    synchronized (this)
                    {
                        //Initialize an integer (that will act as a counter) to zero
                        int counter = 0;
                        //While the counter is smaller than four
                        while(counter <= 4)
                        {
                            //Wait 850 milliseconds
                            this.wait(20);
                            //Increment the counter
                            counter++;
                            //Set the current progress.
                            //This value is going to be passed to the onProgressUpdate() method.
                            publishProgress(counter*25);
                        }
                    }

                    //return response
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
                dialog.dismiss();
                Toasty.error(MainActivity.this, "null response!", Toast.LENGTH_SHORT, true).show();
            }
            else {

                TextView status=findViewById(R.id.txtValidate);
                try {
                    Log.i("response:",response);
                    if(response.trim().equalsIgnoreCase("false")) {
                        //dismiss loading dialogue
                        dialog.dismiss();

                        //starting new intent
                        Intent intent =new Intent(MainActivity.this, PaytmUserValidation.class);
                        Bundle extras= new Bundle();
                        extras.putString("number",PhoneNumber.getText().toString());
                        extras.putString("bill",Bill.getText().toString());
                        intent.putExtras(extras);
                        startActivity(intent);

                    }
                    else {
                        dialog.dismiss();
                        Toasty.success(MainActivity.this, "Your account is already linked with us!", Toast.LENGTH_SHORT, true).show();
                        //TODO directly to checksum;
                        transactionId=response.trim();
                        Intent intent =new Intent(MainActivity.this, Payment.class);
                        Bundle extras= new Bundle();
                        extras.putString("number",PhoneNumber.getText().toString());
                        extras.putString("bill",Bill.getText().toString());
                        extras.putString("transactionId", transactionId);
                        extras.putString("mode",MODE);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }


                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }


    }




}
