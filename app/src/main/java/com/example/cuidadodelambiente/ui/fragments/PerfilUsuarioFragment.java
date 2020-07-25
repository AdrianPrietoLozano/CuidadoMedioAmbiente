package com.example.cuidadodelambiente.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.ActualAmbientalista;
import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.models.UserLocalStore;

import java.util.Observable;
import java.util.Observer;

public class PerfilUsuarioFragment extends Fragment implements Observer {

    private static final String TAG = PerfilUsuarioFragment.class.getSimpleName();

    private TextView idUsuario;
    private TextView nombreUsuario;
    private TextView emailUsuario;
    private TextView puntosUsuario;
    private ProgressBar progressBar;
    private TextView cerrarSesion;
    private ImageView editarPerfil;

    public PerfilUsuarioFragment() {
        // Required empty public constructor
    }

    public static PerfilUsuarioFragment newInstance() {
        PerfilUsuarioFragment fragment = new PerfilUsuarioFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        // SUSCRIBIRSE A ActualAmbientalista
        UserLocalStore.getInstance(getContext()).addObserver(this);

        progressBar = v.findViewById(R.id.progressBar);
        editarPerfil = v.findViewById(R.id.editarPerfil);
        editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "falta por hacer", Toast.LENGTH_SHORT).show();
            }
        });
        cerrarSesion = v.findViewById(R.id.txtCerrarSesion);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "falta por hacer", Toast.LENGTH_SHORT).show();
            }
        });

        //idUsuario = v.findViewById(R.id.idUsuario);
        nombreUsuario = v.findViewById(R.id.nombreUsuario);
        emailUsuario = v.findViewById(R.id.emailUsuario);
        puntosUsuario = v.findViewById(R.id.puntosUsuario);

        User usuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado();
        mostrarDatosUsuario(usuario);


        // get instance de actualUser y establecer los valores

        return v;
    }

    private void mostrarDatosUsuario(User usuario) {
        //idUsuario.setText(String.valueOf(usuario.getId()));
        nombreUsuario.setText(usuario.getNombre());
        emailUsuario.setText(usuario.getEmail());

        if (usuario.getPuntos() == -1) {
            puntosUsuario.setText("ESPERANDO...");
        } else {
            puntosUsuario.setText(String.valueOf(usuario.getPuntos()));
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof User) {
            Toast.makeText(getContext(), "UPDATE USER", Toast.LENGTH_LONG).show();
            mostrarDatosUsuario((User) arg);
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        UserLocalStore.getInstance(getContext()).deleteObserver(this);
    }
}
