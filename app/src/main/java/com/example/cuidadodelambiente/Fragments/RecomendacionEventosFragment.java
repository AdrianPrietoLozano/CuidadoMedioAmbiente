package com.example.cuidadodelambiente.Fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.Entidades.EventoLimpieza;
import com.example.cuidadodelambiente.EventoItem;
import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.Utilidades;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecomendacionEventosFragment extends Fragment {

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


    public RecomendacionEventosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recomendacion_eventos, container, false);

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

        return v;
    }

    private void intentarPeticionBD()
    {
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
        }
    }

    private void iniciarPeticionBD() {
        /*
        String url = getString(R.string.ip) + "EventosLimpieza/recomendaciones_eventos.php?id_ambientalista="+
                DeclaracionFragments.actualAmbientalista;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getinstance(getContext()).addToRequestQueue(jsonObjectRequest);
         */


        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<List<EventoItem>> call = service.doGetEventosRecomendados(DeclaracionFragments.actualAmbientalista);
        call.enqueue(new Callback<List<EventoItem>>() {
            @Override
            public void onResponse(Call<List<EventoItem>> call, final retrofit2.Response<List<EventoItem>> response) {

                recyclerEventos.setAdapter(new EventoAdapter(getContext(), response.body(), new RecyclerViewOnItemClickListener() {
                    @Override
                    public void onClick(View v, int position) {

                        Fragment fragmentDatosEvento = DatosEventoFragment.newInstance(
                                response.body().get(position).getId());
                        ((MainActivity)getActivity())
                                .cambiarFragment(fragmentDatosEvento, "DATOS");
                    }
                }));

                cargandoCircular.ocultarCargaMostrarContenido();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<List<EventoItem>> call, Throwable throwable) {
                call.cancel();
                Toast.makeText(getContext(), "onFailure", Toast.LENGTH_LONG).show();
                mensajeProblema.setText(getString(R.string.estamos_teniendo_problemas));
                swipeRefreshLayout.setRefreshing(false);
                layoutSinConexion.setVisibility(View.VISIBLE);
                cargandoCircular.ocultarCargaMostrarContenido();
            }
        });

    }
}






class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder>
{
    private List<EventoItem> listaEventos;
    private Context context;
    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;




    EventoAdapter(Context context, List<EventoItem> listaEventos, @NonNull RecyclerViewOnItemClickListener
                  recyclerViewOnItemClickListener)
    {
        this.context = context;
        this.listaEventos = listaEventos;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_evento, parent, false);
        final EventoViewHolder eventoViewHolder = new EventoViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewOnItemClickListener.onClick(v, eventoViewHolder.getPosition());
            }
        });

        return eventoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        holder.tituloEvento.setText(listaEventos.get(position).getTitulo());
        holder.fechaHoraEvento.setText(String.format("%s, %s",
                listaEventos.get(position).getFecha(),
                listaEventos.get(position).getHora()));

        String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() + "imagenes/" +
                listaEventos.get(position).getFoto();

        Picasso.with(context).load(urlFoto).into(holder.imagenEvento);

        /*
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        builder.build().load(listaEventos.get(position).getFoto())
                .into(holder.imagenEvento);*/

    }

    public static class EventoViewHolder extends RecyclerView.ViewHolder {
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
    }
}
