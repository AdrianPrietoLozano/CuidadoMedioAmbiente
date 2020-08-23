package com.example.cuidadodelambiente.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.ui.activities.LogIn.view.ActividadLogIn;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Observable;
import java.util.Observer;

public class PerfilUsuarioFragment extends Fragment implements Observer {

    private static final String TAG = PerfilUsuarioFragment.class.getSimpleName();

    private TextView textIdUsuario;
    private TextView textNombreUsuario;
    private TextView textEmailUsuario;
    private TextView textPuntosUsuario;
    private ProgressBar progressBar;
    private TextView textCerrarSesion;
    private ImageView imgEditarPerfil;
    private CardView cardEventosReportes;
    private CardView cardEventosParticipa;

    private GoogleSignInClient mGoogleSignInClient;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        // SUSCRIBIRSE
        UserLocalStore.getInstance(getContext()).addObserver(this);

        progressBar = v.findViewById(R.id.progressBar);
        imgEditarPerfil = v.findViewById(R.id.editarPerfil);
        imgEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "falta por hacer", Toast.LENGTH_SHORT).show();
            }
        });
        textCerrarSesion = v.findViewById(R.id.txtCerrarSesion);
        textCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tipoUsuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado().getTipoUsuario();

                limpiarDatosUsuario();

                if (tipoUsuario == User.USUARIO_GOOGLE) {
                    signOutGoogleUser();

                } else {
                    mostrarActividadLogIn();
                }
            }
        });

        //idUsuario = v.findViewById(R.id.idUsuario);
        textNombreUsuario = v.findViewById(R.id.nombreUsuario);
        textEmailUsuario = v.findViewById(R.id.emailUsuario);
        textPuntosUsuario = v.findViewById(R.id.puntosUsuario);

        cardEventosReportes = v.findViewById(R.id.cardMisEventosYReportes);
        cardEventosReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).cambiarFragment(DeclaracionFragments.misEventosYReportes, "GGG");
            }
        });

        cardEventosParticipa = v.findViewById(R.id.cardEventosParticipa);
        cardEventosParticipa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).cambiarFragment(DeclaracionFragments.participaEventos, "GGG");
            }
        });

        User usuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado();
        mostrarDatosUsuario(usuario);


        // get instance de actualUser y establecer los valores

        return v;
    }

    private void signOutGoogleUser() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mostrarActividadLogIn();
                    }
                });
    }

    private void limpiarDatosUsuario() {
        UserLocalStore.getInstance(getContext()).limpiarDatosUsuario();
        UserLocalStore.getInstance(getContext()).setUsuarioLogueado(false);
    }

    private void mostrarActividadLogIn() {
        startActivity(new Intent(getContext(), ActividadLogIn.class));
        getActivity().finish();
    }

    private void mostrarDatosUsuario(User usuario) {
        //idUsuario.setText(String.valueOf(usuario.getId()));
        textNombreUsuario.setText(usuario.getNombre());
        textEmailUsuario.setText(usuario.getEmail());

        if (usuario.getPuntos() == -1) {
            textPuntosUsuario.setText("-");
        } else {
            textPuntosUsuario.setText(String.valueOf(usuario.getPuntos()));
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
