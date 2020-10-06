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
import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.material.chip.Chip;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MisReportesAdapter extends RecyclerView.Adapter<MisReportesAdapter.MiReporteViewHolder>
{
    private List<ReporteContaminacion> listaReportes;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public MisReportesAdapter(Context context, List<ReporteContaminacion> listaReportes,
                              @NonNull OnItemClickListener onItemClickListener)
    {
        this.context = context;
        this.listaReportes = listaReportes;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    @NonNull
    @Override
    public MiReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reporte, parent, false);
        final MiReporteViewHolder reporteViewHolder = new MiReporteViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, reporteViewHolder.getPosition());
            }
        });

        return reporteViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MiReporteViewHolder holder, int position) {

        ReporteContaminacion reporte = listaReportes.get(position);
        holder.descripcionReporte.setText(reporte.getDescripcion());
        holder.fechaHoraReporte.setText(String.format("%s, %s",
                reporte.getFecha(),
                reporte.getHora()));

        String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() +
                reporte.getRutaFoto();

        // foto del evento
        Picasso.with(context)
                .load(urlFoto)
                .fit()
                .transform(new RoundedTransformationBuilder()
                        .borderColor(ContextCompat.getColor(context, R.color.rojo))
                        .borderWidth(7)
                        .cornerRadiusDp(35)
                        .build()
                ).into(holder.imagenReporte);

    }

    public static class MiReporteViewHolder extends RecyclerView.ViewHolder {
        public ImageView imagenReporte;
        public TextView descripcionReporte;
        public TextView fechaHoraReporte;

        public MiReporteViewHolder(View v)
        {
            super(v);
            imagenReporte = v.findViewById(R.id.imagenReporte);
            descripcionReporte = v.findViewById(R.id.descripcionReporte);
            fechaHoraReporte = v.findViewById(R.id.fechaHoraReporte);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}


