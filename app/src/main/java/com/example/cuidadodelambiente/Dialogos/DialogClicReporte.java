package com.example.cuidadodelambiente.Dialogos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.Entidades.VolleySingleton;
import com.example.cuidadodelambiente.Fragments.CargandoCircular;
import com.example.cuidadodelambiente.Fragments.CrearEventoFragment;
import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.Utilidades;
import com.squareup.picasso.Picasso;


import retrofit2.Call;
import retrofit2.Callback;


/* Diálogo que se muestra al hacer clic en un reporte de contaminación */
public class DialogClicReporte extends DialogFragment {

    private TextView fechaHora, tipoResiduo, volumenResiduo, denunciante, descripcionReporte, mensajeProblema;
    private ImageView imagenReporte;
    private int reporteId;
    private Button botonCrearEvento;
    private Button botonCancelar;
    private Button botonVolverIntentar;
    private ProgressDialog progreso;
    protected ReporteContaminacion reporteContaminacion;
    private CargandoCircular cargandoCircular;
    private LinearLayout layoutSinConexion;


    public static DialogClicReporte newInstance(int num) {
        DialogClicReporte f = new DialogClicReporte();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("reporte_id", num);
        f.setArguments(args);

        return f;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        //VolleySingleton.getinstance(getContext()).getRequestQueue().cancelAll(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        reporteId = getArguments().getInt("reporte_id");

        // representa al reporte que se esta mostrando en el dialog
        reporteContaminacion = new ReporteContaminacion();

        Log.e("ID", String.valueOf(reporteId));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_clic_reporte, null);
        builder.setView(v);

        // para la carga circular
        cargandoCircular = new CargandoCircular(v.findViewById(R.id.contenidoPrincipal),
                v.findViewById(R.id.pantallaCarga));
        cargandoCircular.ocultarContenidoMostrarCarga();

        // layout que se muestra cuando no hay conexion a internet
        layoutSinConexion = v.findViewById(R.id.layoutSinConexion);
        layoutSinConexion.setVisibility(View.INVISIBLE);

        mensajeProblema = v.findViewById(R.id.mensajeProblema);

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

        // evento clic para el boton volver a intentarlo cuando no hay conexion a internet
        botonVolverIntentar = v.findViewById(R.id.volverAIntentarlo);
        botonVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarPeticionBD();
            }
        });


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

        intentarPeticionBD();

        return builder.create();
    }

    private void intentarPeticionBD()
    {
        cargandoCircular.ocultarContenidoMostrarCarga();

        // si hay conexión a internet
        if(Utilidades.hayConexionInternet(getContext())) {
            layoutSinConexion.setVisibility(View.INVISIBLE);
            iniciarPeticionBD();
        }
        else { // no hay conexión a internet
            cargandoCircular.ocultarPantallaCarga();
            cargandoCircular.ocultarContenidoPrincipal();
            Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            layoutSinConexion.setVisibility(View.VISIBLE);
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

                reporteContaminacion = response.body();

                fechaHora.setText(reporteContaminacion.getFecha() + ", " +
                        reporteContaminacion.getHora());
                tipoResiduo.setText(reporteContaminacion.getTipoResiduo());
                volumenResiduo.setText(reporteContaminacion.getVolumenResiduo());
                denunciante.setText(reporteContaminacion.getAmbientalista());
                descripcionReporte.setText(reporteContaminacion.getDescripcion());

                cargandoCircular.ocultarCargaMostrarContenido();

                String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() + "imagenes/" +
                        reporteContaminacion.getRutaFoto();
                Picasso.with(getContext()).load(urlFoto).into(imagenReporte);

            }

            @Override
            public void onFailure(Call<ReporteContaminacion> call, Throwable throwable) {
                call.cancel();
                mensajeProblema.setText(getString(R.string.estamos_teniendo_problemas));
                cargandoCircular.ocultarContenidoPrincipal();
                cargandoCircular.ocultarPantallaCarga();
                layoutSinConexion.setVisibility(View.VISIBLE);
            }
        });
    }
}
