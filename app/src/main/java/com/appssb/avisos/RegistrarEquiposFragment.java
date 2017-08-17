package com.appssb.avisos;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appssb.avisos.Helper.Helper;
import com.appssb.avisos.Mensajeria.EndPoints;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class RegistrarEquiposFragment extends Fragment {
    private static final String TAG = RegistrarEquiposFragment.class.getSimpleName();

    private static final int REQUEST_READ_PERMISSION = 120;

    private Button buttonRegister;
    private TextView editTextEmail;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    Timer timer = new Timer();

    public RegistrarEquiposFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrarequipo, container, false);
        getActivity().setTitle("Avisos");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //La función que queremos ejecutar
                chequeo();
            }
        }, 0, 60000);

        editTextEmail = (TextView) view.findViewById(R.id.registrarEmailDevice);


        firebaseAuth =firebaseAuth.getInstance();
        FirebaseUser user =firebaseAuth.getCurrentUser();
        //adding listener to view
        editTextEmail.setText(user.getEmail());

        return view;
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_equipo, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

            if(id==R.id.menu_registrarequipo) {
                sendTokenToServer();
            }


        return super.onOptionsItemSelected(item);
    }
    //storing token to mysql server
    private void sendTokenToServer() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Registrando Equipo...");
        progressDialog.show();
        final String token = FirebaseInstanceId.getInstance().getToken();
        final String email = editTextEmail.getText().toString();

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Token no generado", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("token", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
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
