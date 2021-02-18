package com.example.cuidadodelambiente.ui.fragments.mis_eventos_reportes;


import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
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

import com.example.cuidadodelambiente.helpers.HelperCargaError;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.activities.crear_reporte.view.ActividadCrearReporte;
import com.example.cuidadodelambiente.ui.fragments.DatosReporteFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MisReportesFragment extends Fragment implements Observer {
    public static final String TAG = MisEventosFragment.class.getSimpleName();

    private RecyclerView recyclerReportes;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private List<ReporteContaminacion> listaReportes;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HelperCargaError helperCargaError;
    private Button botonVolverIntentar;
    private TextView mensajeProblema;

    public MisReportesFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mis_reportes, container, false);

        // suscribirse al observable de ActividadCrearReporte
        ActividadCrearReporte.getObservable().addObserver(this);

        swipeRefreshLayout = v.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.e(TAG, "refresh");
                        listaReportes.clear();
                        intentarPeticionBD();
                    }
                }
        );

        helperCargaError = new HelperCargaError(swipeRefreshLayout,
                v.findViewById(R.id.pantallaCarga), v.findViewById(R.id.layoutSinConexion));
        helperCargaError.mostrarPantallaCarga();

        mensajeProblema = v.findViewById(R.id.mensajeProblema);

        // evento clic para el boton volver a intentarlo cuando no hay conexion a internet
        botonVolverIntentar = v.findViewById(R.id.volverAIntentarlo);
        botonVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarPeticionBD();
            }
        });

        recyclerReportes = v.findViewById(R.id.recyclerMisReportes);
        recyclerReportes.setHasFixedSize(true);

        lManager = new LinearLayoutManager(getContext());
        recyclerReportes.setLayoutManager(lManager);

        listaReportes = new ArrayList<>();

        intentarPeticionBD();

        return v;
    }

    private void intentarPeticionBD()
    {
        swipeRefreshLayout.setRefreshing(true);
        //layoutError.setVisibility(View.GONE);
        helperCargaError.mostrarPantallaCarga();

        // si hay internet
        if(Utilidades.hayConexionInternet(getContext())) {
            iniciarPeticionBD();
        }
        else { // no hay internet
            swipeRefreshLayout.setRefreshing(false);
            //layoutError.setVisibility(View.VISIBLE);
            helperCargaError.mostrarPantallaError();
        }
    }

    private void iniciarPeticionBD() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<List<ReporteContaminacion>> call = service.doGetReportesUsuario();
        call.enqueue(new Callback<List<ReporteContaminacion>>() {
            @Override
            public void onResponse(Call<List<ReporteContaminacion>> call, Response<List<ReporteContaminacion>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "No successful", Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                    mensajeProblema.setText("Ocurrió un error");
                    helperCargaError.mostrarPantallaError();
                    return;
                }

                listaReportes = response.body();

                adapter = new MisReportesAdapter(getContext(),
                        listaReportes, new MisReportesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Integer idReporte = listaReportes.get(position).getId();
                        if (idReporte == null) idReporte = -1;
                        BottomSheetDialogFragment fragmentReporte = DatosReporteFragment.newInstance(idReporte);
                        fragmentReporte.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                        fragmentReporte.show(getFragmentManager(), fragmentReporte.getTag());
                    }
                });

                recyclerReportes.setAdapter(adapter);

                swipeRefreshLayout.setRefreshing(false);
                helperCargaError.mostrarContenidoPrincipal();
            }

            @Override
            public void onFailure(Call<List<ReporteContaminacion>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
                mensajeProblema.setText("Ocurrió un error");
                helperCargaError.mostrarPantallaError();
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.e(TAG, "UPDATE MIS REPORTES FRAGMENT");

        // agrega un nuevo reporte al recycler view
        if (arg instanceof ReporteContaminacion) {
            if (adapter != null) {
                ReporteContaminacion nuevoReporte = (ReporteContaminacion) arg;
                listaReportes.add(nuevoReporte);
                adapter.notifyItemInserted(listaReportes.size() - 1);
            }
        }
    }
}






