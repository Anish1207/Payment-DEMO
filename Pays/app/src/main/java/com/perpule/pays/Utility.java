package com.perpule.pays;


import android.app.ProgressDialog;
import android.content.Context;

public class Utility {
    public static ProgressDialog getProgressDialog(Context context) {
            ProgressDialog progressDialog = new ProgressDialog(context,
                    R.style.TransparentDialog);
            progressDialog.setCancelable(false);
            progressDialog
                    .setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.setProgress(0);
            return progressDialog;
        }

}

