package com.example.cuidadodelambiente.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.Entidades.VolleySingleton;
import com.example.cuidadodelambiente.Fragments.CargandoCircular;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;


public class DatosEventoFragment extends BottomSheetDialogFragment{

    private BottomSheetBehavior mBehavior;
    private int eventoId; // id del evento en la base de datos
    private TextView nombreEvento, fechaHora, creador, descripcion;
    private TextView numPersonasUnidas, mensajeProblema, tipoResiduo;
    private Button botonQuieroParticipar;
    private ImageView imagenEvento;
    private ProgressDialog progreso;
    private ProgressBar barraCarga;
    private JsonObjectRequest jsonObjectRequest;
    private EventoLimpieza eventoLimpieza; // evento mostrado actualmente
    private CargandoCircular cargandoCircular;
    private LinearLayout layoutNoConexion;
    private LinearLayout contenidoPrincipal;
    private Button botonVolverIntentar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        eventoId = getArguments().getInt("evento_id");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View v = View.inflate(getContext(), R.layout.bottom_sheet_eventos, null);

        dialog.setContentView(v);
        mBehavior = BottomSheetBehavior.from((View) v.getParent());
        mBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);

        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        dismiss();
                        break;

                     case BottomSheetBehavior.STATE_DRAGGING:
                         Log.e("DRAGGING", "DRAGGIMG");
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("DRAGGING", "SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        layoutNoConexion = v.findViewById(R.id.layoutNoConexion);
        botonVolverIntentar = v.findViewById(R.id.btnVolverIntentarlo);
        botonVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarPeticionBD();
            }
        });

        barraCarga = v.findViewById(R.id.progressBar);
        nombreEvento = v.findViewById(R.id.nombreEvento);
        fechaHora = v.findViewById(R.id.fecha_hora_evento);
        creador = v.findViewById(R.id.creador_evento);
        tipoResiduo = v.findViewById(R.id.tipo_residuo_evento);
        descripcion = v.findViewById(R.id.descripcion_evento);
        numPersonasUnidas = v.findViewById(R.id.num_personas_unidas);
        imagenEvento = v.findViewById(R.id.imagenEvento);
        botonQuieroParticipar = v.findViewById(R.id.botonQuieroParticipar);

        contenidoPrincipal = v.findViewById(R.id.contenidoPrincipal);
        contenidoPrincipal.setVisibility(View.INVISIBLE);

        // evento clic boton "Quiero Participar"
        botonQuieroParticipar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicBotonQuieroParticipar();
            }
        });

        intentarPeticionBD();

        return dialog;
    }


    private void intentarPeticionBD()
    {
        mostrarCarga();

        // si hay conexión a internet
        if(Utilidades.hayConexionInternet(getContext())) {
            iniciarPeticionBD();
        }
        else { // no hay conexión a internet
            mostrarLayoutError();
        }
    }

    private void iniciarPeticionBD()
    {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        Call<EventoLimpieza> call = service.doGetEventoLimpieza(this.eventoId);
        call.enqueue(new Callback<EventoLimpieza>() {
            @Override
            public void onResponse(Call<EventoLimpieza> call, retrofit2.Response<EventoLimpieza> response) {

                eventoLimpieza = response.body();

                nombreEvento.setText(eventoLimpieza.getTitulo());
                fechaHora.setText(String.format("%s, %s", eventoLimpieza.getFecha(),
                        eventoLimpieza.getHora()));
                creador.setText(eventoLimpieza.getAmbientalista());
                tipoResiduo.setText(eventoLimpieza.getTipoResiduo());
                descripcion.setText(eventoLimpieza.getDescripcion());
                numPersonasUnidas.setText(String.format("%s %s",
                        eventoLimpieza.getNumPersonasUnidas(), "personas unidas"));

                mostrarContenidoPrincipal();

                String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() + "imagenes/" +
                        eventoLimpieza.getRutaFotografia();
                Picasso.with(getContext()).load(urlFoto).into(imagenEvento);

            }

            @Override
            public void onFailure(Call<EventoLimpieza> call, Throwable throwable) {
                call.cancel();
                Log.e("ERROR", throwable.getMessage());
                mostrarLayoutError();

            }
        });
    }

    public static DatosEventoFragment newInstance(int eventoId) {
        DatosEventoFragment f = new DatosEventoFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("evento_id", eventoId);
        f.setArguments(args);

        return f;
    }

    private void clicBotonQuieroParticipar()
    {
        String url = getString(R.string.ip) + "EventosLimpieza/insertar_unirse_evento.php?" +
                "id_ambientalista=" + DeclaracionFragments.actualAmbientalista +
                "&id_evento=" + eventoId +
                "&fecha_inicio=" + eventoLimpieza.getFecha() +
                "&hora_inicio=" + eventoLimpieza.getHora() +
                "&fecha_fin=" + eventoLimpieza.getFecha() +
                "&hora_fin=" + eventoLimpieza.getHora();

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray json = null;
                        String resultado;
                        try {
                            json = response.getJSONArray("resultado");
                            resultado = json.getString(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            resultado = "0";
                        }

                        if(resultado.equals("1"))
                        {
                            Toast.makeText(getContext(), "Éxito", Toast.LENGTH_SHORT).show();
                        }
                        else if(resultado.equals("2"))
                        {
                            Toast.makeText(getContext(), "Error: ya participas en este evento", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
                        }

                        progreso.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progreso.hide();
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        VolleySingleton.getinstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void mostrarCarga() {
        barraCarga.setVisibility(View.VISIBLE);
        layoutNoConexion.setVisibility(View.GONE);
        contenidoPrincipal.setVisibility(View.GONE);
    }

    private void mostrarContenidoPrincipal() {
        contenidoPrincipal.setVisibility(View.VISIBLE);
        barraCarga.setVisibility(View.GONE);
        layoutNoConexion.setVisibility(View.GONE);
    }

    private void mostrarLayoutError() {
        layoutNoConexion.setVisibility(View.VISIBLE);
        barraCarga.setVisibility(View.GONE);
        contenidoPrincipal.setVisibility(View.GONE);
    }
}
