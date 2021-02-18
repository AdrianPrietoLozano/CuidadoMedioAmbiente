package com.example.cuidadodelambiente.ui.fragments.recomendaciones.view;


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
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.view.DatosEventoFragment;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter.IRecomendacionesEventosPresenter;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter.RecomendacionesEventosPresenter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecomendacionEventosFragment extends Fragment
    implements IRecomendacionesEventosView{

    private RecyclerView recyclerEventos;
    private RecyclerView.Adapter adapter;
    //private LinearLayout layoutSinConexion;
    private Button botonVolverIntentar;
    private TextView mensajeProblema;
    private RecyclerView.LayoutManager lManager;
    private List<EventoLimpieza> listaEventos;
    private HelperCargaError helperCargaError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private IRecomendacionesEventosPresenter presenter;


    public RecomendacionEventosFragment() {
        presenter = new RecomendacionesEventosPresenter(this);
    }

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
                        intentarPeticionBD();
                    }
                }
        );

        helperCargaError = new HelperCargaError(swipeRefreshLayout,
                v.findViewById(R.id.pantallaCarga), v.findViewById(R.id.layoutSinConexion));
        helperCargaError.mostrarPantallaCarga();

        recyclerEventos = v.findViewById(R.id.recyclerEventos);
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

        intentarPeticionBD();

        return v;
    }

    private void intentarPeticionBD()
    {
        helperCargaError.mostrarPantallaCarga();

        // si hay internet
        if(Utilidades.hayConexionInternet(getContext())) {
            //layoutSinConexion.setVisibility(View.INVISIBLE);
            //iniciarPeticionBD();
            presenter.cargarRecomendacionesEventos();
        }
        else { // no hay internet
            //helperCargaError.ocultarCargaMostrarContenido();
            helperCargaError.mostrarPantallaError();
            Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            //layoutSinConexion.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onEventosCargadosExitosamente(final List<EventoLimpieza> eventos) {
        recyclerEventos.setAdapter(new RecomendacionesEventosAdapter(getContext(), eventos, new RecomendacionesEventosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Integer idEvento = eventos.get(position).getIdEvento();
                BottomSheetDialogFragment fragmentDatosEvento = DatosEventoFragment.newInstance(idEvento);
                fragmentDatosEvento.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                fragmentDatosEvento.show(getFragmentManager(), fragmentDatosEvento.getTag());
            }
        }));

        //helperCargaError.ocultarCargaMostrarContenido();
        helperCargaError.mostrarContenidoPrincipal();
        swipeRefreshLayout.setRefreshing(false);
    }



    @Override
    public void onEventosCargadosError(Throwable t) {
        //Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
        mensajeProblema.setText(R.string.estamos_teniendo_problemas);
        swipeRefreshLayout.setRefreshing(false);
        //layoutSinConexion.setVisibility(View.VISIBLE);
        helperCargaError.mostrarPantallaError();
        //helperCargaError.ocultarCargaMostrarContenido();
    }
}
