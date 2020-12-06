package com.example.cuidadodelambiente.ui.activities.amin_evento;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdministrarEventoAdapter extends RecyclerView.Adapter<AdministrarEventoAdapter.UserViewHolder>
{
    private List<User> listaUsuarios;
    private Context context;
    private OnItemsSelectedCountListener onItemsSelectedCountListener;
    private int itemsSelectedCount = 0;

    public AdministrarEventoAdapter(Context context, List<User> listaUsuarios,
                                         @NonNull OnItemsSelectedCountListener listener)
    {
        this.context = context;
        this.listaUsuarios = listaUsuarios;
        this.onItemsSelectedCountListener = listener;
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    @NonNull
    @Override
    public AdministrarEventoAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        final AdministrarEventoAdapter.UserViewHolder eventoViewHolder = new AdministrarEventoAdapter.UserViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = listaUsuarios.get(eventoViewHolder.getAdapterPosition());
                //eventoViewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.amarillo1));
                if (user.isChecked()) {
                    eventoViewHolder.imageCheck.setVisibility(View.GONE);
                    itemsSelectedCount--;
                    eventoViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    eventoViewHolder.imageCheck.setVisibility(View.VISIBLE);
                    itemsSelectedCount++;
                    eventoViewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.verdeClaro));
                }

                user.setChecked(!user.isChecked());
                onItemsSelectedCountListener.onItemSelectedCountChange(itemsSelectedCount);
            }
        });

        return eventoViewHolder;
    }

    public List<User> getItemsSelected() {
        List<User> eventosSeleccionados = new ArrayList<>();
        for (User e : listaUsuarios) {
            if (e.isChecked()) {
                eventosSeleccionados.add(e);
            }
        }

        return eventosSeleccionados;
    }

    public void selectAll(boolean seleccionar) {
        int count = 0;

        if (seleccionar == true) {
            count = listaUsuarios.size();
        } else {
            count = 0;
        }

        for (User e : listaUsuarios) {
            e.setChecked(seleccionar);
        }

        itemsSelectedCount = count;

        notifyDataSetChanged();
        onItemsSelectedCountListener.onItemSelectedCountChange(itemsSelectedCount);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministrarEventoAdapter.UserViewHolder holder, int position) {
        holder.userName.setText(listaUsuarios.get(position).getNombre());
        holder.userEmail.setText(listaUsuarios.get(position).getEmail());

        String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() +
                listaUsuarios.get(position).getFoto();

        if (listaUsuarios.get(position).isChecked()) {
            holder.imageCheck.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.verdeClaro));
        } else {
            holder.imageCheck.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
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
                ).into(holder.userFoto);

        //Picasso.with(context).load(urlFoto).into(holder.imagenEvento);

        /*
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        builder.build().load(listaUsuarios.get(position).getFoto())
                .into(holder.imagenEvento);*/

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public ImageView userFoto;
        public TextView userName;
        public TextView userEmail;
        public ImageView imageCheck;

        public UserViewHolder(View v)
        {
            super(v);
            userFoto = v.findViewById(R.id.userFoto);
            userName = v.findViewById(R.id.userName);
            userEmail = v.findViewById(R.id.userEmail);
            imageCheck = v.findViewById(R.id.imageCheck);
        }

    }



    public interface OnItemsSelectedCountListener {
        void onItemSelectedCountChange(int count);
    }
}