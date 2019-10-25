package com.example.cuidadodelambiente.Fragments;


import android.app.ProgressDialog;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.Entidades.EventoLimpieza;
import com.example.cuidadodelambiente.Entidades.VolleySingleton;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecomendacionEventosFragment extends Fragment
        implements Response.ErrorListener, Response.Listener<JSONObject> {

    private ListView listView;
    private ArrayList<EventoLimpieza> eventos;
    private ProgressDialog progreso;
    private JsonObjectRequest jsonObjectRequest;
    /*
    String[] titulos = {"Saneamiento del Río Santiago...", "Título del evento", "Título del evento",
            "Título del evento", "Título del evento", "Título del evento", "Título del evento"};

    String[] fechasHoras = {"26 de septiembre de 2019, 13:00", "Fecha y hora", "Fecha y hora",
            "Fecha y hora", "Fecha y hora", "Fecha y hora", "Fecha y hora"};

    int[] idImagenes = {R.drawable.basura1,
            R.drawable.basura2,
            R.drawable.basura1,
            R.drawable.basura2,
            R.drawable.basura1,
            R.drawable.basura2,
            R.drawable.basura1};
    */

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
        eventos = new ArrayList<>();

        iniciarPeticionBD();
        //---------------

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                        new DatosEventoFragment());
                 */
                /*
                switch (position) {
                    default:
                        Toast.makeText(getContext(), "Agregar evento clic", Toast.LENGTH_SHORT).show();
                        break;
                }*/

                Fragment fragmentDatosEvento = DatosEventoFragment.newInstance((int)view.getTag());
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(), fragmentDatosEvento);
            }
        });

        return v;
    }

    private void iniciarPeticionBD() {
        String url = getString(R.string.ip) + "EventosLimpieza/eventos_recomendados.php?usuario_id=1";

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        VolleySingleton.getinstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progreso.hide();
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray json = response.optJSONArray("ubicacion");

        JSONObject jsonObject;
        LatLng ubicacion;
        EventoLimpieza eventoLimpieza = new EventoLimpieza();

        try
        {
            for(int i = 0; i < json.length(); i++)
            {
                jsonObject = json.getJSONObject(i);

                eventoLimpieza.setIdEvento(jsonObject.optInt("id_evento"));
                eventoLimpieza.setTitulo(jsonObject.optString("titulo"));
                eventoLimpieza.setFecha(jsonObject.optString("fecha"));
                eventoLimpieza.setHora(jsonObject.optString("hora"));
                eventoLimpieza.setRutaFotografia(jsonObject.optString("foto"));

                eventos.add(eventoLimpieza);

            }

            progreso.hide();
            MyAdapter adapter = new MyAdapter(getContext(), eventos);
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            progreso.hide();
            e.printStackTrace();
        }
    }
}






class MyAdapter extends BaseAdapter
{

        Context context;
        ArrayList<EventoLimpieza> listaEventos;

        MyAdapter(Context c, ArrayList<EventoLimpieza> listaEventosventos)
        {
            super(c, R.layout.row_evento, R.id.tituloEvento, titulos);

            this.context = c;
            this.listaEventos = listaEventos;
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

            Drawable originalDrawable = rowEvento.getResources().getDrawable(R.drawable.basura1);
            Bitmap originalBitmal = ((BitmapDrawable) originalDrawable).getBitmap();

            RoundedBitmapDrawable roundedDrawable =
                    RoundedBitmapDrawableFactory.create(rowEvento.getResources(), originalBitmal);

            roundedDrawable.setCornerRadius(originalBitmal.getHeight());

            EventoLimpieza evento = listaEventos.get(position);
            imagen.setImageDrawable(roundedDrawable);
            titulo.setText(evento.getTitulo());
            fechaHora.setText(String.format("%s, %s", evento.getFecha(), evento.getHora()));
            rowEvento.setTag(evento.getIdEvento());
            return rowEvento;
        }
}
