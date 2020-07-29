package com.example.cuidadodelambiente.ui.activities.LogIn.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUpFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = SignUpFragment.class.getSimpleName();

    private Button btnCrearCuenta;
    private TextView txtTengoCuenta;
    private EditText nombreEditText;
    private EditText emailEditText;
    private EditText contraseniaEditText;
    private EditText repiteContraseniaEditText;
    private LinearLayout rootLayout;
    private boolean creandoCuenta;
    private ProgressBar progressBar;

    public SignUpFragment() {
        // Required empty public constructor
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
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);

        rootLayout = v.findViewById(R.id.rootLayout);

        progressBar = v.findViewById(R.id.progressBar);

        nombreEditText = v.findViewById(R.id.editTextNombre);
        emailEditText = v.findViewById(R.id.editTextEmail);
        contraseniaEditText = v.findViewById(R.id.editTextContrasenia);
        repiteContraseniaEditText = v.findViewById(R.id.editTextRepiteContrasenia);

        btnCrearCuenta = v.findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarCrearCuenta();
            }
        });

        txtTengoCuenta = v.findViewById(R.id.yaTengoUnaCuenta);
        txtTengoCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActividadLogIn) getActivity()).cambiarALogInFragment();
            }
        });

        creandoCuenta = false;
        actualizarUI();

        return v;
    }

    private void mostrarErrorContraseniaDiferente() {
        contraseniaEditText.setError("Las contraseñas no coinciden.");
        repiteContraseniaEditText.setError("Las contraseñas no coinciden.");
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private boolean isEmailCorrecto() {
        String email = emailEditText.getText().toString();
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isContraseniaCorrecta() {
        return contraseniaEditText.getText().toString()
                .equals(repiteContraseniaEditText.getText().toString());
    }

    private boolean hayCamposVacios() {
        boolean vacios = false;

        for(int i = 0; i < rootLayout.getChildCount(); i++) {
            if (rootLayout.getChildAt(i) instanceof TextInputLayout) {
                EditText editText = ((TextInputLayout) rootLayout.getChildAt(i)).getEditText();
                if(editText.getText().toString().equals("")) {
                    vacios = true;
                    editText.setError("Campo obligatorio");
                }
            }
        }

        return vacios;
    }


    private void usuarioCreadoCorrectamente(JsonObject json) {
        int id = json.get("id").getAsInt();
        String nombre = json.get("nombre").getAsString();
        String email = json.get("email").getAsString();
        int puntos = json.get("puntos").getAsInt();

        User user = new User(id, nombre, email, puntos, User.USUARIO_GOOGLE);
        user.setTipoUsuario(User.USUARIO_NORMAL);

        // guarda al usuario
        UserLocalStore.getInstance(getContext()).guardarUsuario(user);
        UserLocalStore.getInstance(getContext()).setUsuarioLogueado(true);

        // inicia MainActivity
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    private void actualizarUI() {
        if (creandoCuenta) {
            btnCrearCuenta.setEnabled(false);
            txtTengoCuenta.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnCrearCuenta.setEnabled(true);
            txtTengoCuenta.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }


    private void iniciarCrearCuenta() {

        if (hayCamposVacios()) {
            return;
        }

        if (!isEmailCorrecto()) {
            emailEditText.setError("Correo inválido");
            return;
        }

        if (!isContraseniaCorrecta()) {
            mostrarErrorContraseniaDiferente();
            return;
        }

        creandoCuenta = true;
        actualizarUI();

        String nombre = nombreEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String contrasenia = contraseniaEditText.getText().toString();

        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<JsonObject> call = service.doSignUp(email, nombre, contrasenia);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject json = response.body();
                    int resultado = json.get("resultado").getAsInt();

                    switch (resultado) {
                        case 1:
                            usuarioCreadoCorrectamente(json);
                            break;
                        case 2:
                            emailEditText.setError("El correo ya está en uso.");
                            break;

                        default:
                            mostrarError(json.get("mensaje").getAsString());
                            break;
                    }

                } else {
                    mostrarError("Ocurrió un error");
                }

                creandoCuenta = false;
                actualizarUI();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mostrarError(t.getMessage());
                creandoCuenta = false;
                actualizarUI();
            }
        });
    }

}
