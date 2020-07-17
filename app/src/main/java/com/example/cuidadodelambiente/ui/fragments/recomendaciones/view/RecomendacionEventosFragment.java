package com.example.cuidadodelambiente.ui.fragments.recomendaciones.view;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.Fragments.CargandoCircular;
import com.example.cuidadodelambiente.Fragments.RecyclerViewOnItemClickListener;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.EventoItem;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter.IRecomendacionesEventosPresenter;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter.RecomendacionesEventosPresenter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecomendacionEventosFragment extends Fragment
    implements IRecomendacionesEventosView{

    private JsonObjectRequest jsonObjectRequest;
    private RecyclerView recyclerEventos;
    private RecyclerView.Adapter adapter;
    private LinearLayout layoutSinConexion;
    private Button botonVolverIntentar;
    private TextView mensajeProblema;
    private RecyclerView.LayoutManager lManager;
    private List<EventoLimpieza> listaEventos;
    private CargandoCircular cargandoCircular;
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

        cargandoCircular = new CargandoCircular(swipeRefreshLayout,
                v.findViewById(R.id.pantallaCarga));
        cargandoCircular.ocultarContenidoMostrarCarga();

        recyclerEventos = v.findViewById(R.id.recyclerEventos);
        recyclerEventos.setHasFixedSize(true);

        // layout sin conexion
        layoutSinConexion = v.findViewById(R.id.layoutSinConexion);
        layoutSinConexion.setVisibility(View.INVISIBLE);

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
        cargandoCircular.ocultarContenidoMostrarCarga();

        // si hay internet
        if(Utilidades.hayConexionInternet(getContext())) {
            layoutSinConexion.setVisibility(View.INVISIBLE);
            //iniciarPeticionBD();
            presenter.cargarRecomendacionesEventos(DeclaracionFragments.actualAmbientalista);
        }
        else { // no hay internet
            cargandoCircular.ocultarCargaMostrarContenido();
            Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            layoutSinConexion.setVisibility(View.VISIBLE);
        }
    }

    private void iniciarPeticionBD() {

    }

    @Override
    public void onEventosCargadosExitosamente(final List<EventoItem> eventos) {
        recyclerEventos.setAdapter(new RecomendacionesEventosAdapter(getContext(), eventos, new RecomendacionesEventosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int idEvento = eventos.get(position).getId();
                BottomSheetDialogFragment fragmentDatosEvento = com.example.cuidadodelambiente.ui.fragments.DatosEventoFragment.newInstance(idEvento);
                fragmentDatosEvento.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                fragmentDatosEvento.show(getFragmentManager(), fragmentDatosEvento.getTag());
            }
        }));

        cargandoCircular.ocultarCargaMostrarContenido();
        swipeRefreshLayout.setRefreshing(false);
    }



    @Override
    public void onEventosCargadosError(Throwable t) {
        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
        mensajeProblema.setText(getString(R.string.estamos_teniendo_problemas));
        swipeRefreshLayout.setRefreshing(false);
        layoutSinConexion.setVisibility(View.VISIBLE);
        cargandoCircular.ocultarCargaMostrarContenido();
    }
}
