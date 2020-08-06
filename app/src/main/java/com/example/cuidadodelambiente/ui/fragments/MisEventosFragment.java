package com.example.cuidadodelambiente.ui.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.Fragments.CargandoCircular;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.EventoItem;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter.IRecomendacionesEventosPresenter;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.presenter.RecomendacionesEventosPresenter;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.view.RecomendacionesEventosAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MisEventosFragment extends Fragment {
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
                recyclerEventos.setAdapter(new MisEventosAdapter(getContext(),
                        listaEventos, new MisEventosAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.e(TAG, "CLIC");
                    }
                }));

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
}






