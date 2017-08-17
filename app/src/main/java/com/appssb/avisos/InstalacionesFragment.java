package com.appssb.avisos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.appssb.avisos.Firebase.FirebaseInstalacionEntity;
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
import java.util.UUID;


public class InstalacionesFragment extends Fragment {
    private static final String TAG = InstalacionesFragment.class.getSimpleName();


    EditText editInstalaciones;
    Spinner editSector;
    TextView textId;
    ListView listV_instalaciones;

    FirebaseDatabase firebaseRef;
    DatabaseReference databaseRef;

    ProgressDialog progressDoalog;

    private List<FirebaseInstalacionEntity> listInsta=new ArrayList<FirebaseInstalacionEntity>();
    private ArrayAdapter<FirebaseInstalacionEntity> arrayAdapterFirebaseInstalacionEntity;

    FirebaseInstalacionEntity instalacionEntitySeleccinada;

    Timer timer = new Timer();

    public InstalacionesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instalaciones, container, false);
        getActivity().setTitle("Avisos");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //La función que queremos ejecutar
                chequeo();
            }
        }, 0, 60000);

        String [] values =
                {"Seleccione un tipo","SUR","CENTRO","NORTE"};
        textId=(TextView) view.findViewById(R.id.TextId);
        editSector=(Spinner) view.findViewById(R.id.editSector);
        editInstalaciones=(EditText) view.findViewById(R.id.editInstalaciones);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        editSector.setAdapter(adapter);
        listV_instalaciones=(ListView) view.findViewById(R.id.listV_instalaciones);
        firebaseRef=FirebaseDatabase.getInstance();
        databaseRef=firebaseRef.getReference();
        eventoData();

        listV_instalaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                instalacionEntitySeleccinada=(FirebaseInstalacionEntity)parent.getItemAtPosition(position);
                textId.setText(instalacionEntitySeleccinada.getUid());
                editInstalaciones.setText(instalacionEntitySeleccinada.getInstalacion());

            }


        });

        return view;
    }

    private void eventoData() {
        databaseRef.child("instalaciones").orderByChild("sector").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listInsta.clear();
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    FirebaseInstalacionEntity insta=objSnapshot.getValue(FirebaseInstalacionEntity.class);
                    listInsta.add(insta);
                }
                arrayAdapterFirebaseInstalacionEntity=new ArrayAdapter<FirebaseInstalacionEntity>(getActivity(),android.R.layout.simple_list_item_1,listInsta);
                listV_instalaciones.setAdapter(arrayAdapterFirebaseInstalacionEntity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_instalaciones, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String enteredId = textId.toString();
        String enteredSector = editSector.getSelectedItem().toString();
        String enteredInstalacion = editInstalaciones.getText().toString();
        if (id == R.id.menu_nuevo) {
            if (enteredId.isEmpty()) {
                if (enteredSector == "Seleccione un tipo") {
                    Helper.displayMessageToast(getActivity(), "Seleccione el sector");
                }
                if (enteredInstalacion == "") {
                    Helper.displayMessageToast(getActivity(), "Ingrese el nombre de la instalación");
                }
                if (enteredSector != "Seleccione un tipo" && enteredInstalacion != "") {
                    progressDoalog = new ProgressDialog(getActivity());
                    progressDoalog.setMax(100);
                    progressDoalog.setMessage("Guardando registro....");
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
                    FirebaseInstalacionEntity insta = new FirebaseInstalacionEntity();
                    insta.setUid(UUID.randomUUID().toString());
                    insta.setSector(editSector.getSelectedItem().toString());
                    insta.setInstalacion(editInstalaciones.getText().toString());
                    databaseRef.child("instalaciones").child(insta.getUid()).setValue(insta);
                    limpiarcampos();
                }
            }
        }
        if (id == R.id.menu_actualizar) {
            if (enteredId != "") {
                Helper.displayMessageToast(getActivity(), "Debe seleccionar una instalacion para que se actualize");
            } else {
                progressDoalog = new ProgressDialog(getActivity());
                progressDoalog.setMax(100);
                progressDoalog.setMessage("Modificando registro....");
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
                FirebaseInstalacionEntity insta = new FirebaseInstalacionEntity();
                insta.setUid(instalacionEntitySeleccinada.getUid());
                insta.setSector(editSector.getSelectedItem().toString().trim());
                insta.setInstalacion(editInstalaciones.getText().toString().trim());
                databaseRef.child("instalaciones").child(insta.getUid()).setValue(insta);
                limpiarcampos();
            }
        }
        if(id==R.id.menu_eliminar) {
            if (enteredId != "") {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("¿Desea borrar la instalación?")
                        .setTitle("Borrar Instalación")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDoalog = new ProgressDialog(getActivity());
                                progressDoalog.setMax(100);
                                progressDoalog.setMessage("Eliminando registro....");
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
                                FirebaseInstalacionEntity insta = new FirebaseInstalacionEntity();
                                insta.setUid(instalacionEntitySeleccinada.getUid());
                                databaseRef.child("instalaciones").child(insta.getUid()).removeValue();
                                limpiarcampos();
                                Helper.displayMessageToast(getActivity(), "Instalación eliminada");
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Helper.displayMessageToast(getActivity(), "Accion cancelada");
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void limpiarcampos() {
        textId.setText("");
        editInstalaciones.setText("");
        String [] values =
                {"Seleccione un tipo","SUR","CENTRO","NORTE"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        editSector.setAdapter(adapter);
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
