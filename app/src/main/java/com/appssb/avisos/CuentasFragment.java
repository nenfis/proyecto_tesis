package com.appssb.avisos;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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
import java.util.Timer;
import java.util.TimerTask;


public class CuentasFragment extends Fragment {
    private static final String TAG = CuentasFragment.class.getSimpleName();


    EditText editCorreo;
    EditText editPass;
    String adminEmail;
    ListView listV_cuentas;


    FirebaseDatabase firebaseRef;
    DatabaseReference databaseRef;

    ProgressDialog progressDoalog;

    private List<FirebaseUserEntity> listUser=new ArrayList<FirebaseUserEntity>();
    private ArrayAdapter<FirebaseUserEntity> arrayAdapterFirebaseUserEntity;

    FirebaseUserEntity userEntitySeleccinada;

    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    Timer timer = new Timer();

    public CuentasFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cuentas, container, false);
        getActivity().setTitle("Avisos");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //La función que queremos ejecutar
                chequeo();
            }
        }, 0, 60000);

        editCorreo=(EditText) view.findViewById(R.id.editCorreo);
        editPass=(EditText) view.findViewById(R.id.editPass);
        listV_cuentas=(ListView) view.findViewById(R.id.listV_cuentas);
        firebaseRef=FirebaseDatabase.getInstance();
        databaseRef=firebaseRef.getReference();
        eventoData();

        listV_cuentas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userEntitySeleccinada=(FirebaseUserEntity)parent.getItemAtPosition(position);
                editCorreo.setText(userEntitySeleccinada.getEmail());
                editPass.setText(userEntitySeleccinada.getPass());

            }


        });

        return view;
    }

    private void eventoData() {
        databaseRef.child("users").orderByChild("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listUser.clear();
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    FirebaseUserEntity use=objSnapshot.getValue(FirebaseUserEntity.class);
                    listUser.add(use);
                }
                arrayAdapterFirebaseUserEntity=new ArrayAdapter<FirebaseUserEntity>(getActivity(),android.R.layout.simple_list_item_1,listUser);
                listV_cuentas.setAdapter(arrayAdapterFirebaseUserEntity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_cuentas, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        String enteredCorreo = editCorreo.getText().toString();
        final String enteredPass = editPass.getText().toString();
        adminEmail=firebaseAuth.getCurrentUser().getEmail();
        final String adminPass="123admin";
        if (id==R.id.menu_nuevo) {
            if (TextUtils.isEmpty(enteredCorreo) || TextUtils.isEmpty(enteredPass)) {
                Helper.displayMessageToast(getActivity(), "Se deben ingresar los datos");
            }
            else{

                firebaseAuth.createUserWithEmailAndPassword(enteredCorreo,enteredPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDoalog = new ProgressDialog(getActivity());
                            progressDoalog.setMax(100);
                            progressDoalog.setMessage("Creando registro....");
                            progressDoalog.setTitle("EJECUTANDO");
                            progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDoalog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        while (progressDoalog.getProgress() <= progressDoalog
                                                .getMax()) {
                                            Thread.sleep(10);
                                            handle.sendMessage(handle.obtainMessage());
                                            if (progressDoalog.getProgress() == progressDoalog
                                                    .getMax()) {

                                                progressDoalog.dismiss();

                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            FirebaseAuth.getInstance();
                            FirebaseUserEntity use = new FirebaseUserEntity();
                            use.setuId(firebaseAuth.getCurrentUser().getUid());
                            use.setEmail(firebaseAuth.getCurrentUser().getEmail());
                            use.setPass(enteredPass);
                            databaseRef.child("users").child(use.getuId()).setValue(use);

                            limpiarcampos();
                            firebaseAuth.signInWithEmailAndPassword(adminEmail,adminPass);
                        }else {
                            Helper.displayMessageToast(getActivity(), "Cuenta Existente, error de creación");
                        }


                    }
                });

            }
        }



        return super.onOptionsItemSelected(item);
    }

    private void limpiarcampos() {

        editCorreo.setText("");
        editPass.setText("");
    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(1);
        }
    };

    private void chequeo() {
        getActivity().runOnUiThread(conexion);
    }

    private Runnable conexion = new Runnable() {
        public void run() {
            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        Helper.displayMessageToast(getActivity(), "Esta conetado");
                    } else {
                        Helper.displayMessageToast(getActivity(), "Ha perdido la conexión");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Helper.displayMessageToast(getActivity(), "Imposible de detectar");
                }
            });
        }
    };
}
