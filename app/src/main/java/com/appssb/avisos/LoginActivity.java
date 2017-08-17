package com.appssb.avisos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.appssb.avisos.Firebase.FirebaseUserEntity;
import com.appssb.avisos.Helper.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();




    ProgressDialog progressDoalog;

    private FirebaseAuth mAuth;


    TextView textEmail, textUsu, textTipo, textPass;
    EditText editPass;
    ListView listV_usuarioslogin;

    FirebaseDatabase firebaseRef;
    DatabaseReference databaseRef;

    private Button loginConectarse;
    private List<FirebaseUserEntity> listUser = new ArrayList<FirebaseUserEntity>();
    private ArrayAdapter<FirebaseUserEntity> arrayAdapterFirebaseUserEntity;

    FirebaseUserEntity userEntitySeleccinada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        textEmail = (TextView) findViewById(R.id.TextCorreo);
        textUsu = (TextView) findViewById(R.id.TextUsuName);
        textTipo = (TextView) findViewById(R.id.TextTipo);
        textPass = (TextView) findViewById(R.id.TextPass);
        editPass = (EditText) findViewById(R.id.TextPassConfir);
        loginConectarse = (Button) findViewById(R.id.login_button);
        listV_usuarioslogin = (ListView) findViewById(R.id.listV_usuarioslogin);
        mAuth = ((FirebaseApplication) getApplication()).getFirebaseAuth();


        SharedPreferences prefe=getSharedPreferences("datos", Context.MODE_PRIVATE);
        String correo=prefe.getString("correo","");
        String password=prefe.getString("contraseña","");
        String tipo=prefe.getString("tipo","");

        mAuth.signInWithEmailAndPassword("acceso@appavisos.com","123acceso").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseRef = FirebaseDatabase.getInstance();
                    databaseRef = firebaseRef.getReference();
                    eventoData();
                }

            }
        });




        listV_usuarioslogin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userEntitySeleccinada = (FirebaseUserEntity) parent.getItemAtPosition(position);
                textEmail.setText(userEntitySeleccinada.getEmail());
                textUsu.setText(userEntitySeleccinada.getName());
                textTipo.setText(userEntitySeleccinada.getTipo());
                textPass.setText(userEntitySeleccinada.getPass());
            }


        });


        if(correo.isEmpty()){
            loginConectarse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String emailSelec=textEmail.getText().toString().trim();
                    String useSelect=textUsu.getText().toString().trim();
                    String tipoSelect=textTipo.getText().toString().trim();
                    String contraSelect=textPass.getText().toString().trim();
                    String passIngres=editPass.getText().toString().trim();

                    if(emailSelec.isEmpty()){
                        Helper.displayMessageToast(LoginActivity.this, "Seleccione el usuario para ingresar");
                    }else{
                        if (TextUtils.equals(contraSelect,passIngres)){
                            progressDoalog = new ProgressDialog(LoginActivity.this);
                            progressDoalog.setMessage("Accesando....");
                            progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDoalog.show();
                            SharedPreferences preferencias=getSharedPreferences("datos",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=preferencias.edit();
                            editor.putString("correo", emailSelec);
                            editor.putString("contraseña", contraSelect);
                            editor.putString("tipo", tipoSelect);
                            editor.commit();
                            ((FirebaseApplication)getApplication()).loginAUser(LoginActivity.this, emailSelec, passIngres,tipoSelect);

                        }else{
                            Helper.displayMessageToast(LoginActivity.this, "La contraseña no coincide con la registrada");
                            editPass.setText("");
                        }
                    }




                }
            });
        }else{
            ((FirebaseApplication)getApplication()).loginAUser(LoginActivity.this, correo, password,tipo);
        }





}


    private void eventoData() {
        databaseRef.child("users").orderByChild("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listUser.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    FirebaseUserEntity use = objSnapshot.getValue(FirebaseUserEntity.class);
                    listUser.add(use);
                }
                arrayAdapterFirebaseUserEntity = new ArrayAdapter<FirebaseUserEntity>(LoginActivity.this, android.R.layout.simple_list_item_1, listUser);
                listV_usuarioslogin.setAdapter(arrayAdapterFirebaseUserEntity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }





}

