package com.appssb.avisos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appssb.avisos.Firebase.FirebaseInstalacionEntity;
import com.appssb.avisos.Firebase.FirebaseOrdenEntity;
import com.appssb.avisos.Firebase.FirebaseUserEntity;
import com.appssb.avisos.Helper.Helper;
import com.appssb.avisos.Mensajeria.EndPoints;
import com.appssb.avisos.Mensajeria.MyVolley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class PlaniOrdenesFragment extends Fragment {
    private static final String TAG = PlaniOrdenesFragment.class.getSimpleName();

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    EditText editDescripcion;
    ListView listV_ordenes;
    TextView textId, textEstado,textPlanificador ,textFechaApertura, textReporte, textFechaCierre;

    String user=FirebaseAuth.getInstance().getCurrentUser().getEmail();
    FirebaseDatabase firebaseRef;
    DatabaseReference databaseRef;

    ProgressDialog progressDoalog;

    private List<FirebaseOrdenEntity> listOrden=new ArrayList<FirebaseOrdenEntity>();
    private ArrayAdapter<FirebaseOrdenEntity> arrayAdapterFirebaseOrdenEntity;

    FirebaseOrdenEntity ordenEntitySeleccinada;


    private List<String> nomeConsulta = new ArrayList<String>();
    private ArrayAdapter<String> userdataAdapter;
    private DatabaseReference usermDatabaseReference;
    private Spinner usermSpinner;

    private List<String> instaConsulta = new ArrayList<String>();
    private ArrayAdapter<String> instadataAdapter;
    private DatabaseReference instamDatabaseReference;
    private Spinner instamSpinner;

    private String userSelected;
    private String instaSelected;

    Timer timer = new Timer();

    public PlaniOrdenesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planiordenes, container, false);
        getActivity().setTitle("Avisos");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //La función que queremos ejecutar
                chequeo();
            }
        }, 0, 60000);

        textId=(TextView) view.findViewById(R.id.textId);
        textEstado=(TextView) view.findViewById(R.id.textEstado);
        textFechaApertura=(TextView) view.findViewById(R.id.textFechaApertura);
        editDescripcion=(EditText) view.findViewById(R.id.editDescripcion);
        textFechaCierre=(TextView) view.findViewById(R.id.textFechaCierrre);
        textReporte=(TextView) view.findViewById(R.id.textReporte);
        textPlanificador=(TextView) view.findViewById(R.id.textPlanificador);

        listV_ordenes=(ListView) view.findViewById(R.id.listV_Ordenes);
        firebaseRef=FirebaseDatabase.getInstance();
        databaseRef=firebaseRef.getReference();
        eventoData();

        listV_ordenes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ordenEntitySeleccinada=(FirebaseOrdenEntity)parent.getItemAtPosition(position);
                userSelected=ordenEntitySeleccinada.getEjecutor();
                instaSelected=ordenEntitySeleccinada.getInstalacion();
                textId.setText(ordenEntitySeleccinada.getId());
                textEstado.setText(ordenEntitySeleccinada.getEstado());
                textPlanificador.setText(ordenEntitySeleccinada.getPlanificador());
                usermSpinner.setSelection(getIndex(usermSpinner, userSelected));
                instamSpinner.setSelection(getIndex(instamSpinner, instaSelected));
                textFechaApertura.setText(ordenEntitySeleccinada.getDate());
                editDescripcion.setText(ordenEntitySeleccinada.getDescripcion());
                textFechaCierre.setText(ordenEntitySeleccinada.getDate2());
                textReporte.setText(ordenEntitySeleccinada.getReporte());
            }
        });



        userdataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, nomeConsulta);
        userdataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usermSpinner = (Spinner)view.findViewById(R.id.spinnerCuenta);
        usermSpinner.setAdapter(userdataAdapter);
        cargarspinnerCuentas();

        instadataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, instaConsulta);
        instadataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instamSpinner = (Spinner)view.findViewById(R.id.spinnerInstalacion);
        instamSpinner.setAdapter(instadataAdapter);
        cargarspinnerInstalaciones();



            textEstado.setText("Abierta");
            textFechaApertura.setText(sdf.format(new Date()));
            colocarplanificador();

        return view;
    }

    private void eventoData() {
        databaseRef.child("ordenes").orderByChild("estado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listOrden.clear();
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    FirebaseOrdenEntity orde=objSnapshot.getValue(FirebaseOrdenEntity.class);
                    listOrden.add(orde);
                }
                arrayAdapterFirebaseOrdenEntity=new ArrayAdapter<FirebaseOrdenEntity>(getActivity(),android.R.layout.simple_list_item_1,listOrden);
                listV_ordenes.setAdapter(arrayAdapterFirebaseOrdenEntity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_planiordenes, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        String readEstado=textEstado.getText().toString();
        String enteredDescripcion = editDescripcion.getText().toString();
        if(id==R.id.menu_nuevaorden){
            limpiarcampos();
            Helper.displayMessageToast(getActivity(), "Listo para capturar nueva orden");
        }
        if (id==R.id.menu_nuevo) {
            if (readEstado.contains("Asignada")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Ejecutandose")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Atendida")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Reportada")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Cerrada")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Abierta")) {
                if (TextUtils.isEmpty(enteredDescripcion)) {
                    Helper.displayMessageToast(getActivity(), "Se deben ingresar los datos");
                } else {
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
                    FirebaseOrdenEntity orden = new FirebaseOrdenEntity();
                    orden.setId(UUID.randomUUID().toString());
                    orden.setEstado("Asignada");
                    orden.setDate(textFechaApertura.getText().toString());
                    orden.setPlanificador(textPlanificador.getText().toString());
                    orden.setEjecutor(usermSpinner.getSelectedItem().toString());
                    orden.setInstalacion(instamSpinner.getSelectedItem().toString());
                    orden.setDescripcion(editDescripcion.getText().toString());
                    orden.setDate2(textFechaCierre.getText().toString());
                    orden.setReporte(textReporte.getText().toString());
                    databaseRef.child("ordenes").child(orden.getId()).setValue(orden);
                    sendPush();
                    limpiarcampos();
                }
            }
        }
        if (id==R.id.menu_actualizar) {
            if (readEstado.contains("Abierta")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Ejecutandose")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Atendida")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Reportada")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Cerrada")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Asignada")) {
                if (TextUtils.isEmpty(enteredDescripcion)) {
                    Helper.displayMessageToast(getActivity(), "Se deben ingresar los datos");
                } else {
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
                    FirebaseOrdenEntity orden = new FirebaseOrdenEntity();
                    orden.setId(ordenEntitySeleccinada.getId());
                    orden.setEstado("Asignada");
                    orden.setDate(textFechaApertura.getText().toString());
                    orden.setPlanificador(textPlanificador.getText().toString());
                    orden.setEjecutor(usermSpinner.getSelectedItem().toString());
                    orden.setInstalacion(instamSpinner.getSelectedItem().toString());
                    orden.setDescripcion(editDescripcion.getText().toString());
                    orden.setDate2(textFechaCierre.getText().toString());
                    orden.setReporte(textReporte.getText().toString());
                    databaseRef.child("ordenes").child(orden.getId()).setValue(orden);
                    sendPush();
                    limpiarcampos();
                }
            }
        }
        if (id==R.id.menu_cerrar) {
            if (readEstado.contains("Abierta")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Ejecutandose")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Atendida")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Asignada")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Cerrada")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Reportada")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("¿Desea cerrar la orden?")
                        .setTitle("Cerrar Orden")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                                FirebaseOrdenEntity orden = new FirebaseOrdenEntity();
                                orden.setId(ordenEntitySeleccinada.getId());
                                orden.setEstado("Cerrada");
                                orden.setDate(textFechaApertura.getText().toString());
                                orden.setPlanificador(textPlanificador.getText().toString());
                                orden.setEjecutor(usermSpinner.getSelectedItem().toString());
                                orden.setInstalacion(instamSpinner.getSelectedItem().toString());
                                orden.setDescripcion(editDescripcion.getText().toString());
                                orden.setDate2(textFechaCierre.getText().toString());
                                orden.setReporte(textReporte.getText().toString());
                                databaseRef.child("ordenes").child(orden.getId()).setValue(orden);
                                limpiarcampos();
                                Helper.displayMessageToast(getActivity(), "Orden cerrada");
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
        textEstado.setText("Abierta");
        textFechaApertura.setText(sdf.format(new Date()));
        textPlanificador.setText(user);
        usermSpinner.getSelectedItem();
        instamSpinner.getSelectedItem();
        editDescripcion.setText("");
        textFechaCierre.setText("");
        textReporte.setText("");

    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(1);
        }
    };

    private void cargarspinnerCuentas(){


        usermDatabaseReference = FirebaseDatabase.getInstance().getReference();

        usermDatabaseReference.child("users").orderByChild("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    FirebaseUserEntity data = snapshot.getValue(FirebaseUserEntity.class);
                    nomeConsulta.add(data.getEmail());
                }
                userdataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void cargarspinnerInstalaciones(){


       instamDatabaseReference = FirebaseDatabase.getInstance().getReference();

        instamDatabaseReference.child("instalaciones").orderByChild("instalacion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    FirebaseInstalacionEntity data = snapshot.getValue(FirebaseInstalacionEntity.class);
                    instaConsulta.add(data.getInstalacion());
                }
                instadataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void colocarplanificador(){
        textPlanificador.setText(user);
    }

    private void sendPush(){
            sendSinglePush();
    }

    private void sendSinglePush(){
        final String title = "Orden Asignada";
        final String message =  "Se le ha asignado una nueva orden";
        final String email = usermSpinner.getSelectedItem().toString();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);
                params.put("email", email);
                return params;
            }
        };

        MyVolley.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

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
