package com.example.cuidadodelambiente.ui.fragments.participaciones;


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

import com.example.cuidadodelambiente.helpers.HelperCargaError;
import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.view.DatosEventoFragment;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParticipaEventosFragment extends Fragment
    implements Contract.View, Observer {

    private RecyclerView recyclerEventos;
    private Button botonVolverIntentar;
    private TextView mensajeProblema;
    private RecyclerView.LayoutManager lManager;
    private List<EventoLimpieza> listaEventos;
    private HelperCargaError helperCargaError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Contract.Presenter presenter;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;


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

        presenter = new ParticipacionesEventosPresenter(this);

        swipeRefreshLayout = v.findViewById(R.id.contenidoPrincipal);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        listaEventos.clear();
                        presenter.fetchEventos();
                    }
                }
        );

        helperCargaError = new HelperCargaError(swipeRefreshLayout,
                v.findViewById(R.id.pantallaCarga), v.findViewById(R.id.layoutSinConexion));

        recyclerEventos = v.findViewById(R.id.recyclerParticipaEventos);
        recyclerEventos.setHasFixedSize(true);

        // mensaje de error que se muestra cuando ocurre algun error
        mensajeProblema = v.findViewById(R.id.mensajeProblema);

        // evento clic para el boton volver a intentarlo cuando no hay conexion a internet
        botonVolverIntentar = v.findViewById(R.id.volverAIntentarlo);
        botonVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.fetchEventos();
            }
        });

        // Usar un administrador para Recycler
        lManager = new LinearLayoutManager(getContext());
        recyclerEventos.setLayoutManager(lManager);

        listaEventos = new ArrayList<>();

        // oculta el bottomnavigationview
        ((MainActivity) getActivity()).cambiarVisibilidadBottomNavigation(View.GONE);

        presenter.fetchEventos();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        presenter.fetchEventos();

    }

    @Override
    public void showLoading() {
        helperCargaError.mostrarPantallaCarga();
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showEventos(List<EventoLimpieza> eventos) {
        recyclerEventos.setAdapter(new ParticipacionesEventosAdapter(getContext(), eventos));
        helperCargaError.mostrarContenidoPrincipal();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(String error) {
        mensajeProblema.setText(error);
        helperCargaError.mostrarPantallaError();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showNoEventos() {
        showError("No participas en ningún evento");
    }
}