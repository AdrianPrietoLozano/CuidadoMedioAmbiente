package com.example.cuidadodelambiente.ui.fragments.multiselection;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.EventoItem;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.view.DatosEventoFragment;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.view.RecomendacionesEventosAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MultiSelectionFragment extends Fragment {

    private RecyclerView  recyclerEventos;
    private List<EventoLimpieza> listaEventos;
    private MultiSelectionAdapter adapter;

    public MultiSelectionFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MultiSelectionFragment newInstance() {
        MultiSelectionFragment fragment = new MultiSelectionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_multi_selection, container, false);

        Button b = v.findViewById(R.id.boton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.selectAll();
            }
        });

        recyclerEventos = v.findViewById(R.id.recyclerEventos);
        recyclerEventos.setHasFixedSize(true);

        // Usar un administrador para Recycler
        recyclerEventos.setLayoutManager(new LinearLayoutManager(getContext()));

        listaEventos = new ArrayList<>();
        intentarPeticionBD();

        return v;
    }


    private void intentarPeticionBD() {
        int idUsuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado().getId();
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetEventosRecomendados(idUsuario).enqueue(new Callback<List<EventoItem>>() {
            @Override
            public void onResponse(Call<List<EventoItem>> call, Response<List<EventoItem>> response) {
                if (response.isSuccessful()) {
                    final List<EventoItem> eventos = response.body();
                    adapter = new MultiSelectionAdapter(getContext(), eventos, new MultiSelectionAdapter.OnItemsSelectedCountListener() {
                        @Override
                        public void onItemSelectedCountChange(int count) {
                            Log.e("TAD", String.valueOf(count));
                        }
                    });
                    recyclerEventos.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<EventoItem>> call, Throwable t) {

            }
        });
    }
}
