package com.example.cuidadodelambiente.ui.fragments.multiselection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.EventoItem;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectionAdapter extends RecyclerView.Adapter<MultiSelectionAdapter.EventoViewHolder>
{
    private List<EventoItem> listaEventos;
    private Context context;
    private OnItemsSelectedCountListener onItemsSelectedCountListener;
    private int itemsSelectedCount = 0;

    public MultiSelectionAdapter(Context context, List<EventoItem> listaEventos,
                                         @NonNull OnItemsSelectedCountListener listener)
    {
        this.context = context;
        this.listaEventos = listaEventos;
        this.onItemsSelectedCountListener = listener;
    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    @NonNull
    @Override
    public MultiSelectionAdapter.EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        final MultiSelectionAdapter.EventoViewHolder eventoViewHolder = new MultiSelectionAdapter.EventoViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventoItem e = listaEventos.get(eventoViewHolder.getAdapterPosition());
                //eventoViewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.amarillo1));
                if (e.getChecked()) {
                    eventoViewHolder.checkBox.setChecked(false);
                    itemsSelectedCount--;
                } else {
                    eventoViewHolder.checkBox.setChecked(true);
                    itemsSelectedCount++;
                }

                e.setChecked(!e.getChecked());
                onItemsSelectedCountListener.onItemSelectedCountChange(itemsSelectedCount);
            }
        });

        return eventoViewHolder;
    }

    public List<EventoItem> getItemsSelected() {
        List<EventoItem> eventosSeleccionados = new ArrayList<>();
        for (EventoItem e : listaEventos) {
            if (e.getChecked()) {
                eventosSeleccionados.add(e);
            }
        }

        return eventosSeleccionados;
    }

    public void selectAll() {
        boolean check;
        int count = 0;

        // si todos los items estan seleccionados
        if (itemsSelectedCount == listaEventos.size()) {
            check = false;
            count = 0;
        } else { // si no estan TODOS seleccionados
            check = true;
            count = listaEventos.size();
        }

        for (EventoItem e : listaEventos) {
            e.setChecked(check);
        }

        itemsSelectedCount = count;

        notifyDataSetChanged();
        onItemsSelectedCountListener.onItemSelectedCountChange(itemsSelectedCount);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiSelectionAdapter.EventoViewHolder holder, int position) {
        holder.tituloEvento.setText(listaEventos.get(position).getTitulo());
        holder.fechaHoraEvento.setText(String.format("%s, %s",
                listaEventos.get(position).getFecha(),
                listaEventos.get(position).getHora()));

        String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() +
                listaEventos.get(position).getFoto();

        if (listaEventos.get(position).getChecked()) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

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
        public TextView tituloEvento;
        public TextView fechaHoraEvento;
        public CheckBox checkBox;

        public EventoViewHolder(View v)
        {
            super(v);
            imagenEvento = v.findViewById(R.id.userFoto);
            tituloEvento = v.findViewById(R.id.userName);
            fechaHoraEvento = v.findViewById(R.id.userEmail);
            checkBox = v.findViewById(R.id.checkBox);
        }

    }



    public interface OnItemsSelectedCountListener {
        void onItemSelectedCountChange(int count);
    }
}