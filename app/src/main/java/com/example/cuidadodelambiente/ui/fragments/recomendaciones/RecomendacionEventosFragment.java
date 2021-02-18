package com.example.cuidadodelambiente.ui.fragments.recomendaciones;


import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.helpers.HelperCargaError;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.view.DatosEventoFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecomendacionEventosFragment extends Fragment
    implements Contract.View {

    private RecyclerView recyclerEventos;
    private RecyclerView.Adapter adapter;
    //private LinearLayout layoutSinConexion;
    private Button botonVolverIntentar;
    private TextView mensajeProblema;
    private RecyclerView.LayoutManager lManager;
    private List<EventoLimpieza> listaEventos;
    private HelperCargaError helperCargaError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Contract.Presenter presenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recomendacion_eventos, container, false);

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

        presenter = new RecomendacionesEventosPresenter(this);

        recyclerEventos = v.findViewById(R.id.recyclerEventos);
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

        presenter.fetchEventos();

        return v;
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
        recyclerEventos.setAdapter(new RecomendacionesEventosAdapter(getContext(), eventos, new RecomendacionesEventosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Integer idEvento = eventos.get(position).getIdEvento();
                BottomSheetDialogFragment fragmentDatosEvento = DatosEventoFragment.newInstance(idEvento);
                fragmentDatosEvento.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                fragmentDatosEvento.show(getFragmentManager(), fragmentDatosEvento.getTag());
            }
        }));

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
        // falta por hacer
        showError("Sin recomendaciones");
    }
}
