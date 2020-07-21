package com.example.cuidadodelambiente.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActividadLogIn extends AppCompatActivity {

    private final int RC_SIGN_IN = 10;
    private final String TAG = ActividadLogIn.class.getSimpleName();

    private GoogleSignInClient mGoogleSignInClient;
    private Call<User> callLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_log_in);

        // solo para pruebas
        findViewById(R.id.btnSaltar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarMainActivity();
                finish();
            }
        });

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /*


        */
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account == null) {
            Toast.makeText(getApplicationContext(), "No log-in", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(getApplicationContext(), "log-in", Toast.LENGTH_SHORT).show();
            iniciarMainActivity();
            finish();
        }
        */

        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<GoogleSignInAccount>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {

                                try {
                                    GoogleSignInAccount account = task.getResult(ApiException.class);
                                    if (account == null)
                                        Toast.makeText(getApplicationContext(), "not log in", Toast.LENGTH_SHORT).show();
                                    else {
                                        Log.e(TAG, account.getEmail());
                                        Toast.makeText(getApplicationContext(), "log in", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (ApiException e) {
                                    Toast.makeText(getApplicationContext(), "not log in", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                );



        //updateUI(account);
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
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Toast.makeText(getApplicationContext(), "handle", Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), account.getIdToken(), Toast.LENGTH_LONG).show();

            APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
            Log.e(TAG, account.getIdToken());
            callLogIn = service.doVerificarGoogleUser(account.getIdToken());
            Toast.makeText(getApplicationContext(), "enviando id token", Toast.LENGTH_SHORT).show();
            callLogIn.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Toast.makeText(getApplicationContext(), "onResponse", Toast.LENGTH_SHORT).show();
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "successful", Toast.LENGTH_SHORT).show();
                        User user = response.body();
                        if (user.getResultado() == 1) {
                            Log.e(TAG, String.valueOf(user.getId()));
                            Log.e(TAG, user.getEmail());
                            Log.e(TAG, user.getNombre());
                            Log.e(TAG, String.valueOf(user.getPuntos()));
                        } else {
                            Log.e(TAG, user.getMensaje());
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });


            //iniciarMainActivity();
            //finish();

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            //updateUI(null);
        }
    }

    private void iniciarMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

}
