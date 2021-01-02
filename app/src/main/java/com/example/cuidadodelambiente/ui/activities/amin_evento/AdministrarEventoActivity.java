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
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.helpers.HelperCargaError;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
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

        botonAceptar = findViewById(R.id.btnCompletado);
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (User user : adapter.getItemsSelected()) {
                    Log.e("DAFSD", String.valueOf(user.getId()));
                    Log.e("DAFSD", user.getNombre());
                }
            }
        });

        recyclerUsuarios = findViewById(R.id.recyclerUsuarios);
        recyclerUsuarios.setHasFixedSize(true);

        // Usar un administrador para Recycler
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerUsuarios.getContext(), DividerItemDecoration.HORIZONTAL);
        recyclerUsuarios.addItemDecoration(itemDecoration);

        intentarPeticionBD();
    }

    private void intentarPeticionBD() {
        helperCargaError.mostrarPantallaCarga();

        if (Utilidades.hayConexionInternet(getApplicationContext())) {
            iniciarPeticionBD();
        } else {
            helperCargaError.mostrarPantallaError();
        }

        /*
        List<User> usuarios = new ArrayList<>();
        usuarios.add(new User(1, "adrian", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1,  "luis", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1,  "hector", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1, "adrian", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1,  "luis", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1,  "hector", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1, "adrian", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1,  "luis", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1,  "hector", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1, "adrian", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1,  "luis", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1,  "hector", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1, "adrian", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1,  "luis", "correo@gmai.com", 1, 1));
        usuarios.add(new User(1,  "hector", "correo@gmai.com", 1, 1));
        for (User user : usuarios)
            user.setFoto("imagenes/imagen1.jpg");

        adapter = new AdministrarEventoAdapter(getApplicationContext(), usuarios, new AdministrarEventoAdapter.OnItemsSelectedCountListener() {
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
        */
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
}
