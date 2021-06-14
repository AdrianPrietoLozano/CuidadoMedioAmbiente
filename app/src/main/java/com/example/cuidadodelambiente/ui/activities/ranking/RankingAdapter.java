package com.example.cuidadodelambiente.ui.activities.ranking;

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
import com.example.cuidadodelambiente.data.models.UserRank;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankViewHolder> {
    private Context context;
    private List<UserRank> ranking;

    public RankingAdapter(Context context, List<UserRank> ranking)
    {
        this.context = context;
        this.ranking = ranking;
    }


    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rank, parent, false);
        final RankViewHolder rankViewHolder = new RankViewHolder(v);

        return rankViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {
        holder.rank.setText(String.valueOf(ranking.get(position).getRank()));
        holder.nombre.setText(ranking.get(position).getNombre());
        holder.puntos.setText(String.valueOf(ranking.get(position).getPuntos()));
    }

    @Override
    public int getItemCount() {
        return ranking.size();
    }

    class RankViewHolder extends RecyclerView.ViewHolder {
        public TextView rank;
        public TextView nombre;
        public TextView puntos;

        public RankViewHolder(View view) {
            super(view);
            rank = view.findViewById(R.id.rank);
            nombre = view.findViewById(R.id.nombre);
            puntos = view.findViewById(R.id.puntos);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
