package com.appssb.avisos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    TextView textEmailConectado, textUidConectado, textVerApp;

    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    private static final int REQUEST_READ_PERMISSION = 120;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        getActivity().setTitle("Avisos");

        textEmailConectado=(TextView) view.findViewById(R.id.textEmailConectado);
        textUidConectado=(TextView) view.findViewById(R.id.textUidConectado);
        textVerApp=(TextView) view.findViewById(R.id.textVerApp);

        textEmailConectado.setText(firebaseAuth.getCurrentUser().getEmail());
        textUidConectado.setText(firebaseAuth.getCurrentUser().getUid());
        textVerApp.setText(BuildConfig.VERSION_NAME);

        return view;
    }


}
