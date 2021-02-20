package com.example.cuidadodelambiente.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.DatosEventoFragment;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.view.RecomendacionesEventosAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscarEventosActivity extends AppCompatActivity {

    private RecyclerView recyclerEventos;
    private RecyclerView.LayoutManager lManager;
    private LinearLayout layoutMensaje;
    private TextView txtMensaje;
    private ProgressBar barraCarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_eventos);

        Utilidades.cambiarColorStatusBar(getWindow(),
                ContextCompat.getColor(getApplicationContext(), R.color.verde3));

        MaterialToolbar toolbar = findViewById(R.id.toolbarBuscar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layoutMensaje = findViewById(R.id.layoutMensaje);
        txtMensaje = findViewById(R.id.txtMensaje);
        barraCarga = findViewById(R.id.barraCargando);

        recyclerEventos = findViewById(R.id.recyclerEventos);
        lManager = new LinearLayoutManager(getApplicationContext());
        recyclerEventos.setLayoutManager(lManager);

        mostrarMensaje("Comienza a buscar", false);

        SearchView searchView = findViewById(R.id.barraBusqueda);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchEventos(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchEventos(newText);
                return false;
            }
        });

        searchView.setQueryHint("Buscar eventos");
        searchView.onActionViewExpanded();

    }

    private void fetchEventos(String query) {
        if (query.equals("")) {
            mostrarMensaje("Comienza a buscar", false);
            return;
        }

        mostrarMensaje("Buscando...", true);

        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doBusquedaEventos(query).enqueue(new Callback<List<EventoLimpieza>>() {
            @Override
            public void onResponse(Call<List<EventoLimpieza>> call, Response<List<EventoLimpieza>> response) {
                if (response.isSuccessful()) {
                    List<EventoLimpieza> eventos = response.body();
                    if (eventos.isEmpty()) {
                        mostrarMensaje("Sin resultados", false);
                        return;
                    }

                    recyclerEventos.setAdapter(new RecomendacionesEventosAdapter(getApplicationContext(), eventos, new RecomendacionesEventosAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Integer idEvento = eventos.get(position).getIdEvento();
                            BottomSheetDialogFragment fragmentDatosEvento = DatosEventoFragment.newInstance(idEvento);
                            fragmentDatosEvento.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                            fragmentDatosEvento.show(getSupportFragmentManager(), fragmentDatosEvento.getTag());
                        }
                    }));

                    mostrarResultados();

                } else {
                    Toast.makeText(getApplicationContext(), "no successfully", Toast.LENGTH_SHORT).show();
                    mostrarMensaje("Inténtalo de nuevo", false);
                }
            }

            @Override
            public void onFailure(Call<List<EventoLimpieza>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                mostrarMensaje("Inténtalo de nuevo", false);
            }
        });
    }

    private void mostrarMensaje(String msg, boolean mostrarCarga) {
        barraCarga.setVisibility(mostrarCarga ? View.VISIBLE : View.GONE);
        recyclerEventos.setVisibility(View.GONE);
        txtMensaje.setText(msg);
        layoutMensaje.setVisibility(View.VISIBLE);
    }

    private void mostrarResultados() {
        layoutMensaje.setVisibility(View.GONE);
        recyclerEventos.setVisibility(View.VISIBLE);
    }
}
