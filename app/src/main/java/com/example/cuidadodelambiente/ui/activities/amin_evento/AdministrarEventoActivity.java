package com.example.cuidadodelambiente.ui.activities.amin_evento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.helpers.HelperCargaError;
import com.google.android.gms.common.util.NumberUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdministrarEventoActivity extends AppCompatActivity {

    private RecyclerView recyclerUsuarios;
    private AdministrarEventoAdapter adapter;
    private TextView textSeleccionados;
    private CheckBox checkBoxSeleccionar;
    private Button botonAceptar;
    private Chip chipSeleccionarTodo;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;
    private HelperCargaError helperCargaError;
    private Button btnVolverIntentar;
    private TextView mensajeProblema;
    private Integer idEvento;
    private Integer idUsuario;
    private boolean usuarioParticipaEnEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrar_evento);

        // Cambia el color del status bar a verde
        Utilidades.cambiarColorStatusBar(getWindow(),
                ContextCompat.getColor(getApplicationContext(), R.color.verde3));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Elige los participantes");
        toolbarTitle.setTextColor(Color.WHITE);

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            Toast.makeText(this, "extras == null", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            idEvento = extras.getInt(Constants.EVENTO_ID);
        }

        idUsuario = UserLocalStore.getInstance(getApplicationContext()).getUsuarioLogueado().getId();

        helperCargaError = new HelperCargaError(findViewById(R.id.contenidoPrincipal),
                findViewById(R.id.pantallaCarga), findViewById(R.id.layoutSinConexion));
        helperCargaError.mostrarContenidoPrincipal();

        mensajeProblema = findViewById(R.id.mensajeProblema);
        btnVolverIntentar = findViewById(R.id.volverAIntentarlo);
        btnVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarPeticionBD();
            }
        });

        textSeleccionados = findViewById(R.id.textSeleccionados);
        chipSeleccionarTodo = findViewById(R.id.chipSeleccionarTodo);
        chipSeleccionarTodo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.selectAll(true);
                } else {
                    adapter.selectAll(false);
                }
            }
        });


        recyclerUsuarios = findViewById(R.id.recyclerUsuarios);
        recyclerUsuarios.setHasFixedSize(true);

        // Usar un administrador para Recycler
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerUsuarios.getContext(), DividerItemDecoration.HORIZONTAL);
        recyclerUsuarios.addItemDecoration(itemDecoration);

        botonAceptar = findViewById(R.id.btnCompletado);
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonAceptar.setEnabled(false);
                botonAceptar.setText("Cargando...");
                adapter.setClickable(false);
                enviarIdsUsuarios();
            }
        });

        intentarPeticionBD();
    }

    private void intentarPeticionBD() {
        helperCargaError.mostrarPantallaCarga();

        if (Utilidades.hayConexionInternet(getApplicationContext())) {
            iniciarPeticionBD();
        } else {
            helperCargaError.mostrarPantallaError();
        }
    }

    public void iniciarPeticionBD() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetParticipacionesEvento(idEvento).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful()) {
                    mensajeProblema.setText("Ocurrió un error");
                    helperCargaError.mostrarPantallaError();
                    return;
                }

                usuarioParticipaEnEvento = eliminarIdUsuario(response.body());

                adapter = new AdministrarEventoAdapter(getApplicationContext(), response.body(), new AdministrarEventoAdapter.OnItemsSelectedCountListener() {
                    @Override
                    public void onItemSelectedCountChange(int count) {
                        if (count == 1) {
                            textSeleccionados.setText("1 seleccionado");
                        } else {
                            textSeleccionados.setText(String.valueOf(count) + " seleccionados");
                        }
                        if (count > 0) {
                            botonAceptar.setEnabled(true);
                        } else {
                            botonAceptar.setEnabled(false);
                        }
                    }
                });

                recyclerUsuarios.setAdapter(adapter);
                helperCargaError.mostrarContenidoPrincipal();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                mensajeProblema.setText("Ocurrió un error");
                helperCargaError.mostrarPantallaError();
            }
        });
    }

    private boolean eliminarIdUsuario(List<User> usuarios) {
        Iterator itr = usuarios.iterator();
        while (itr.hasNext()) {
            User user = (User) itr.next();
            if (user.getId().equals(idUsuario)) {
                itr.remove();
                return true;
            }
        }

        return false;
    }

    private List<Integer> idsParticipantes() {
        List<Integer> idsUsuarios = new ArrayList<>();
        for (User user : adapter.getItemsSelected()) {
            idsUsuarios.add(user.getId());
        }

        if (usuarioParticipaEnEvento)
            idsUsuarios.add(idUsuario);

        return idsUsuarios;
    }

    private void enviarIdsUsuarios() {
        List<Integer> idsUsuarios = idsParticipantes();

        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doEnviarIdsParticipantes(idEvento, idUsuario, idsUsuarios).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "no successful", Toast.LENGTH_SHORT).show();
                    restablecerBotonAceptar();
                    return;
                }

                JsonObject res = response.body();
                if (res.get("resultado").getAsString().equals("0")) {
                    Toast.makeText(getApplicationContext(), res.get("mensaje").getAsString(), Toast.LENGTH_SHORT).show();
                    restablecerBotonAceptar();
                    return;
                }

                try {
                    Integer puntos = res.get("puntos").getAsInt();

                    User user = UserLocalStore.getInstance(getApplicationContext()).getUsuarioLogueado();
                    user.setPuntos(user.getPuntos() + puntos);
                    Log.e("PUNTOS", user.getPuntos().toString());
                    UserLocalStore.getInstance(getApplicationContext()).guardarUsuario(user);
                    Toast.makeText(getApplicationContext(), "Operación éxitosa. Agregados " + puntos + " puntos.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Problemas al agregar los puntos", Toast.LENGTH_SHORT).show();
                } finally {
                    finish();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
                restablecerBotonAceptar();
            }
        });
    }

    private void restablecerBotonAceptar() {
        botonAceptar.setEnabled(true);
        botonAceptar.setText("Completado");
        adapter.setClickable(true);
    }
}
