package com.perpule.pays;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import es.dmoral.toasty.Toasty;

import com.perpule.pays.Utility;


public class MainActivity extends AppCompatActivity {


    protected ProgressDialog dialog;
    Button btnPaytm;
    EditText PhoneNumber;
    EditText Bill;
    static final String CHECK_VALIDATION_URL = "https://micro-s-perpule.appspot.com/checkexistence?number=";


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
                new MainActivity.CheckOTP().execute();
            }
        });

    }

    class CheckOTP extends AsyncTask<Void, Integer, String> {



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
                URL url = new URL(CHECK_VALIDATION_URL + Number);
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
                            this.wait(850);
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
                response = "THERE WAS AN ERROR";
                Toasty.error(MainActivity.this, "null response!", Toast.LENGTH_SHORT, true).show();
            }
            else {

                TextView status=findViewById(R.id.txtValidate);
                try {
                    Log.i("response:",response);
                    if(response.trim().equalsIgnoreCase("yes")) {
                        dialog.dismiss();
                        Toasty.success(MainActivity.this, "Your paytm account is already linked with us", Toast.LENGTH_SHORT, true).show();
                        //TODO directly to checksum;
                    }
                    else if (response.trim().equalsIgnoreCase("no")) {
                        //dismiss loading dialogue
                        dialog.dismiss();

                        //starting new intent
                        Intent intent =new Intent(MainActivity.this, UserValidationPaytm.class);
                        Bundle extras= new Bundle();
                        extras.putString("number",PhoneNumber.getText().toString());
                        extras.putString("bill",Bill.getText().toString());
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                    else{
                        dialog.dismiss();
                        Toasty.error(MainActivity.this, "Response:'"+response+"'", Toast.LENGTH_LONG, true).show();

                    }

                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        }


    }




}
