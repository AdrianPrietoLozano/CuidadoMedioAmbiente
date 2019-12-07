package com.example.cuidadodelambiente.Fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.Entidades.EventoLimpieza;
import com.example.cuidadodelambiente.Entidades.VolleySingleton;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParticipaEventos extends Fragment
        implements Response.ErrorListener, Response.Listener<JSONObject> {

    private JsonObjectRequest jsonObjectRequest;
    private RecyclerView recyclerEventos;
    private RecyclerView.Adapter adapter;
    private LinearLayout layoutSinConexion;
    private Button botonVolverIntentar;
    private TextView mensajeProblema;
    private RecyclerView.LayoutManager lManager;
    private List<EventoLimpieza> listaEventos;
    private CargandoCircular cargandoCircular;
    private SwipeRefreshLayout swipeRefreshLayout;


    public ParticipaEventos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_participa_eventos, container, false);
        // Inflate the layout for this fragment


        swipeRefreshLayout = v.findViewById(R.id.contenidoPrincipal);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        listaEventos.clear();
                        intentarPeticionBD();
                    }
                }
        );

        cargandoCircular = new CargandoCircular(swipeRefreshLayout,
                v.findViewById(R.id.pantallaCarga));
        cargandoCircular.ocultarContenidoMostrarCarga();


        recyclerEventos = v.findViewById(R.id.recyclerParticipaEventos);
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

        return v;
    }

    public void intentarPeticionBD() {
        cargandoCircular.ocultarContenidoMostrarCarga();

        // si hay internet
        if(Utilidades.hayConexionInternet(getContext())) {
            layoutSinConexion.setVisibility(View.INVISIBLE);
            iniciarPeticionBD();
        }
        else { // no hay internet
            cargandoCircular.ocultarCargaMostrarContenido();
            Toast.makeText(getContext(), getString(R.string.sin_internet), Toast.LENGTH_SHORT).show();
            layoutSinConexion.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void iniciarPeticionBD() {
        String url = getString(R.string.ip) + "EventosLimpieza/datos_participacion_evento.php?id_ambientalista="+
                DeclaracionFragments.actualAmbientalista;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getinstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

        swipeRefreshLayout.setRefreshing(false);
        mensajeProblema.setText(getString(R.string.estamos_teniendo_problemas));
        layoutSinConexion.setVisibility(View.VISIBLE);

        cargandoCircular.ocultarCargaMostrarContenido();

    }

    public void recargar()
    {
        listaEventos.clear();
        swipeRefreshLayout.setRefreshing(true);
        intentarPeticionBD();
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray json = response.optJSONArray("datos");

        JSONObject jsonObject;
        EventoLimpieza eventoLimpieza = null;

        try
        {
            if(json.length() == 0) // no participa en ningun evento
            {
                mensajeProblema.setText("No participas en ningún evento");
                layoutSinConexion.setVisibility(View.VISIBLE);
            }
            else {
                for (int i = 0; i < json.length(); i++) {
                    jsonObject = json.getJSONObject(i);
                    eventoLimpieza = new EventoLimpieza();

                    //eventoLimpieza.setIdEvento(jsonObject.optInt("id_evento"));
                    eventoLimpieza.setTitulo(jsonObject.optString("titulo"));
                    eventoLimpieza.setFecha(jsonObject.optString("fecha"));
                    eventoLimpieza.setHora(jsonObject.optString("hora"));
                    eventoLimpieza.setRutaFotografia(jsonObject.optString("foto"));
                    eventoLimpieza.setAmbientalista(jsonObject.optString("creador"));
                    eventoLimpieza.setDescripcion(jsonObject.optString("descripcion"));
                    eventoLimpieza.setTipoResiduo(jsonObject.optString("tipo"));

                    listaEventos.add(eventoLimpieza);
                }
                recyclerEventos.setAdapter(new ParticipaEventoAdapter(getContext(), listaEventos));
            }
            cargandoCircular.ocultarCargaMostrarContenido();
            swipeRefreshLayout.setRefreshing(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
















class ParticipaEventoAdapter extends RecyclerView.Adapter<ParticipaEventoAdapter.ParticipaEventoViewHolder>
{
    private List<EventoLimpieza> listaEventos;
    private JsonObjectRequest jsonObjectRequest;
    private Context context;

    ParticipaEventoAdapter(Context context, List<EventoLimpieza> listaEventos)
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
    public ParticipaEventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_participa_evento, parent, false);
        final ParticipaEventoViewHolder participaEventoViewHolder = new ParticipaEventoViewHolder(v);

        return participaEventoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipaEventoViewHolder holder, int position) {
        holder.tituloEvento.setText(listaEventos.get(position).getTitulo());
        holder.fechaHoraEvento.setText(String.format("%s, %s",
                listaEventos.get(position).getFecha(),
                listaEventos.get(position).getHora()));
        holder.creador.setText("Creador: " + listaEventos.get(position).getAmbientalista());
        holder.tipoResiduo.setText(listaEventos.get(position).getTipoResiduo());
        holder.descripcion.setText(listaEventos.get(position).getDescripcion());


        if(holder.imagenEvento.getTag().toString() == "NO")
        {
            String urlImagen = holder.itemView.getResources().getString(R.string.ip) +
                    "EventosLimpieza/imagenes/" +
                    listaEventos.get(position).getRutaFotografia();

            System.out.println(urlImagen);

            iniciarCargaImagen(urlImagen, holder);
        }
    }


    private void iniciarCargaImagen(String urlImagen, final ParticipaEventoViewHolder holder)
    {
        urlImagen.replace(" ", "%20"); // evitar errores con los espacios

        ImageRequest imageRequest = new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                holder.imagenEvento.setImageBitmap(response);
                holder.imagenEvento.setTag("SI"); // la imagen no se recargará
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                holder.imagenEvento.setImageResource(R.drawable.imagen_no_disponible);
            }
        });

        VolleySingleton.getinstance(context).addToRequestQueue(imageRequest);
    }


    public static class ParticipaEventoViewHolder extends RecyclerView.ViewHolder {
        // campos de un elemento de la lista
        public ImageView imagenEvento;
        public TextView tituloEvento;
        public TextView fechaHoraEvento;
        public TextView creador;
        public TextView descripcion;
        public TextView tipoResiduo;

        public ParticipaEventoViewHolder(View v)
        {
            super(v);
            imagenEvento = v.findViewById(R.id.imagenEvento);
            imagenEvento.setTag("NO");
            tituloEvento = v.findViewById(R.id.tituloEvento);
            creador = v.findViewById(R.id.creador);
            fechaHoraEvento = v.findViewById(R.id.fecha_hora);
            descripcion = v.findViewById(R.id.descripcion);
            tipoResiduo = v.findViewById(R.id.tipo_residuo);

        }
    }
}
