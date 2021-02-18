package com.example.cuidadodelambiente.ui.fragments.participaciones.view;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.helpers.HelperCargaError;
import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.view.DatosEventoFragment;
import com.example.cuidadodelambiente.ui.fragments.participaciones.presenter.IParticipacionesEventosPresenter;
import com.example.cuidadodelambiente.ui.fragments.participaciones.presenter.ParticipacionesEventosPresenter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParticipaEventosFragment extends Fragment
    implements IParticipacionesEventosView, Observer {

    private JsonObjectRequest jsonObjectRequest;
    private RecyclerView recyclerEventos;
    private RecyclerView.Adapter adapter;
    //private RelativeLayout layoutSinConexion;
    private Button botonVolverIntentar;
    private TextView mensajeProblema;
    private RecyclerView.LayoutManager lManager;
    private List<EventoLimpieza> listaEventos;
    private HelperCargaError helperCargaError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private IParticipacionesEventosPresenter presenter;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;

    public ParticipaEventosFragment() {
        this.presenter = new ParticipacionesEventosPresenter(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_participa_eventos, container, false);

        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        toolbarTitle = v.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Eventos donde participas");
        toolbarTitle.setTextColor(Color.WHITE);

        DatosEventoFragment.getObservable().addObserver(this);

        swipeRefreshLayout = v.findViewById(R.id.contenidoPrincipal);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        listaEventos.clear();
                        intentarPeticionBD();
                    }
                }
        );

        helperCargaError = new HelperCargaError(swipeRefreshLayout,
                v.findViewById(R.id.pantallaCarga), v.findViewById(R.id.layoutSinConexion));
        //helperCargaError.ocultarContenidoMostrarCarga();
        helperCargaError.mostrarPantallaCarga();


        recyclerEventos = v.findViewById(R.id.recyclerParticipaEventos);
        recyclerEventos.setHasFixedSize(true);

        // layout sin conexion
        //layoutSinConexion = v.findViewById(R.id.layoutSinConexion);
        //layoutSinConexion.setVisibility(View.INVISIBLE);

        // mensaje de error que se muestra cuando ocurre algun error
        mensajeProblema = v.findViewById(R.id.mensajeProblema);

        // evento clic para el boton volver a intentarlo cuando no hay conexion a internet
        botonVolverIntentar = v.findViewById(R.id.volverAIntentarlo);
        botonVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarPeticionBD();
            }
        });

        // Usar un administrador para Recycler
        lManager = new LinearLayoutManager(getContext());
        recyclerEventos.setLayoutManager(lManager);

        listaEventos = new ArrayList<>();

        // oculta el bottomnavigationview
        ((MainActivity) getActivity()).cambiarVisibilidadBottomNavigation(View.GONE);

        intentarPeticionBD();

        return v;
    }

    public void intentarPeticionBD() {
        helperCargaError.mostrarPantallaCarga();

        // si hay internet
        if(Utilidades.hayConexionInternet(getContext())) {
            //layoutSinConexion.setVisibility(View.INVISIBLE);
            int idUsuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado().getId();
            this.presenter.cargarParticipacionesEventos();
        }
        else { // no hay internet
            //helperCargaError.ocultarCargaMostrarContenido();
            helperCargaError.mostrarPantallaError();
            Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            //layoutSinConexion.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onEventosCargadosExitosamente(List<EventoLimpieza> eventos) {
        if(eventos.size() == 0) {
            mensajeProblema.setText("No participas en ningún evento");
            //helperCargaError.ocultarPantallaCarga();
            //layoutSinConexion.setVisibility(View.VISIBLE);
            helperCargaError.mostrarPantallaError();

        } else {
            recyclerEventos.setAdapter(new ParticipacionesEventosAdapter(getContext(), eventos));
            //helperCargaError.ocultarCargaMostrarContenido();
            helperCargaError.mostrarContenidoPrincipal();
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onEventosCargadosError(Throwable t) {
        swipeRefreshLayout.setRefreshing(false);
        mensajeProblema.setText(R.string.estamos_teniendo_problemas);
        //layoutSinConexion.setVisibility(View.VISIBLE);
        //helperCargaError.ocultarCargaMostrarContenido();
        helperCargaError.mostrarPantallaError();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            ((MainActivity) getActivity()).cambiarVisibilidadBottomNavigation(View.VISIBLE);
        } else {
            ((MainActivity) getActivity()).cambiarVisibilidadBottomNavigation(View.GONE);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.e("PARTICIPACIONES", "UPDATE PARTICIPACIONES");
        intentarPeticionBD();

    }
}