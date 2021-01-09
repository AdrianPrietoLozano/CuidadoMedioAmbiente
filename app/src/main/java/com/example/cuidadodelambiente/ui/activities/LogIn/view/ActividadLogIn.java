package com.example.cuidadodelambiente.ui.activities.LogIn.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.ui.activities.ActividadCrearEvento;
import com.example.cuidadodelambiente.ui.activities.LogIn.presenter.ILogInPresenter;
import com.example.cuidadodelambiente.ui.activities.LogIn.presenter.LogInPresenter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class ActividadLogIn extends AppCompatActivity implements ILogInView {

    private final int RC_SIGN_IN = 10;
    private final String TAG = ActividadLogIn.class.getSimpleName();

    private LogInFragment logInFragment = new LogInFragment();
    private SignUpFragment signUpFragment = new SignUpFragment();
    private GoogleSignInClient mGoogleSignInClient;
    private ILogInPresenter presenter;
    private TextView txtTitulo;
    private ProgressDialog progressLogIn;
    private String fcmToken;

    public ActividadLogIn() {
        presenter = new LogInPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_log_in);

        txtTitulo = findViewById(R.id.titulo);

        Utilidades.cambiarColorStatusBar(getWindow(),
                ContextCompat.getColor(getApplicationContext(), R.color.verde3));

        cambiarALogInFragment();

        // Set the dimensions of the sign-in button.
        CardView googleSignIn = findViewById(R.id.google_sign_in);
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Falta", Toast.LENGTH_SHORT).show();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


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


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Log.e(TAG, "onCreate() saliendo");

    }

    private void cambiarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();
    }

    public void cambiarALogInFragment() {
        cambiarFragment(logInFragment);
        txtTitulo.setText("Inicio de sesión");

    }

    public void cambiarASignUpFragment() {
        cambiarFragment(signUpFragment);
        txtTitulo.setText("Crear una cuenta");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "onStart");

        UserLocalStore userLocalStore = UserLocalStore.getInstance(getApplicationContext());

        // si el usuario no esta logueado
        if (!userLocalStore.isUsuarioLogueado()) {
            return;
        }

        // el usuario ya esta logueado
        iniciarMainActivity();

        // si el usuario inicio sesión con su cuenta de google
        if (userLocalStore.getUsuarioLogueado().getTipoUsuario() == User.USUARIO_GOOGLE) {
            Log.e(TAG, "tipo GOOGLE");

            silentSignIn();

        } else { // el usuario inició sesión de forma normal
            Log.e(TAG, "tipo NORMAL");
            int idUsuario = userLocalStore.getUsuarioLogueado().getId();
            presenter.cargarDatosUsuarioNormal(idUsuario);
        }

        Log.e(TAG, "onStart() saliendo");

    }

    private void silentSignIn() {
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<GoogleSignInAccount>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                try {
                                    GoogleSignInAccount account = task.getResult(ApiException.class);
                                    if (account != null){
                                        // obtener los datos del usuario desde el servidor
                                        presenter.autentificarUsuarioGoogle(account.getIdToken(), fcmToken);
                                        Log.e(TAG, "MainActivity desde silentSignIn");
                                    }
                                    else {
                                        Toast.makeText(ActividadLogIn.this,
                                                "Ocurrió un error 1", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (ApiException e) {
                                    Log.e("LOGIN", e.toString());
                                    Toast.makeText(ActividadLogIn.this,
                                            "Ocurrió un error 2", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        progressLogIn = new ProgressDialog(ActividadLogIn.this);
        progressLogIn.setMessage("Cargando");
        progressLogIn.setCancelable(false);
        progressLogIn.show();

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            presenter.autentificarUsuarioGoogle(account.getIdToken(), fcmToken);

        } catch (ApiException e) {
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();

            progressLogIn.dismiss();
        }
    }

    private void iniciarMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public void cargarDatosUsuarioNormalError(String error) {
        Log.e(TAG, "error en Login");
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cargarDatosUsuarioNormalExito(User user) {
        Log.e(TAG, "exito desde LOGIN");
        UserLocalStore.getInstance(getApplicationContext()).guardarUsuario(user);
    }

    @Override
    public void autentificarUsuarioGoogleError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

        if (!UserLocalStore.getInstance(getApplicationContext()).isUsuarioLogueado()) {
            if (progressLogIn != null)
                progressLogIn.dismiss();
        }
    }

    @Override
    public void autentificarUsuarioGoogleExito(User user) {
        user.setTipoUsuario(User.USUARIO_GOOGLE);
        UserLocalStore.getInstance(getApplicationContext()).guardarUsuario(user);

        // si el usuario aun no esta logueado inicia la actividad principal
        if (!UserLocalStore.getInstance(getApplicationContext()).isUsuarioLogueado()) {
            if (progressLogIn != null)
                progressLogIn.dismiss();

            UserLocalStore.getInstance(getApplicationContext()).setUsuarioLogueado(true);
            iniciarMainActivity();
        }
    }
}
