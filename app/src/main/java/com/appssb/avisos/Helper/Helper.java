package com.appssb.avisos.Helper;


import android.content.Context;
import android.widget.Toast;

public class Helper {

    public static final String USER_KEY = "usuarios";



    public static void displayMessageToast(Context context, String displayMessage){
        Toast.makeText(context, displayMessage, Toast.LENGTH_LONG).show();
    }
}
