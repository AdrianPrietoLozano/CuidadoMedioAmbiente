package com.example.cuidadodelambiente.ui.fragments.recomendaciones;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecomendacionesEventosAdapter extends RecyclerView.Adapter<RecomendacionesEventosAdapter.EventoViewHolder>
{
    private List<EventoLimpieza> listaEventos;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public RecomendacionesEventosAdapter(Context context, List<EventoLimpieza> listaEventos,
                                  @NonNull OnItemClickListener onItemClickListener)
    {
        this.context = context;
        this.listaEventos = listaEventos;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    @NonNull
    @Override
    public RecomendacionesEventosAdapter.EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_evento, parent, false);
        final RecomendacionesEventosAdapter.EventoViewHolder eventoViewHolder = new RecomendacionesEventosAdapter.EventoViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, eventoViewHolder.getPosition());
            }
        });

        return eventoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecomendacionesEventosAdapter.EventoViewHolder holder, int position) {
        holder.tituloEvento.setText(listaEventos.get(position).getTitulo());
        holder.fechaHoraEvento.setText(String.format("%s, %s",
                listaEventos.get(position).getFecha(),
                listaEventos.get(position).getHora()));

        String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() +
                listaEventos.get(position).getRutaFotografia();

        // foto del evento
        Picasso.with(context)
                .load(urlFoto)
                .fit()
                .transform(new RoundedTransformationBuilder()
                        .borderColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .borderWidth(7)
                        .cornerRadiusDp(35)
                        .build()
                ).into(holder.imagenEvento);

        //Picasso.with(context).load(urlFoto).into(holder.imagenEvento);

        /*
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        builder.build().load(listaEventos.get(position).getFoto())
                .into(holder.imagenEvento);*/

    }

    public static class EventoViewHolder extends RecyclerView.ViewHolder {
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
            imagenUbicacion = v.findViewById(R.id.imagenUbicacion);

        }
    }



    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}