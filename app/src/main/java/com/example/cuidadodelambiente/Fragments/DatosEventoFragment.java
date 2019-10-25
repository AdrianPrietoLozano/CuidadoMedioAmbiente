package com.example.cuidadodelambiente.Fragments;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cuidadodelambiente.Entidades.EventoLimpieza;
import com.example.cuidadodelambiente.Entidades.VolleySingleton;
import com.example.cuidadodelambiente.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatosEventoFragment extends Fragment
        implements Response.Listener<JSONObject>, Response.ErrorListener{

    private int eventoId; // id del evento en la base de datos
    private TextView nombreEvento, fechaHora, creador, descripcion, numPersonasUnidas;
    private ImageView imagenEvento;
    private ProgressDialog progreso;
    private JsonObjectRequest jsonObjectRequest;

    public DatosEventoFragment() {
        // Required empty public constructor
    }

    private void iniciarPeticionBD()
    {
        String url = getString(R.string.ip) + "EventosLimpieza/datos_evento.php?evento_id="+eventoId;

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_datos_evento, container, false);
        nombreEvento = v.findViewById(R.id.nombreEvento);
        fechaHora = v.findViewById(R.id.fecha_hora_evento);
        creador = v.findViewById(R.id.creador_evento);
        descripcion = v.findViewById(R.id.descripcion_evento);
        numPersonasUnidas = v.findViewById(R.id.num_personas_unidas);
        imagenEvento = v.findViewById(R.id.imagenEvento);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        eventoId = getArguments().getInt("evento_id");

        iniciarPeticionBD();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progreso.hide();
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        progreso.hide();

        JSONArray json = response.optJSONArray("datos_evento");
        JSONObject jsonObject = null;
        EventoLimpieza eventoLimpieza = new EventoLimpieza();

        try {
            jsonObject = json.getJSONObject(0);

            eventoLimpieza.setRutaFotografia(jsonObject.optString("foto"));
            eventoLimpieza.setTitulo(jsonObject.optString("titulo"));
            eventoLimpieza.setFecha(jsonObject.optString("fecha"));
            eventoLimpieza.setHora(jsonObject.optString("hora"));
            eventoLimpieza.setAmbientalista(jsonObject.optString("creador"));
            eventoLimpieza.setDescripcion(jsonObject.optString("descripcion"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        nombreEvento.setText(eventoLimpieza.getTitulo());
        fechaHora.setText(String.format("%s, %s", eventoLimpieza.getFecha(),
                eventoLimpieza.getHora()));
        creador.setText(eventoLimpieza.getAmbientalista());
        descripcion.setText(eventoLimpieza.getDescripcion());

        String urlImagen = getString(R.string.ip) + "EventosLimpieza/imagenes/" + eventoLimpieza.getRutaFotografia();
        Toast.makeText(getContext(), urlImagen, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getinstance(getContext()).addToRequestQueue(imageRequest);
    }
}
