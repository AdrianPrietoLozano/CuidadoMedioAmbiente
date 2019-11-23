package com.example.cuidadodelambiente.Fragments;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.Entidades.EventoLimpieza;
import com.example.cuidadodelambiente.Entidades.VolleySingleton;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatosEventoFragment extends Fragment
        implements Response.Listener<JSONObject>, Response.ErrorListener{

    private int eventoId; // id del evento en la base de datos
    private TextView nombreEvento, fechaHora, creador, descripcion, numPersonasUnidas, mensajeProblema;
    private Button botonQuieroParticipar;
    private Button botonVolverIntentar;
    private ImageView imagenEvento;
    private ProgressDialog progreso;
    private JsonObjectRequest jsonObjectRequest;
    private EventoLimpieza eventoLimpieza; // evento mostrado actualmente
    private CargandoCircular cargandoCircular;
    private LinearLayout layoutSinConexion;

    public DatosEventoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_datos_evento, container, false);

        // para la carga circular
        cargandoCircular = new CargandoCircular(v.findViewById(R.id.contenidoPrincipal),
                v.findViewById(R.id.pantallaCarga));
        cargandoCircular.ocultarContenidoMostrarCarga();

        // se muestra cuando no hay conexion
        layoutSinConexion = v.findViewById(R.id.layoutSinConexion);
        layoutSinConexion.setVisibility(View.INVISIBLE);

        mensajeProblema = v.findViewById(R.id.mensajeProblema);

        nombreEvento = v.findViewById(R.id.nombreEvento);
        fechaHora = v.findViewById(R.id.fecha_hora_evento);
        creador = v.findViewById(R.id.creador_evento);
        descripcion = v.findViewById(R.id.descripcion_evento);
        numPersonasUnidas = v.findViewById(R.id.num_personas_unidas);
        imagenEvento = v.findViewById(R.id.imagenEvento);
        botonQuieroParticipar = v.findViewById(R.id.botonQuieroParticipar);

        // evento clic boton "Quiero Participar"
        botonQuieroParticipar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicBotonQuieroParticipar();
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

        intentarPeticionBD();
        return v;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        eventoId = getArguments().getInt("evento_id");
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
        String url = getString(R.string.ip) + "EventosLimpieza/datos_evento.php?evento_id="+eventoId;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getinstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public static DatosEventoFragment newInstance(int eventoId) {
        DatosEventoFragment f = new DatosEventoFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("evento_id", eventoId);
        f.setArguments(args);

        return f;
    }




    private void clicBotonQuieroParticipar()
    {
        String url = getString(R.string.ip) + "EventosLimpieza/insertar_unirse_evento.php?" +
                "id_ambientalista=" + DeclaracionFragments.actualAmbientalista +
                "&id_evento=" + eventoId +
                "&fecha_inicio=" + eventoLimpieza.getFecha() +
                "&hora_inicio=" + eventoLimpieza.getHora() +
                "&fecha_fin=" + eventoLimpieza.getFecha() +
                "&hora_fin=" + eventoLimpieza.getHora();

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray json = null;
                        String resultado;
                        try {
                            json = response.getJSONArray("resultado");
                            resultado = json.getString(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            resultado = "0";
                        }

                        if(resultado.equals("1"))
                        {
                            Toast.makeText(getContext(), "Éxito", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
                        }

                        progreso.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progreso.hide();
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        VolleySingleton.getinstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }



    @Override
    public void onErrorResponse(VolleyError error) {
        mensajeProblema.setText(getString(R.string.estamos_teniendo_problemas));
        cargandoCircular.ocultarContenidoPrincipal();
        cargandoCircular.ocultarPantallaCarga();
        layoutSinConexion.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(JSONObject response) {

        JSONArray json = response.optJSONArray("datos_evento");
        JSONObject jsonObject = null;

        // limpia e inicializa un evento
        eventoLimpieza = null;
        eventoLimpieza = new EventoLimpieza();

        try {
            jsonObject = json.getJSONObject(0);

            eventoLimpieza.setRutaFotografia(jsonObject.optString("foto"));
            eventoLimpieza.setTitulo(jsonObject.optString("titulo"));
            eventoLimpieza.setFecha(jsonObject.optString("fecha"));
            eventoLimpieza.setHora(jsonObject.optString("hora"));
            eventoLimpieza.setAmbientalista(jsonObject.optString("creador"));
            eventoLimpieza.setDescripcion(jsonObject.optString("descripcion"));
            eventoLimpieza.setNumPersonasUnidas(response.optInt("personas_unidas"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        nombreEvento.setText(eventoLimpieza.getTitulo());
        fechaHora.setText(String.format("%s, %s", eventoLimpieza.getFecha(),
                eventoLimpieza.getHora()));
        creador.setText(eventoLimpieza.getAmbientalista());
        descripcion.setText(eventoLimpieza.getDescripcion());
        numPersonasUnidas.setText(String.format("%s %s",
                eventoLimpieza.getNumPersonasUnidas(), "personas unidas"));

        cargandoCircular.ocultarCargaMostrarContenido();

        String urlImagen = getString(R.string.ip) + "EventosLimpieza/imagenes/" + eventoLimpieza.getRutaFotografia();
        iniciarCargaImagen(urlImagen);
    }

    private void iniciarCargaImagen(String urlImagen)
    {
        urlImagen.replace(" ", "%20"); // evitar errores con los espacios

        ImageRequest imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imagenEvento.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imagenEvento.setImageResource(R.drawable.imagen_no_disponible);
            }
        });

        VolleySingleton.getinstance(getContext()).addToRequestQueue(imageRequest);
    }
}
