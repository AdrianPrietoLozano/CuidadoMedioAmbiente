package com.example.cuidadodelambiente.ui.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.ui.activities.crear_evento.ActividadCrearEvento;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

public abstract class BaseActivity extends AppCompatActivity implements MvpView {

    private ProgressDialog progressDialog;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;

    public abstract void onProgressDialogCancel(DialogInterface dialog, int which);
    public abstract @LayoutRes int getLayout();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
    }

    public void initToolbar(String title, int bgColor, int textColor, @DrawableRes int arrowBack) {
        if (toolbar == null || toolbarTitle == null)
            return;

        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(bgColor);
        toolbar.setNavigationIcon(arrowBack);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbarTitle.setText(title);
        toolbarTitle.setTextColor(textColor);
    }

    @Override
    public boolean isNetworkConnected() {
        return Utilidades.hayConexionInternet(getApplicationContext());
    }

    @Override
    public void showLoading() {
        hideLoading();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Cargando");
        progressDialog.setMessage("Cargando");
        progressDialog.setCancelable(false);
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onProgressDialogCancel(dialog, which);
            }
        });
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

    @Override
    public void showError(String error) {
        showMessage(error != null ? error : "Error");
    }

    @Override
    public void hideError() {

    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
