package com.example.cuidadodelambiente.Dialogos;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.Entidades.ReporteContaminacion;
import com.example.cuidadodelambiente.Entidades.VolleySingleton;
import com.example.cuidadodelambiente.Fragments.CrearEventoFragment;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Time;


/* Diálogo que se muestra al hacer clic en un reporte de contaminación */
public class DialogClicReporte extends DialogFragment
    implements Response.Listener<JSONObject>, Response.ErrorListener{

    private TextView fechaHora, tipoResiduo, volumenResiduo, denunciante, descripcionReporte;
    private ImageView imagenReporte;
    private int reporteId;
    private Button botonCrearEvento;
    private Button botonCancelar;
    private ProgressDialog progreso;
    private JsonObjectRequest jsonObjectRequest;
    private ReporteContaminacion reporteContaminacion;


    public static DialogClicReporte newInstance(int num) {
        DialogClicReporte f = new DialogClicReporte();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("reporte_id", num);
        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        reporteId = getArguments().getInt("reporte_id");

        // representa al reporte que se esta mostrando en el dialog
        reporteContaminacion = new ReporteContaminacion();

        iniciarPeticionBD();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_clic_reporte, null);
        builder.setView(v);

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
                        reporteContaminacion.getId(), reporteContaminacion.getUbicacion().latitude,
                        reporteContaminacion.getUbicacion().longitude);

                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                        crearEventoFragment);
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

        return builder.create();
    }

    private void iniciarPeticionBD()
    {
        String url = getString(R.string.ip) + "EventosLimpieza/datos_reporte.php?reporte_id="+reporteId;

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getinstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        progreso.hide();
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        progreso.hide();

        JSONArray json = response.optJSONArray("datos_reporte");
        JSONObject jsonObject = null;

        try {
            jsonObject = json.getJSONObject(0);

            reporteContaminacion.setId(jsonObject.optInt("id"));
            reporteContaminacion.setFecha(jsonObject.optString("fecha"));
            reporteContaminacion.setHora(jsonObject.optString("hora"));
            reporteContaminacion.setDescripcion(jsonObject.optString("descripcion"));
            reporteContaminacion.setTipoResiduo(jsonObject.optString("tipo"));
            reporteContaminacion.setVolumenResiduo(jsonObject.optString("volumen"));
            reporteContaminacion.setAmbientalista(jsonObject.optString("nombre_usuario"));
            reporteContaminacion.setRutaFotografia(jsonObject.optString("foto"));
            reporteContaminacion.setUbicacion( new LatLng(jsonObject.optDouble("latitud"),
                                                        jsonObject.optDouble("longitud")));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        fechaHora.setText(reporteContaminacion.getFecha() + ", " +
                reporteContaminacion.getHora());
        tipoResiduo.setText(reporteContaminacion.getTipoResiduo());
        volumenResiduo.setText(reporteContaminacion.getVolumenResiduo());
        denunciante.setText(reporteContaminacion.getAmbientalista());
        descripcionReporte.setText(reporteContaminacion.getDescripcion());

        String urlImagen = getString(R.string.ip) + "EventosLimpieza/imagenes/" + reporteContaminacion.getRutaFotografia();
        iniciarCargaImagen(urlImagen);
    }

    private void iniciarCargaImagen(String urlImagen)
    {
        urlImagen.replace(" ", "%20"); // evitar errores con los espacios

        ImageRequest imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imagenReporte.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                imagenReporte.setImageResource(R.drawable.imagen_no_disponible);
            }
        });

        VolleySingleton.getinstance(getContext()).addToRequestQueue(imageRequest);
    }
}
