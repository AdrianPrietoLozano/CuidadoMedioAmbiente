package com.example.cuidadodelambiente.ui.activities.LogIn.view;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cuidadodelambiente.R;


public class LogInFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = LogInFragment.class.getSimpleName();

    private Button botonIniciarSesion;
    private TextView txtCrearUnaCuenta;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_in, container, false);

        botonIniciarSesion = v.findViewById(R.id.btnIniciarSesion);
        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "OnClick");
            }
        });

        txtCrearUnaCuenta = v.findViewById(R.id.crearUnaCuenta);
        txtCrearUnaCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActividadLogIn) getActivity()).cambiarASignUpFragment();
            }
        });


        return v;
    }

}
