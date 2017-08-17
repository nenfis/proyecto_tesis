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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appssb.avisos.Firebase.FirebaseOrdenEntity;
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


public class EjecOrdenesFragment extends Fragment {
    private static final String TAG = EjecOrdenesFragment.class.getSimpleName();

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    EditText editFechaCierre, editReporte ;
    ListView listV_ordenes;
    TextView textId, textEstado,textPlanificador ,textEjecutor,textInstalacion,textFechaApertura, textDescripcion ;

    String user=FirebaseAuth.getInstance().getCurrentUser().getEmail();
    FirebaseDatabase firebaseRef;
    DatabaseReference databaseRef;

    ProgressDialog progressDoalog;

    private List<FirebaseOrdenEntity> listOrden=new ArrayList<FirebaseOrdenEntity>();
    private ArrayAdapter<FirebaseOrdenEntity> arrayAdapterFirebaseOrdenEntity;

    FirebaseOrdenEntity ordenEntitySeleccinada;

    Timer timer = new Timer();

    public EjecOrdenesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ejecordenes, container, false);
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
        textPlanificador=(TextView) view.findViewById(R.id.textPlanificador);
        textEjecutor=(TextView) view.findViewById(R.id.textEjecutor);
        textInstalacion=(TextView) view.findViewById(R.id.textInstalacion);
        textDescripcion=(TextView) view.findViewById(R.id.textDescripcion);
        editFechaCierre=(EditText) view.findViewById(R.id.editFechaCierrre);
        editReporte=(EditText) view.findViewById(R.id.editReporte);


        listV_ordenes=(ListView) view.findViewById(R.id.listV_Ordenes);
        firebaseRef=FirebaseDatabase.getInstance();
        databaseRef=firebaseRef.getReference();
        eventoData();

        listV_ordenes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ordenEntitySeleccinada=(FirebaseOrdenEntity)parent.getItemAtPosition(position);
                textId.setText(ordenEntitySeleccinada.getId());
                textEstado.setText(ordenEntitySeleccinada.getEstado());
                textPlanificador.setText(ordenEntitySeleccinada.getPlanificador());
                textEjecutor.setText(ordenEntitySeleccinada.getEjecutor());
                textInstalacion.setText(ordenEntitySeleccinada.getInstalacion());
                textFechaApertura.setText(ordenEntitySeleccinada.getDate());
                textDescripcion.setText(ordenEntitySeleccinada.getDescripcion());
                editFechaCierre.setText(ordenEntitySeleccinada.getDate2());
                editReporte.setText(ordenEntitySeleccinada.getReporte());
            }
        });

        textEstado.setText("Abierta");
        textFechaApertura.setText(sdf.format(new Date()));

        return view;
    }

    private void eventoData() {
        databaseRef.child("ordenes").orderByChild("ejecutor").equalTo(user).addValueEventListener(new ValueEventListener() {
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
        inflater.inflate(R.menu.view_ejecordenes, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        String readEstado=textEstado.getText().toString();
        String readFechaCierre=editFechaCierre.getText().toString();
        String readReporte=editReporte.getText().toString();

        if (id==R.id.menu_aceptar) {
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
                orden.setEstado("Ejecutandose");
                orden.setDate(textFechaApertura.getText().toString());
                orden.setPlanificador(textPlanificador.getText().toString());
                orden.setEjecutor(textEjecutor.getText().toString());
                orden.setInstalacion(textInstalacion.getText().toString());
                orden.setDescripcion(textDescripcion.getText().toString());
                orden.setDate2(editFechaCierre.getText().toString());
                orden.setReporte(editReporte.getText().toString());
                databaseRef.child("ordenes").child(orden.getId()).setValue(orden);
                Helper.displayMessageToast(getActivity(), "La actividad ha sido aceptada para trabajarse");
                sendPushaceptacion();
                limpiarcampos();
            }
        }
        if (id == R.id.menu_actualizar) {
            if (readEstado.contains("Abierta")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Asignada")) {
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
            if (readEstado.contains("Ejecutandose")) {
                if (TextUtils.isEmpty(readFechaCierre) || TextUtils.isEmpty(readReporte)) {
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
                    orden.setEstado("Atendida");
                    orden.setDate(textFechaApertura.getText().toString());
                    orden.setPlanificador(textPlanificador.getText().toString());
                    orden.setEjecutor(textEjecutor.getText().toString());
                    orden.setInstalacion(textInstalacion.getText().toString());
                    orden.setDescripcion(textDescripcion.getText().toString());
                    orden.setDate2(editFechaCierre.getText().toString());
                    orden.setReporte(editReporte.getText().toString());
                    databaseRef.child("ordenes").child(orden.getId()).setValue(orden);
                    Helper.displayMessageToast(getActivity(), "La actividad ha sido terminada y esta lista para regresarla a su planificador");
                    limpiarcampos();
                }
            }
        }
        if (id==R.id.menu_reportar) {
            if (readEstado.contains("Abierta")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Ejecutandose")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Reportada")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Asignada")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Cerrada")) {
                Helper.displayMessageToast(getActivity(), "Esta opcion no esta habilitada");
            }
            if (readEstado.contains("Atendida")) {
                if (TextUtils.isEmpty(readFechaCierre) || TextUtils.isEmpty(readReporte)) {
                    Helper.displayMessageToast(getActivity(), "Se deben ingresar los datos");
                } else {
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
                                    orden.setEstado("Reportada");
                                    orden.setDate(textFechaApertura.getText().toString());
                                    orden.setPlanificador(textPlanificador.getText().toString());
                                    orden.setEjecutor(textEjecutor.getText().toString());
                                    orden.setInstalacion(textInstalacion.getText().toString());
                                    orden.setDescripcion(textDescripcion.getText().toString());
                                    orden.setDate2(editFechaCierre.getText().toString());
                                    orden.setReporte(editReporte.getText().toString());
                                    databaseRef.child("ordenes").child(orden.getId()).setValue(orden);
                                    sendPushreportar();
                                    limpiarcampos();
                                    Helper.displayMessageToast(getActivity(), "Orden reportada");
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void limpiarcampos() {
        textId.setText("");
        textEstado.setText("Abierta");
        textFechaApertura.setText("");
        textPlanificador.setText("");
        textEjecutor.setText("");
        textInstalacion.setText("");
        textDescripcion.setText("");
        editFechaCierre.setText("");
        editReporte.setText("");

    }

    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDoalog.incrementProgressBy(1);
        }
    };



    private void sendPushaceptacion(){
        sendSinglePushaceptacion();
    }
    private void sendPushreportar(){
        sendSinglePushreporte();
    }

    private void sendSinglePushaceptacion(){
        final String title = "Orden Aceptada";
        final String message =  "Se ha aceptado una orden";
        final String email = textPlanificador.getText().toString();



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

    private void sendSinglePushreporte(){
        final String title = "Orden Reportada";
        final String message =  "Se ha terminado una orden";
        final String email = textPlanificador.getText().toString();



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
