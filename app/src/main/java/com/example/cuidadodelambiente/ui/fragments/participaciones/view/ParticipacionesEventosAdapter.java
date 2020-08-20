package com.example.cuidadodelambiente.ui.fragments.participaciones.view;

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

class ParticipacionesEventosAdapter extends RecyclerView.Adapter<ParticipacionesEventosAdapter.ParticipaEventoViewHolder>
{
    private List<EventoLimpieza> listaEventos;
    private Context context;

    ParticipacionesEventosAdapter(Context context, List<EventoLimpieza> listaEventos)
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
        //holder.tipoResiduo.setText(listaEventos.get(position).getTipoResiduo());
        holder.descripcion.setText(listaEventos.get(position).getDescripcion());

        String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() + "imagenes/" +
                listaEventos.get(position).getRutaFotografia();
        Picasso.with(context).load(urlFoto).into(holder.imagenEvento);

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
            tituloEvento = v.findViewById(R.id.tituloEvento);
            creador = v.findViewById(R.id.creador);
            fechaHoraEvento = v.findViewById(R.id.fecha_hora);
            descripcion = v.findViewById(R.id.descripcion);
            tipoResiduo = v.findViewById(R.id.tipo_residuo);

        }
    }
}