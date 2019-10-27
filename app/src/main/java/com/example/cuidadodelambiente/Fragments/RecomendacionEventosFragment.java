package com.example.cuidadodelambiente.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.Entidades.EventoLimpieza;
import com.example.cuidadodelambiente.Entidades.VolleySingleton;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecomendacionEventosFragment extends Fragment
        implements Response.ErrorListener, Response.Listener<JSONObject> {

    private ProgressDialog progreso;
    private JsonObjectRequest jsonObjectRequest;

    private RecyclerView recyclerEventos;
    private RecyclerView.Adapter adapter;
    private LinearLayout layoutSinConexion;
    private Button botonVolverIntentar;
    private TextView mensajeProblema;
    private RecyclerView.LayoutManager lManager;
    private List<EventoLimpieza> listaEventos;

    public RecomendacionEventosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recomendacion_eventos, container, false);

        recyclerEventos = v.findViewById(R.id.recyclerEventos);
        recyclerEventos.setHasFixedSize(true);

        // layout sin conexion
        layoutSinConexion = v.findViewById(R.id.layoutSinConexion);
        layoutSinConexion.setVisibility(View.INVISIBLE);

        // mensaje de error que se muestra cuando ocurre algun error
        mensajeProblema = v.findViewById(R.id.mensajeProblema);

        // evento clic para el boton volver a intentarlo cuando no hay conexion a internet
        botonVolverIntentar = v.findViewById(R.id.volverAIntentarlo);
        botonVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarPeticionBD();
            }
        });

        // Usar un administrador para Recycler
        lManager = new LinearLayoutManager(getContext());
        recyclerEventos.setLayoutManager(lManager);

        listaEventos = new ArrayList<>();

        intentarPeticionBD();

        /*
        recyclerEventos.setOnClickListener(new AdapterView.OnClickListener() {

            @Override
            public void onClick(View v) {
                Fragment fragmentDatosEvento = DatosEventoFragment.newInstance((int)v.getTag());
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(), fragmentDatosEvento);
            }
        });*/

        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Fragment fragmentDatosEvento = DatosEventoFragment.newInstance((int)view.getTag());
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(), fragmentDatosEvento);
            }
        });
        */

        return v;
    }

    private void intentarPeticionBD()
    {
        ConnectivityManager con = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {
            layoutSinConexion.setVisibility(View.INVISIBLE);
            iniciarPeticionBD();
        }
        else {
            Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            layoutSinConexion.setVisibility(View.VISIBLE);
        }
    }

    private void iniciarPeticionBD() {
        String url = getString(R.string.ip) + "EventosLimpieza/eventos_recomendados.php?usuario_id=1";

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

        mensajeProblema.setText(getString(R.string.estamos_teniendo_problemas));
        layoutSinConexion.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray json = response.optJSONArray("eventos");

        JSONObject jsonObject;
        LatLng ubicacion;
        EventoLimpieza eventoLimpieza = null;

        try
        {
            for(int i = 0; i < json.length(); i++)
            {
                jsonObject = json.getJSONObject(i);
                eventoLimpieza = new EventoLimpieza();

                eventoLimpieza.setIdEvento(jsonObject.optInt("id_evento"));
                eventoLimpieza.setTitulo(jsonObject.optString("titulo"));
                eventoLimpieza.setFecha(jsonObject.optString("fecha"));
                eventoLimpieza.setHora(jsonObject.optString("hora"));
                eventoLimpieza.setRutaFotografia(jsonObject.optString("foto"));

                listaEventos.add(eventoLimpieza);

            }

            progreso.hide();
            adapter = new EventoAdapter(getContext(), listaEventos);
            recyclerEventos.setAdapter(adapter);

        } catch (JSONException e) {
            progreso.hide();
            e.printStackTrace();
        }
    }
}






class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder>
{
    private List<EventoLimpieza> listaEventos;
    private JsonObjectRequest jsonObjectRequest;
    Context context;


    public static class EventoViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        // campos de un elemento de la lista
        public ImageView imagenEvento;
        public ImageView imagenUbicacion;
        public TextView tituloEvento;
        public TextView fechaHoraEvento;

        public EventoViewHolder(View v)
        {
            super(v);
            imagenEvento = v.findViewById(R.id.imagenEvento);
            tituloEvento = v.findViewById(R.id.tituloEvento);
            fechaHoraEvento = v.findViewById(R.id.fechaHoraEvento);
        }

        @Override
        public void onClick(View view) {
            Fragment fragmentDatosEvento = DatosEventoFragment.newInstance((int)view.getTag());

        }
    }

    EventoAdapter(Context context, List<EventoLimpieza> listaEventos)
    {
        this.context = context;
        this.listaEventos = listaEventos;
    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_evento, parent, false);

        return new EventoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        holder.tituloEvento.setText(listaEventos.get(position).getTitulo());
        holder.fechaHoraEvento.setText(String.format("%s, %s",
                listaEventos.get(position).getFecha(),
                listaEventos.get(position).getHora()));

        String urlImagen = holder.itemView.getResources().getString(R.string.ip) +
                "EventosLimpieza/imagenes/" +
                listaEventos.get(position).getRutaFotografia();

        iniciarCargaImagen(urlImagen, holder);
    }


    private void iniciarCargaImagen(String urlImagen, final EventoViewHolder holder)
    {
        urlImagen.replace(" ", "%20"); // evitar errores con los espacios

        ImageRequest imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                // hacer la imagen redonda
                RoundedBitmapDrawable roundedDrawable =
                        RoundedBitmapDrawableFactory.create(holder.itemView.getResources(), response);
                roundedDrawable.setCornerRadius(response.getHeight());

                holder.imagenEvento.setImageDrawable(roundedDrawable);
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getinstance(context).addToRequestQueue(imageRequest);
    }
}
