package com.example.cuidadodelambiente.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.helpers.HelperCargaError;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.data.responses.ReporteContaminacionResponse;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;
import retrofit2.Callback;

public class DatosReporteFragment extends BottomSheetDialogFragment
    implements Observer {

    private BottomSheetBehavior mBehavior;
    private TextView fechaHora, tipoResiduo, volumenResiduo, denunciante, descripcionReporte, mensajeProblema;
    private ImageView imagenReporte;
    private Integer reporteId;
    private Button botonCrearEvento;
    private Button botonLimpiar;
    private ProgressBar barraCarga;
    private ReporteContaminacion reporteContaminacion;
    private HelperCargaError helperCargaError;
    private LinearLayout layoutNoConexion;
    private Button botonVolverIntentar;
    private LinearLayout contenidoPrincipal;

    private Call<ReporteContaminacionResponse> callDatosReporte;

    public static DatosReporteFragment newInstance(Integer idReporte) {

        Bundle args = new Bundle();

        if (idReporte == null) idReporte = -1; // para que no truene en caso de un error
        args.putInt(Constants.REPORTE_ID, idReporte);

        DatosReporteFragment fragment = new DatosReporteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reporteId = getArguments().getInt(Constants.REPORTE_ID);

        Log.e("ID", String.valueOf(reporteId));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View v = View.inflate(getContext(), R.layout.bottom_sheet_reportes, null);

        dialog.setContentView(v);
        mBehavior = BottomSheetBehavior.from((View) v.getParent());
        mBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);

        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        dismiss();
                        break;

                     case BottomSheetBehavior.STATE_DRAGGING:
                         Log.e("DRAGGING", "DRAGGIMG");
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("DRAGGING", "SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        layoutNoConexion = v.findViewById(R.id.layoutNoConexion);
        botonVolverIntentar = v.findViewById(R.id.btnVolverIntentarlo);
        botonVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarPeticionBD();
            }
        });

        barraCarga = v.findViewById(R.id.progressBar);

        contenidoPrincipal = v.findViewById(R.id.contenidoPrincipal);
        contenidoPrincipal.setVisibility(View.INVISIBLE);

        fechaHora = v.findViewById(R.id.fecha_hora_reporte);
        tipoResiduo = v.findViewById(R.id.tipo_residuo);
        volumenResiduo = v.findViewById(R.id.volumen_residuo);
        denunciante = v.findViewById(R.id.ambientalista_denunciante);
        descripcionReporte = v.findViewById(R.id.descripcion_reporte);
        imagenReporte = v.findViewById(R.id.imagenReporte);

        botonCrearEvento = v.findViewById(R.id.botonCrearEvento);
        botonCrearEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("REPORTE", String.valueOf(reporteContaminacion.getId()));

                Intent intent = new Intent(getContext(), DeclaracionFragments.crearEventoActivity.getClass());
                intent.putExtra("ID_REPORTE", reporteContaminacion.getId());
                intent.putExtra("LATITUD", reporteContaminacion.getLatitud());
                intent.putExtra("LONGITUD", reporteContaminacion.getLongitud());

                startActivity(intent);

                dismiss();
            }
        });

        botonLimpiar = v.findViewById(R.id.botonLimpiar);
        botonLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarCrearLimpieza();
            }
        });

        intentarPeticionBD();

        return dialog;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (callDatosReporte != null) {
            callDatosReporte.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LimpiezaFragment.getObservable().deleteObserver(this);
    }

    private void iniciarCrearLimpieza() {
        // se suscribe al observable de LimpiezaFragment
        // de este modo podremos saber cuando se cree una nueva limpieza
        LimpiezaFragment.getObservable().addObserver(this);

        BottomSheetDialogFragment fragmentLimpieza = LimpiezaFragment.newInstance(reporteId);
        fragmentLimpieza.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        fragmentLimpieza.show(getFragmentManager(), fragmentLimpieza.getTag());
    }


    private void intentarPeticionBD()
    {
        mostrarCarga();

        // si hay conexión a internet
        if(Utilidades.hayConexionInternet(getContext())) {
            iniciarPeticionBD();
        }
        else { // no hay conexión a internet
            mostrarLayoutError();
            //helperCargaError.ocultarPantallaCarga();
            //helperCargaError.ocultarContenidoPrincipal();
            //Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            //layoutSinConexion.setVisibility(View.VISIBLE);
        }
    }



    private void iniciarPeticionBD()
    {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        callDatosReporte = service.doGetReporteContaminacion(this.reporteId);
        callDatosReporte.enqueue(new Callback<ReporteContaminacionResponse>() {
            @Override
            public void onResponse(Call<ReporteContaminacionResponse> call, retrofit2.Response<ReporteContaminacionResponse> response) {

                if (!response.isSuccessful()) {
                    mostrarLayoutError();
                    return;
                }

                if (response.body().getStatus().getResultado() == 1) {
                    reporteContaminacion = response.body().getReporte();

                    fechaHora.setText(reporteContaminacion.getFecha() + ", " +
                            reporteContaminacion.getHora());
                    tipoResiduo.setText(reporteContaminacion.getResiduos().toString());
                    volumenResiduo.setText(reporteContaminacion.getVolumenResiduo());
                    denunciante.setText(reporteContaminacion.getAmbientalista());
                    descripcionReporte.setText(reporteContaminacion.getDescripcion());

                    if (reporteContaminacion.getTieneEvento() || reporteContaminacion.getTieneLimpieza()) {
                        botonCrearEvento.setEnabled(false);
                        botonLimpiar.setEnabled(false);
                    } else {
                        botonCrearEvento.setEnabled(true);
                        botonLimpiar.setEnabled(true);
                    }

                    mostrarContenidoPrincipal();

                    String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() +
                            reporteContaminacion.getRutaFoto();
                    Picasso.with(getContext()).load(urlFoto).into(imagenReporte);

                } else {
                    Toast.makeText(getContext(),
                            response.body().getStatus().getMensaje(), Toast.LENGTH_SHORT).show();
                    mostrarLayoutError();
                }
            }

            @Override
            public void onFailure(Call<ReporteContaminacionResponse> call, Throwable throwable) {
                Log.e("ERROR", throwable.getMessage());
                mostrarLayoutError();

            }
        });
    }

    private void mostrarCarga() {
        barraCarga.setVisibility(View.VISIBLE);
        layoutNoConexion.setVisibility(View.GONE);
        contenidoPrincipal.setVisibility(View.GONE);
    }

    private void mostrarContenidoPrincipal() {
        contenidoPrincipal.setVisibility(View.VISIBLE);
        barraCarga.setVisibility(View.GONE);
        layoutNoConexion.setVisibility(View.GONE);
    }

    private void mostrarLayoutError() {
        layoutNoConexion.setVisibility(View.VISIBLE);
        barraCarga.setVisibility(View.GONE);
        contenidoPrincipal.setVisibility(View.GONE);
    }

    // se ejecuta cuando se crea una limpieza
    @Override
    public void update(Observable o, Object arg) {
        botonLimpiar.setEnabled(false);
        botonCrearEvento.setEnabled(false);
    }
}
