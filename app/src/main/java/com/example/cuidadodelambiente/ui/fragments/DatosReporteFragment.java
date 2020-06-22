package com.example.cuidadodelambiente.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
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

import com.example.cuidadodelambiente.Fragments.CargandoCircular;
import com.example.cuidadodelambiente.Fragments.CrearEventoFragment;
import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;

public class DatosReporteFragment extends BottomSheetDialogFragment {

    private BottomSheetBehavior mBehavior;
    private TextView fechaHora, tipoResiduo, volumenResiduo, denunciante, descripcionReporte, mensajeProblema;
    private ImageView imagenReporte;
    private int reporteId;
    private Button botonCrearEvento;
    private Button botonCancelar;
    private Button botonVolverIntentar;
    private ProgressBar progreso;
    protected ReporteContaminacion reporteContaminacion;
    private CargandoCircular cargandoCircular;
    private LinearLayout layoutSinConexion;
    private LinearLayout contenidoPrincipal;

    public static DatosReporteFragment newInstance(int idReporte) {

        Bundle args = new Bundle();
        args.putInt("reporte_id", idReporte);

        DatosReporteFragment fragment = new DatosReporteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reporteId = getArguments().getInt("reporte_id");

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

        // para la carga circular
        cargandoCircular = new CargandoCircular(v.findViewById(R.id.contenidoPrincipal),
                v.findViewById(R.id.pantallaCarga));

        //cargandoCircular.ocultarContenidoMostrarCarga();
        progreso = v.findViewById(R.id.progressBar);

        // layout que se muestra cuando no hay conexion a internet
        //layoutSinConexion = v.findViewById(R.id.layoutSinConexion);
        //layoutSinConexion.setVisibility(View.INVISIBLE);

        //mensajeProblema = v.findViewById(R.id.mensajeProblema);
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
                CrearEventoFragment crearEventoFragment = CrearEventoFragment.newInstance(
                        reporteContaminacion.getId(), reporteContaminacion.getLatitud(),
                        reporteContaminacion.getLongitud());

                ((MainActivity)getActivity())
                        .cambiarFragment(crearEventoFragment, "CREAR");
                dismiss();
            }
        });

        botonCancelar = v.findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        /*
        // evento clic para el boton volver a intentarlo cuando no hay conexion a internet
        botonVolverIntentar = v.findViewById(R.id.volverAIntentarlo);
        botonVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarPeticionBD();
            }
        });
        */


            /*
            // evento clic del botón aceptar
            builder.setPositiveButton("Crear evento", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                            new CrearEventoFragment());
                }
            })

            // evento clic del botón cancelar
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            */

        iniciarPeticionBD();

        return dialog;
    }




    private void intentarPeticionBD()
    {
        //cargandoCircular.ocultarContenidoMostrarCarga();

        // si hay conexión a internet
        if(Utilidades.hayConexionInternet(getContext())) {
            //layoutSinConexion.setVisibility(View.INVISIBLE);
            iniciarPeticionBD();
        }
        else { // no hay conexión a internet
            //cargandoCircular.ocultarPantallaCarga();
            //cargandoCircular.ocultarContenidoPrincipal();
            //Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            //layoutSinConexion.setVisibility(View.VISIBLE);
        }
    }



    private void iniciarPeticionBD()
    {
        /*
        String url = getString(R.string.ip) + "EventosLimpieza/datos_reporte.php?reporte_id="+reporteId;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        jsonObjectRequest.setTag(this);
        VolleySingleton.getinstance(getContext()).addToRequestQueue(jsonObjectRequest);
         */


        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<ReporteContaminacion> call = service.doGetReporteContaminacion(this.reporteId);
        call.enqueue(new Callback<ReporteContaminacion>() {
            @Override
            public void onResponse(Call<ReporteContaminacion> call, retrofit2.Response<ReporteContaminacion> response) {

                Log.e("EXITO", "recibido");
                reporteContaminacion = response.body();

                fechaHora.setText(reporteContaminacion.getFecha() + ", " +
                        reporteContaminacion.getHora());
                tipoResiduo.setText(reporteContaminacion.getTipoResiduo());
                volumenResiduo.setText(reporteContaminacion.getVolumenResiduo());
                denunciante.setText(reporteContaminacion.getAmbientalista());
                descripcionReporte.setText(reporteContaminacion.getDescripcion());

                contenidoPrincipal.setVisibility(View.VISIBLE);
                progreso.setVisibility(View.GONE);

                //cargandoCircular.ocultarCargaMostrarContenido();

                String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() + "imagenes/" +
                        reporteContaminacion.getRutaFoto();
                Picasso.with(getContext()).load(urlFoto).into(imagenReporte);

            }

            @Override
            public void onFailure(Call<ReporteContaminacion> call, Throwable throwable) {
                call.cancel();
                Log.e("ERROR", throwable.getMessage());
                //mensajeProblema.setText(getString(R.string.estamos_teniendo_problemas));
                //cargandoCircular.ocultarContenidoPrincipal();
                //cargandoCircular.ocultarPantallaCarga();
                //layoutSinConexion.setVisibility(View.VISIBLE);

            }
        });
    }
}
