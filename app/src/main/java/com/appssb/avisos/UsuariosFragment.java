package com.appssb.avisos;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.appssb.avisos.Firebase.FirebaseUserEntity;
import com.appssb.avisos.Helper.Helper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class UsuariosFragment extends Fragment {
    private static final String TAG = UsuariosFragment.class.getSimpleName();



    EditText editNombre;
    Spinner editTipo;
    ListView listV_usuarios;
    TextView textUid, textEmail, textPass;

    FirebaseDatabase firebaseRef;
    DatabaseReference databaseRef;

    ProgressDialog progressDoalog;

    private List<FirebaseUserEntity> listUser=new ArrayList<FirebaseUserEntity>();
    private ArrayAdapter<FirebaseUserEntity> arrayAdapterFirebaseUserEntity;

    FirebaseUserEntity userEntitySeleccinada;

    Timer timer = new Timer();

    public UsuariosFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios, container, false);
        getActivity().setTitle("Avisos");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //La función que queremos ejecutar
                chequeo();
            }
        }, 0, 60000);

        String [] values =
                {"Seleccione un tipo","Administrador","Planificador","Ejecutor"};
        textUid=(TextView) view.findViewById(R.id.TextUid);
        textEmail=(TextView) view.findViewById(R.id.TextCorreo);
        textPass=(TextView) view.findViewById(R.id.TextPass);
        editNombre=(EditText) view.findViewById(R.id.editNombre);
        editTipo=(Spinner) view.findViewById(R.id.editTipo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        editTipo.setAdapter(adapter);
        listV_usuarios=(ListView) view.findViewById(R.id.listV_usuarios);
        firebaseRef=FirebaseDatabase.getInstance();
        databaseRef=firebaseRef.getReference();
        eventoData();

        listV_usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userEntitySeleccinada=(FirebaseUserEntity)parent.getItemAtPosition(position);
                textUid.setText(userEntitySeleccinada.getuId());
                textEmail.setText(userEntitySeleccinada.getEmail());
                textPass.setText(userEntitySeleccinada.getPass());
                editNombre.setText(userEntitySeleccinada.getName());



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
                listV_usuarios.setAdapter(arrayAdapterFirebaseUserEntity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_usuarios, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        String enteredNombre = editNombre.getText().toString();
        String enteredTipo = editTipo.getSelectedItem().toString();
        if(id==R.id.menu_actualizar){
            if(TextUtils.isEmpty(enteredNombre) || TextUtils.isEmpty(enteredTipo)){
                Helper.displayMessageToast(getActivity(), "Se deben seleccionar una cuenta para modificar");
            }
            else {
                if(enteredTipo=="Seleccione un tipo"){
                    Helper.displayMessageToast(getActivity(), "Debe seleccionar un tipo correcto");
                }else{
                    progressDoalog = new ProgressDialog(getActivity());
                    progressDoalog.setMax(100);
                    progressDoalog.setMessage("Actualizando registro....");
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
                FirebaseUserEntity use = new FirebaseUserEntity();
                use.setuId(userEntitySeleccinada.getuId());
                use.setEmail(textEmail.getText().toString().trim());
                use.setPass(textPass.getText().toString().trim());
                use.setName(editNombre.getText().toString().trim());
                use.setTipo(editTipo.getSelectedItem().toString().trim());
                databaseRef.child("users").child(use.getuId()).setValue(use);

                limpiarcampos();
                }
            }
        }



        return super.onOptionsItemSelected(item);
    }

    private void limpiarcampos() {
        textUid.setText("");
        textEmail.setText("");
        textPass.setText("");
        editNombre.setText("");
        String [] values =
                {"Seleccione un tipo","Administrador","Planificador","Ejecutor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        editTipo.setAdapter(adapter);
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
