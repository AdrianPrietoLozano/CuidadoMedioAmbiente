package com.example.cuidadodelambiente.ui.fragments.limpiezas;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.ParaObservar;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LimpiezaFragment extends BottomSheetDialogFragment implements Contract.View {

    private final int REQUEST_CODE_ELEGIR_FOTO = 10;
    private BottomSheetBehavior mBehavior;
    private ImageView fotoEvidencia;
    private ImageView iconElegirFoto;
    private int reporteId;
    private Uri uriImagen;
    private Button btnLimpiar;
    private TextInputEditText textDescripcion;
    private ProgressDialog progressDialog;
    private static ParaObservar observable = new ParaObservar();
    private Contract.Presenter presenter;


    public LimpiezaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            reporteId = getArguments().getInt(Constants.REPORTE_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View v = View.inflate(getContext(), R.layout.bottom_sheet_limpieza, null);

        dialog.setContentView(v);
        mBehavior = BottomSheetBehavior.from((View) v.getParent());
        mBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);

        this.presenter = new LimpiezaPresenter(this);

        CardView cardFoto = v.findViewById(R.id.cardFoto);
        cardFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent, "Selecciona la fotografía"),
                        REQUEST_CODE_ELEGIR_FOTO);
            }
        });

        fotoEvidencia = v.findViewById(R.id.fotoEvidencia);
        iconElegirFoto = v.findViewById(R.id.iconElegirFoto);

        textDescripcion = v.findViewById(R.id.editTextDescripcion);

        btnLimpiar = v.findViewById(R.id.limpiar);
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriImagen != null) {
                    String descripcion = textDescripcion.getText().toString();
                    String urlFoto = Utilidades.getRealPathFromURI(uriImagen, getContext());

                    presenter.crearLimpieza(reporteId, descripcion, urlFoto);
                } else {
                    showMessage("Debes elegir una imagen");
                }
            }
        });

        cambiarColorStatusBar();

        return dialog;
    }

    public static LimpiezaFragment newInstance(int idReporte) {

        Bundle args = new Bundle();
        args.putInt(Constants.REPORTE_ID, idReporte);

        LimpiezaFragment fragment = new LimpiezaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ParaObservar getObservable() {
        return observable;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ELEGIR_FOTO && resultCode == Activity.RESULT_OK) {
            uriImagen = data.getData();
            iconElegirFoto.setVisibility(View.GONE);
            fotoEvidencia.setImageURI(uriImagen);
            fotoEvidencia.setVisibility(View.VISIBLE);
        }
    }

    private void cambiarColorStatusBar() {
        try {
            Utilidades.cambiarColorStatusBar(getDialog().getWindow(),
                    ContextCompat.getColor(getContext(), R.color.amarillo1));
        } catch (Exception e) {

        }
    }

    @Override
    public void showLoading() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Creando limpieza");
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }

    @Override
    public void showMessage(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLimpiezaCreada() {
        //Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        observable.notificar(null);
    }

    @Override
    public void close() {
        dismiss();
    }

    @Override
    public void onDestroyView() {
        Log.e("DESTROY", "DESTROY EN LIMPIEZA");
        presenter.detachView();
        super.onDestroyView();
    }
}
