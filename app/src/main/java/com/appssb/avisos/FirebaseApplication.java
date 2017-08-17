package com.appssb.avisos;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.appssb.avisos.Helper.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseApplication extends Application {

    private static final String TAG = FirebaseApplication.class.getSimpleName();



    public FirebaseAuth firebaseAuth;

    public FirebaseAuth.AuthStateListener mAuthListener;

    public FirebaseAuth getFirebaseAuth(){
        return firebaseAuth = FirebaseAuth.getInstance();
    }

    public void loginAUser(final Context context, final String email, String password, final String tipo){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity)context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(tipo.contains("Administrador")){
                                Helper.displayMessageToast(context, "Bienvenido " + email);

                                Intent profileIntent = new Intent(context, AdminNavActivity.class);
                                context.startActivity(profileIntent);
                            }
                            if(tipo.contains("Planificador")){
                                Helper.displayMessageToast(context, "Bienvenido " + email);
                                Intent profileIntent = new Intent(context, PlanificadorNavActivity.class);
                                context.startActivity(profileIntent);
                            }
                            if(tipo.contains("Ejecutor")){
                                Helper.displayMessageToast(context, "Bienvenido " + email);
                                Intent profileIntent = new Intent(context, EjecutorNavActivity.class);
                                context.startActivity(profileIntent);
                            }
                        }
                    }
                });
    }

    public void loginOut(final Context context){
        Helper.displayMessageToast(context,"Hasta luego" + firebaseAuth.getCurrentUser().getEmail());
        firebaseAuth.signOut();
        Intent MainIntent=new Intent(context,MainActivity.class);
        context.startActivity(MainIntent);
    }


}
