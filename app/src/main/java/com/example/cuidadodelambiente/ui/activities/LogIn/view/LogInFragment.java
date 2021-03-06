package com.example.cuidadodelambiente.ui.activities.LogIn.view;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LogInFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = LogInFragment.class.getSimpleName();

    private Button botonIniciarSesion;
    private TextView txtCrearUnaCuenta;
    private EditText emailEditText;
    private EditText contraseniaEditText;
    private boolean iniciandoSesion;
    private ProgressBar progressBar;
    private String fcmToken;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_in, container, false);

        progressBar = v.findViewById(R.id.progressBar);

        emailEditText = v.findViewById(R.id.editTextEmail);
        contraseniaEditText = v.findViewById(R.id.editTextContrasenia);

        botonIniciarSesion = v.findViewById(R.id.btnIniciarSesion);
        botonIniciarSesion.setOnClickListener(listenerLogIn);

        txtCrearUnaCuenta = v.findViewById(R.id.crearUnaCuenta);
        txtCrearUnaCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActividadLogIn) getActivity()).cambiarASignUpFragment();
            }
        });

        // obtenemos el FCM token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("TOKEN-FAILED", task.getException().toString());
                            return;
                        }

                        fcmToken = task.getResult().getToken();
                    }
                });

        iniciandoSesion = false;
        actualizarUI();

        return v;
    }

    private void usuarioAutentificado(JsonObject json) {
        int id = json.get("id").getAsInt();
        String token = json.get("token").getAsString();
        String nombre = json.get("nombre").getAsString();
        String email = json.get("email").getAsString();
        int puntos = json.get("puntos").getAsInt();

        User user = new User(id, token, nombre, email, puntos, User.USUARIO_GOOGLE);
        user.setTipoUsuario(User.USUARIO_NORMAL);

        // guarda al usuario
        UserLocalStore.getInstance(getContext()).guardarUsuario(user);
        UserLocalStore.getInstance(getContext()).setUsuarioLogueado(true);

        // inicia MainActivity
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void mostrarErrorEmailNoRegistrado() {
        emailEditText.setError("Email no registrado");
    }

    private void mostrarErrorContrasenia() {
        contraseniaEditText.setError("Contraseña incorrecta");
    }

    private boolean isEmailCorrecto(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void actualizarUI() {
        if (iniciandoSesion) {
            botonIniciarSesion.setEnabled(false);
            txtCrearUnaCuenta.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            botonIniciarSesion.setEnabled(true);
            txtCrearUnaCuenta.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener listenerLogIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = emailEditText.getText().toString();
            String contrasenia = contraseniaEditText.getText().toString();

            if (!isEmailCorrecto(email)) {
                emailEditText.setError("Correo inválido");
                return;
            }

            if (email.equals("")) {
                emailEditText.setError("Campo obligatorio");
                return;
            }

            if (contrasenia.equals("")) {
                contraseniaEditText.setError("Campo obligatorio");
                return;
            }

            iniciandoSesion = true;
            actualizarUI();

            APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
            Call<JsonObject> call = service.doLogIn(email, contrasenia, fcmToken);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject json = response.body();
                        int resultado = json.get("resultado").getAsInt();

                        switch (resultado) {
                            case 1:
                                usuarioAutentificado(json);
                                break;
                            case 2:
                                mostrarErrorEmailNoRegistrado();
                                break;
                            case 3:
                                //mostrarErrorContrasenia();
                                mostrarError(json.get("mensaje").getAsString());
                                break;

                            default:
                                mostrarError(json.get("mensaje").getAsString());
                                break;
                        }

                    } else {
                        mostrarError("Ocurrió un error");
                    }

                    iniciandoSesion = false;
                    actualizarUI();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mostrarError(t.getMessage());
                    iniciandoSesion = false;
                    actualizarUI();
                }
            });
        }
    };

}
