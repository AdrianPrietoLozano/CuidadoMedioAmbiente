package com.example.cuidadodelambiente.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import androidx.fragment.app.DialogFragment;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.Fragments.CargandoCircular;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;


public class DatosEventoFragment extends BottomSheetDialogFragment{

    private final String TAG = DatosEventoFragment.class.getSimpleName();

    private BottomSheetBehavior mBehavior;
    private int eventoId; // id del evento en la base de datos
    private TextView nombreEvento, fechaHora, creador, descripcion;
    private TextView numPersonasUnidas, mensajeProblema, tipoResiduo;
    private LinearLayout layoutDatosReporte;
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

    private Call<EventoLimpieza> callDatosEvento;
    private Call<JsonObject> callUnirseEvento;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventoId = getArguments().getInt(Constants.EVENTO_ID);
        }
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

        layoutDatosReporte = v.findViewById(R.id.layoutDatosReporte);
        layoutDatosReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventoLimpieza.getIdReporte() != null) {
                    BottomSheetDialogFragment fragmentDatosReporte = DatosReporteFragment.newInstance(eventoLimpieza.getIdReporte());
                    fragmentDatosReporte.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                    fragmentDatosReporte.show(getFragmentManager(), fragmentDatosReporte.getTag());
                }
            }
        });

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


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        callDatosEvento.cancel();
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
        Integer idUsuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado().getId();

        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        callDatosEvento = service.doGetEventoLimpieza(this.eventoId, idUsuario);
        callDatosEvento.enqueue(new Callback<EventoLimpieza>() {
            @Override
            public void onResponse(Call<EventoLimpieza> call, retrofit2.Response<EventoLimpieza> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "!response.isSuccessful()", Toast.LENGTH_LONG).show();
                    return;
                }

                eventoLimpieza = response.body();
                if (eventoLimpieza.getResultado() == 1) {
                    nombreEvento.setText(eventoLimpieza.getTitulo());
                    fechaHora.setText(String.format("%s, %s", eventoLimpieza.getFecha(),
                            eventoLimpieza.getHora()));
                    creador.setText(eventoLimpieza.getAmbientalista());
                    tipoResiduo.setText(eventoLimpieza.getResiduos().toString());
                    descripcion.setText(eventoLimpieza.getDescripcion());
                    numPersonasUnidas.setText(String.format("%s %s",
                            eventoLimpieza.getNumPersonasUnidas(), "personas unidas"));
                    if (eventoLimpieza.getIdReporte() != null) {
                        layoutDatosReporte.setVisibility(View.VISIBLE);
                    }

                    // verifiar si el usuario ya participa en este evento
                    if (eventoLimpieza.getUsuarioParticipa()) {
                        botonQuieroParticipar.setEnabled(false);
                        botonQuieroParticipar.setText("Ya participas en este evento");
                    }

                    mostrarContenidoPrincipal();

                    String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() + "imagenes/" +
                            eventoLimpieza.getRutaFotografia();
                    Picasso.with(getContext()).load(urlFoto).into(imagenEvento);
                } else {
                    Toast.makeText(getContext(), eventoLimpieza.getMensaje(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventoLimpieza> call, Throwable throwable) {
                Log.e("ERROR", throwable.getMessage());
                mostrarLayoutError();
            }
        });
    }

    public static DatosEventoFragment newInstance(int eventoId) {
        DatosEventoFragment f = new DatosEventoFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(Constants.EVENTO_ID, eventoId);
        f.setArguments(args);

        return f;
    }

    private void clicBotonQuieroParticipar()
    {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();

        Integer idUsuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado().getId();
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        callUnirseEvento = service.doUnirseEvento(idUsuario,
                eventoId, eventoLimpieza.getFecha(), eventoLimpieza.getHora(),
                eventoLimpieza.getFecha(), eventoLimpieza.getHora());

        callUnirseEvento.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject json = response.body();
                    int resultado = json.get("resultado").getAsInt();
                    Log.e("RESULTADO", String.valueOf(resultado));
                    String mensaje = json.get("mensaje").getAsString();
                    Log.e("MENSAJE", mensaje);

                    if (resultado == 1) { // éxito
                        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                        botonQuieroParticipar.setEnabled(false);
                    } else if (resultado == 2) { // ya participa en el evento
                        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    } else { // ocurrió un error
                        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                }

                progreso.hide();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progreso.hide();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
