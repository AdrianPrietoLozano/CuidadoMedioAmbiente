package com.example.cuidadodelambiente.ui.fragments.mis_eventos_reportes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.material.chip.Chip;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MisEventosAdapter extends RecyclerView.Adapter<MisEventosAdapter.MiEventoViewHolder>
{
    private List<EventoLimpieza> listaEventos;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public MisEventosAdapter(Context context, List<EventoLimpieza> listaEventos,
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
    public MiEventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_evento_2, parent, false);
        final MiEventoViewHolder eventoViewHolder = new MiEventoViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, eventoViewHolder.getPosition());
            }
        });

        return eventoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MiEventoViewHolder holder, int position) {

        EventoLimpieza evento = listaEventos.get(position);
        holder.tituloEvento.setText(evento.getTitulo());
        holder.fechaHoraEvento.setText(String.format("%s, %s",
                evento.getFecha(),
                evento.getHora()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Constants.LOCALE_MX);
        try {
            Date fecha = sdf.parse(String.format("%s %s", evento.getFecha(), evento.getHora()));
            Date now = sdf.parse(sdf.format(new Date()));

            if (fecha.before(now)) {
                holder.statusEvento.setChipBackgroundColorResource(R.color.colorPrimary);
                holder.statusEvento.setText("En curso");
            } else {
                holder.statusEvento.setChipBackgroundColorResource(R.color.rojoNaranja);
                holder.statusEvento.setText("Expiró");
            }

        } catch (ParseException e) {
            holder.statusEvento.setVisibility(View.GONE);
        }

        String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() + "imagenes/" +
                evento.getRutaFotografia();

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

    public static class MiEventoViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagenEvento;
        public ImageView imagenUbicacion;
        public TextView tituloEvento;
        public TextView fechaHoraEvento;
        public Chip statusEvento;

        public MiEventoViewHolder(View v)
        {
            super(v);
            imagenEvento = v.findViewById(R.id.imagenEvento);
            tituloEvento = v.findViewById(R.id.tituloEvento);
            fechaHoraEvento = v.findViewById(R.id.fechaHoraEvento);
            imagenUbicacion = v.findViewById(R.id.imagenUbicacion);
            statusEvento = v.findViewById(R.id.statusEvento);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}


