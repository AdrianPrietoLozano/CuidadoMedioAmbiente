package com.example.cuidadodelambiente;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.cuidadodelambiente.Dialogos.DialogClicReporte;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatosEventoFragment extends Fragment
        implements Response.Listener<JSONObject>, Response.ErrorListener{

    private int eventoId; // id del evento en la base de datos
    private TextView nombreEvento, fechaHora, creador, descripcion, numPersonasUnidas;
    ProgressDialog progreso;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public DatosEventoFragment() {
        // Required empty public constructor
    }

    private void iniciarPeticionBD()
    {
        String url = "http://192.168.1.68/EventosLimpieza/datos_evento.php?evento_id="+eventoId;

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    public static DatosEventoFragment newInstance(int eventoId) {
        DatosEventoFragment f = new DatosEventoFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("evento_id", eventoId);
        f.setArguments(args);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_datos_evento, container, false);
        nombreEvento = v.findViewById(R.id.nombreEvento);
        fechaHora = v.findViewById(R.id.fecha_hora_evento);
        creador = v.findViewById(R.id.creador_evento);
        descripcion = v.findViewById(R.id.descripcion_evento);
        numPersonasUnidas = v.findViewById(R.id.num_personas_unidas);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        request = Volley.newRequestQueue(getContext());

        super.onCreate(savedInstanceState);
        eventoId = getArguments().getInt("evento_id");

        iniciarPeticionBD();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progreso.hide();
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        progreso.hide();

        JSONArray json = response.optJSONArray("datos_evento");
        JSONObject jsonObject = null;

        try {
            jsonObject = json.getJSONObject(0);
            nombreEvento.setText(jsonObject.optString("titulo"));
            fechaHora.setText(String.format("%s, %s", jsonObject.optString("fecha"), jsonObject.optString("hora")));
            creador.setText(jsonObject.optString("creador"));
            descripcion.setText(jsonObject.optString("descripcion"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
