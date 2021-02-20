package com.example.cuidadodelambiente.ui.fragments.datos_evento;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DatosEventoAdapter extends RecyclerView.Adapter<DatosEventoAdapter.EventoViewHolder> {
    private Context context;
    private List<EventoLimpieza> eventos;
    private OnItemClickListener itemClickListener;

    public DatosEventoAdapter(Context context, List<EventoLimpieza> listaEventos,
                                         @NonNull OnItemClickListener onItemClickListener)
    {
        this.context = context;
        this.eventos = listaEventos;
        this.itemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_evento, parent, false);
        final EventoViewHolder eventoViewHolder = new EventoViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v, eventoViewHolder.getPosition());
            }
        });

        return eventoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        holder.tituloEvento.setText(eventos.get(position).getTitulo());
        holder.fechaHoraEvento.setText(String.format("%s, %s",
                eventos.get(position).getFecha(),
                eventos.get(position).getHora()));

        String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() +
                eventos.get(position).getRutaFotografia();

        Picasso.with(context)
                .load(urlFoto)
                .into(holder.imagenEvento);
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    class EventoViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagenEvento;
        public TextView tituloEvento;
        public TextView fechaHoraEvento;

        public EventoViewHolder(View view) {
            super(view);
            imagenEvento = view.findViewById(R.id.imagenEvento);
            tituloEvento = view.findViewById(R.id.tituloEvento);
            fechaHoraEvento = view.findViewById(R.id.fechaHoraEvento);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
