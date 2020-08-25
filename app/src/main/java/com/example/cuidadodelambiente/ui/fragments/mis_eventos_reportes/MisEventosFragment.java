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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.activities.ActividadCrearEvento;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.view.DatosEventoFragment;
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
public class MisEventosFragment extends Fragment implements Observer {
    public static final String TAG = MisEventosFragment.class.getSimpleName();

    private RecyclerView recyclerEventos;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;
    private List<EventoLimpieza> listaEventos;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layoutError;
    private TextView textReintentar;

    public MisEventosFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mis_eventos, container, false);

        ActividadCrearEvento.getObservable().addObserver(this);

        layoutError = v.findViewById(R.id.layoutError);
        textReintentar = v.findViewById(R.id.textReintentar);
        textReintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarPeticionBD();
            }
        });

        swipeRefreshLayout = v.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.e(TAG, "refresh");
                        listaEventos.clear();
                        intentarPeticionBD();
                    }
                }
        );

        recyclerEventos = v.findViewById(R.id.recyclerMisEventos);
        recyclerEventos.setHasFixedSize(true);

        // Usar un administrador para Recycler
        lManager = new LinearLayoutManager(getContext());
        recyclerEventos.setLayoutManager(lManager);

        listaEventos = new ArrayList<>();

        intentarPeticionBD();

        return v;
    }

    private void intentarPeticionBD()
    {
        Log.e(TAG, "peticion");
        swipeRefreshLayout.setRefreshing(true);
        layoutError.setVisibility(View.GONE);

        // si hay internet
        if(Utilidades.hayConexionInternet(getContext())) {
            iniciarPeticionBD();
        }
        else { // no hay internet
            swipeRefreshLayout.setRefreshing(false);
            layoutError.setVisibility(View.VISIBLE);
        }
    }

    private void iniciarPeticionBD() {
        int idUsuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado().getId();
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<List<EventoLimpieza>> call = service.doGetEventosUsuario(idUsuario);
        call.enqueue(new Callback<List<EventoLimpieza>>() {
            @Override
            public void onResponse(Call<List<EventoLimpieza>> call, Response<List<EventoLimpieza>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "No successful", Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                    layoutError.setVisibility(View.VISIBLE);
                    return;
                }

                listaEventos = response.body();
                adapter = new MisEventosAdapter(getContext(),
                        listaEventos, new MisEventosAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        int idEvento = listaEventos.get(position).getIdEvento();
                        BottomSheetDialogFragment fragmentEvento = DatosEventoFragment.newInstance(idEvento);
                        fragmentEvento.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                        fragmentEvento.show(getFragmentManager(), fragmentEvento.getTag());
                    }
                });

                recyclerEventos.setAdapter(adapter);

                swipeRefreshLayout.setRefreshing(false);
                layoutError.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<EventoLimpieza>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
                layoutError.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.e(TAG, "UPDATE MIS EVENTOS FRAGMENT");

        if (arg instanceof EventoLimpieza) {
            if (adapter != null) {
                EventoLimpieza evento = (EventoLimpieza) arg;
                listaEventos.add(evento);
                adapter.notifyItemInserted(listaEventos.size() - 1);
            }
        }

    }
}






