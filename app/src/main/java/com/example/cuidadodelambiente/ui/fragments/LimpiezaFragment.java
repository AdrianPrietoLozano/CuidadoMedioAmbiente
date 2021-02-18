package com.example.cuidadodelambiente.ui.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class LimpiezaFragment extends BottomSheetDialogFragment {

    private final int REQUEST_CODE_ELEGIR_FOTO = 10;
    private BottomSheetBehavior mBehavior;
    private ImageView fotoEvidencia;
    private ImageView iconElegirFoto;
    private int reporteId;
    private Uri uriImagen;
    private Button btnLimpiar;
    private TextInputEditText textDescripcion;
    private static ParaObservar observable = new ParaObservar();


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
                //iniciarLimpieza();
                Toast.makeText(getContext(), "Falta por hacer", Toast.LENGTH_SHORT).show();
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

    private void iniciarLimpieza() {
        RequestBody idUsuarioPart;
        RequestBody idReportePart;
        RequestBody descripcionPart;
        MultipartBody.Part imagenPart;

        int idUsuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado().getId();
        String descripcion = textDescripcion.getText().toString();

        idUsuarioPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idUsuario));
        idReportePart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(this.reporteId));

        if (!descripcion.equals("")) {
            descripcionPart = RequestBody.create(MediaType.parse("text/plain"), descripcion);
        } else {
            descripcionPart = null;
        }

        if (uriImagen != null) {
            File file = new File(Utilidades.getRealPathFromURI(uriImagen, getContext()));

            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            imagenPart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        } else {
            imagenPart = null;
        }


        final ProgressDialog progresoCrearLimpieza = new ProgressDialog(getContext());
        progresoCrearLimpieza.setTitle("Creando limpieza");
        progresoCrearLimpieza.setMessage("Cargando...");
        progresoCrearLimpieza.setCancelable(false);
        progresoCrearLimpieza.show();

        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<JsonObject> call = service.doAgregarLimpieza(idReportePart, descripcionPart, imagenPart);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progresoCrearLimpieza.dismiss();
                if (response.isSuccessful()) {
                    JsonObject json = response.body();
                    String mensaje = json.get("mensaje").getAsString();
                    int resultado = json.get("resultado").getAsInt();

                    if (resultado == 1) {
                        // notifica a los observers de que se creó una nueva limpieza.
                        // debería pasar como parámetro un objecto Limpieza
                        observable.notificar(null);
                        Toast.makeText(getContext(), "EXITO", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                progresoCrearLimpieza.dismiss();
            }
        });


    }
}
