package com.example.cuidadodelambiente.ui.fragments.datos_evento.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.cuidadodelambiente.ParaObservar;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.helpers.HelperCargaError;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.ui.activities.amin_evento.AdministrarEventoActivity;
import com.example.cuidadodelambiente.ui.fragments.DatosReporteFragment;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.presenter.DatosEventoPresenter;
import com.example.cuidadodelambiente.ui.fragments.datos_evento.presenter.IDatosEventoPresenter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


public class DatosEventoFragment extends BottomSheetDialogFragment
    implements IDatosEventoView{

    private final String TAG = DatosEventoFragment.class.getSimpleName();

    private BottomSheetBehavior mBehavior;
    private Integer eventoId; // id del evento en la base de datos
    private Integer idUsuario;
    private TextView nombreEvento, fechaHora, creador, descripcion;
    private TextView numPersonasUnidas, mensajeProblema, tipoResiduo;
    private LinearLayout layoutDatosReporte;
    private Button botonParticipar;
    private Button botonAdministrarEvento;
    private ImageView imagenEvento;
    private ProgressDialog progreso;
    private ProgressBar barraCarga;
    private JsonObjectRequest jsonObjectRequest;
    private EventoLimpieza eventoLimpieza; // evento mostrado actualmente
    private HelperCargaError helperCargaError;
    private LinearLayout layoutNoConexion;
    private LinearLayout contenidoPrincipal;
    private Button botonVolverIntentar;

    private static ParaObservar observable = new ParaObservar();
    private IDatosEventoPresenter presenter;

    public DatosEventoFragment() {
        this.presenter = new DatosEventoPresenter(this);
    }

    public static ParaObservar getObservable() {
        return observable;
    }

    public static DatosEventoFragment newInstance(Integer eventoId) {
        DatosEventoFragment f = new DatosEventoFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if (eventoId == null) eventoId = -1; // para que no truene en caso de un error
        args.putInt(Constants.EVENTO_ID, eventoId);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventoId = getArguments().getInt(Constants.EVENTO_ID);
        } else {
            eventoId = -1;
        }

        idUsuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado().getId();
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
        botonParticipar = v.findViewById(R.id.botonParticipar);
        botonAdministrarEvento = v.findViewById(R.id.btnAdministrarEvento);
        botonAdministrarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                //((MainActivity) getActivity()).cambiarFragment(new AdministrarEventoFragment(), "GGG");
                Intent intent = new Intent(getContext(), AdministrarEventoActivity.class);
                intent.putExtra(Constants.EVENTO_ID, eventoId);
                startActivity(intent);
            }
        });

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


        inicializarProgressDialog();

        intentarPeticionBD();

        return dialog;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        presenter.cancelarCargarDatosEvento();
    }

    private void inicializarProgressDialog() {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
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
        presenter.cargarDatosEvento(this.eventoId, idUsuario);
    }



    private void configurarBotonQuieroParticipar() {
        if (botonParticipar.isEnabled())
            botonParticipar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        botonParticipar.setText("Quiero participar");
        botonParticipar.setTextColor(Color.WHITE);
        botonParticipar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicBotonQuieroParticipar();
            }
        });
    }

    private void configurarBotonDejarParticipar() {
        if (botonParticipar.isEnabled())
            botonParticipar.setBackgroundColor(getResources().getColor(R.color.rojoClaro));
        botonParticipar.setText("Dejar de participar");
        botonParticipar.setTextColor(Color.BLACK);
        botonParticipar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progreso.show();

                presenter.dejarDeParticiparEnEvento(
                        idUsuario,
                        eventoLimpieza.getIdEvento()
                );
            }
        });
    }

    private void clicBotonQuieroParticipar()
    {
        progreso.show();

        presenter.participarEnEvento(
                eventoLimpieza.getIdEvento(),
                idUsuario
        );
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

    @Override
    public void onCargarDatosEventoError(String error) {
        mostrarLayoutError();
    }

    @Override
    public void onParticiparEnEventoError(int resultado, String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        progreso.dismiss();
    }

    @Override
    public void onDejarParticiparEventoError(int resultado, String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        progreso.dismiss();
    }

    @Override
    public void onCargarDatosEventoExito(EventoLimpieza evento) {
        this.eventoLimpieza = evento;

        nombreEvento.setText(eventoLimpieza.getTitulo());
        fechaHora.setText(String.format("%s, %s", eventoLimpieza.getFecha(),
                eventoLimpieza.getHora()));
        creador.setText(eventoLimpieza.getCreador().getNombre());
        tipoResiduo.setText(eventoLimpieza.getResiduos().toString());
        descripcion.setText(eventoLimpieza.getDescripcion());
        numPersonasUnidas.setText(String.format("%s %s",
                eventoLimpieza.getNumPersonasUnidas(), "personas unidas"));
        if (eventoLimpieza.getIdReporte() != null) {
            layoutDatosReporte.setVisibility(View.VISIBLE);
        }

        // verificar si la fecha del evento aún está vigente
        SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Constants.LOCALE_MX);
        boolean isFechaEventoVigente = true;
        try {
            Date dateEvento = parser.parse(eventoLimpieza.getFecha() + " " + eventoLimpieza.getHora());
            Date now = new Date();
            if (dateEvento.before(now)) {
                botonParticipar.setEnabled(false);
                isFechaEventoVigente = false; // la fecha del evento ya pasó
            }
        } catch (Exception e) {
            Log.e("FECHA", e.toString());
            botonParticipar.setEnabled(true);
            isFechaEventoVigente = true;
        }


        // verificar si el usuario ya participa en este evento
        if (eventoLimpieza.getUsuarioParticipa()) {
            configurarBotonDejarParticipar();
        } else {
            configurarBotonQuieroParticipar();
        }


        int idUsuario = UserLocalStore.getInstance(getContext()).getUsuarioLogueado().getId();
        // si el usuario es el creador, la fecha del evento ya pasó y el evento aún no ha sido administrado
        // se activa el boton para admin. el evento
        if (eventoLimpieza.getCreador().getId() == idUsuario && eventoLimpieza.getAdministrado().equals("0")
                && !isFechaEventoVigente ) {
            botonAdministrarEvento.setVisibility(View.VISIBLE);
        } else {
            botonAdministrarEvento.setVisibility(View.GONE);
        }

        mostrarContenidoPrincipal();

        String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() +
                eventoLimpieza.getRutaFotografia();
        Picasso.with(getContext()).load(urlFoto).into(imagenEvento);
    }

    @Override
    public void onParticiparEnEventoExito() {
        Toast.makeText(getContext(), "Éxito", Toast.LENGTH_SHORT).show();
        configurarBotonDejarParticipar();
        eventoLimpieza.setNumPersonasUnidas(eventoLimpieza.getNumPersonasUnidas() + 1);
        numPersonasUnidas.setText(String.format("%s %s",
                eventoLimpieza.getNumPersonasUnidas(), "personas unidas"));

        progreso.dismiss();

        getObservable().notificar(eventoLimpieza);
    }

    @Override
    public void onDejarParticiparEventoExito() {
        Toast.makeText(getContext(), "Éxito", Toast.LENGTH_SHORT).show();
        configurarBotonQuieroParticipar();
        eventoLimpieza.setNumPersonasUnidas(eventoLimpieza.getNumPersonasUnidas() - 1);
        numPersonasUnidas.setText(String.format("%s %s",
                eventoLimpieza.getNumPersonasUnidas(), "personas unidas"));

        progreso.dismiss();

        getObservable().notificar(eventoLimpieza);
    }
}
