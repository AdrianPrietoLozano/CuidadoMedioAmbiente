package com.example.cuidadodelambiente.ui.fragments.admin_evento;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;


public class AdministrarEventoFragment extends Fragment {

    private RecyclerView recyclerUsuarios;
    private AdministrarEventoAdapter adapter;
    private TextView textSeleccionados;
    private CheckBox checkBoxSeleccionar;
    private Button botonAceptar;
    private Chip chipSeleccionarTodo;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;

    public AdministrarEventoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AdministrarEventoFragment newInstance() {
        AdministrarEventoFragment fragment = new AdministrarEventoFragment();
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
        View v = inflater.inflate(R.layout.fragment_admin_evento, container, false);

        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        toolbarTitle = v.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Selecciona los participantes");
        toolbarTitle.setTextColor(Color.WHITE);


        textSeleccionados = v.findViewById(R.id.textSeleccionados);
        chipSeleccionarTodo = v.findViewById(R.id.chipSeleccionarTodo);
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

        botonAceptar = v.findViewById(R.id.btnCompletado);
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (User user : adapter.getItemsSelected()) {
                    Log.e("DAFSD", String.valueOf(user.getId()));
                    Log.e("DAFSD", user.getNombre());
                }
            }
        });

        recyclerUsuarios = v.findViewById(R.id.recyclerUsuarios);
        recyclerUsuarios.setHasFixedSize(true);

        // Usar un administrador para Recycler
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerUsuarios.getContext(), DividerItemDecoration.HORIZONTAL);
        recyclerUsuarios.addItemDecoration(itemDecoration);

        ((MainActivity) getActivity()).cambiarVisibilidadBottomNavigation(View.GONE);

        intentarPeticionBD();

        return v;
    }


    private void intentarPeticionBD() {
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

        adapter = new AdministrarEventoAdapter(getContext(), usuarios, new AdministrarEventoAdapter.OnItemsSelectedCountListener() {
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

        /*
        int idUsuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado().getId();
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doGetEventosRecomendados(idUsuario).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    final List<User> eventos = response.body();
                    adapter = new AdministrarEventoAdapter(getContext(), eventos, new AdministrarEventoAdapter.OnItemsSelectedCountListener() {
                        @Override
                        public void onItemSelectedCountChange(int count) {
                            Log.e("TAD", String.valueOf(count));
                        }
                    });
                    recyclerEventos.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
         */
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            ((MainActivity) getActivity()).cambiarVisibilidadBottomNavigation(View.VISIBLE);
        } else {
            ((MainActivity) getActivity()).cambiarVisibilidadBottomNavigation(View.GONE);
        }
    }
}
