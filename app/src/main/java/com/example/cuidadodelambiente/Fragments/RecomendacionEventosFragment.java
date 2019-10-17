package com.example.cuidadodelambiente.Fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cuidadodelambiente.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecomendacionEventosFragment extends Fragment {

    ListView listView;
    String[] titulos = {"Saneamiento del Río Santiago ...", "Título del evento", "Título del evento",
            "Título del evento", "Título del evento", "Título del evento", "Título del evento"};

    String[] fechasHoras = {"26 de septiembre de 2019, 13:00", "Fecha y hora", "Fecha y hora",
            "Fecha y hora", "Fecha y hora", "Fecha y hora", "Fecha y hora"};

    int[] idImagenes = {R.drawable.basura1,
            R.drawable.basura2,
            R.drawable.basura1,
            R.drawable.basura2,
            R.drawable.basura1,
            R.drawable.basura2,
            R.drawable.basura1,};


    public RecomendacionEventosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recomendacion_eventos, container, false);

        listView = v.findViewById(R.id.listViewEventos);

        MyAdapter adapter = new MyAdapter(getContext(), titulos, fechasHoras, idImagenes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    default:
                        Toast.makeText(getContext(), "Agregar evento clic", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        return v;
    }
}

class MyAdapter extends ArrayAdapter<String>
{

        Context context;
        String[] titulos;
        String[] fechasHoras;
        int[] idImagenes;

        MyAdapter(Context c, String[] titulos, String[] fechasHoras, int[] idImagenes)
        {
            super(c, R.layout.row_evento, R.id.tituloEvento, titulos);

            this.context = c;
            this.titulos = titulos;
            this.fechasHoras = fechasHoras;
            this.idImagenes = idImagenes;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowEvento = layoutInflater.inflate(R.layout.row_evento, parent, false);
            ImageView imagen = rowEvento.findViewById(R.id.imagenEvento);
            TextView titulo = rowEvento.findViewById(R.id.tituloEvento);
            TextView fechaHora = rowEvento.findViewById(R.id.fechaHoraEvento);

            Drawable originalDrawable = rowEvento.getResources().getDrawable(idImagenes[position]);
            Bitmap originalBitmal = ((BitmapDrawable) originalDrawable).getBitmap();

            RoundedBitmapDrawable roundedDrawable =
                    RoundedBitmapDrawableFactory.create(rowEvento.getResources(), originalBitmal);

            roundedDrawable.setCornerRadius(originalBitmal.getHeight());

            imagen.setImageDrawable(roundedDrawable);
            titulo.setText(titulos[position]);
            fechaHora.setText(fechasHoras[position]);
            return rowEvento;
        }
}
