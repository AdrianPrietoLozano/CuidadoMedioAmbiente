package com.example.cuidadodelambiente.ui.fragments.datos_evento;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class DatosEventoFragment extends BottomSheetDialogFragment
    implements Contract.View {

    private final String TAG = DatosEventoFragment.class.getSimpleName();

    private BottomSheetBehavior mBehavior;
    private Integer eventoId; // id del evento en la base de datos
    private Integer idUsuario;
    private TextView nombreEvento, fechaHora, creador, descripcion;
    private TextView numPersonasUnidas, mensajeProblema, tipoResiduo;
    private LinearLayout layoutDatosReporte;
    private Button botonParticipar;
    private Button botonDejarParticipar;
    private Button botonAdministrarEvento;
    private ImageView imagenEvento;
    private ProgressDialog progreso;
    private ProgressBar barraCarga;
    private EventoLimpieza eventoLimpieza; // evento mostrado actualmente
    private LinearLayout layoutNoConexion;
    private LinearLayout contenidoPrincipal;
    private Button botonVolverIntentar;
    private LinearLayout layoutRecyler;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapterEventos;
    private RecyclerView.LayoutManager lManager;

    private static ParaObservar observable = new ParaObservar();
    private Contract.Presenter presenter;

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

        this.presenter = new DatosEventoPresenter(this);

        layoutRecyler = v.findViewById(R.id.layoutRecycler);
        layoutRecyler.setVisibility(View.GONE);
        recyclerView = v.findViewById(R.id.recyclerEventos);
        recyclerView.setHasFixedSize(true);
        lManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(lManager);

        layoutNoConexion = v.findViewById(R.id.layoutNoConexion);
        botonVolverIntentar = v.findViewById(R.id.btnVolverIntentarlo);
        botonVolverIntentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.fetchEvento(eventoId);
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
        botonParticipar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.participarEnEvento(eventoLimpieza.getIdEvento());
            }
        });
        botonDejarParticipar = v.findViewById(R.id.botonDejarParticipar);
        botonDejarParticipar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("DEJAR", "Dejar view");
                presenter.dejarDeParticiparEnEvento(eventoLimpieza.getIdEvento());
            }
        });
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

        presenter.fetchEvento(this.eventoId);

        return dialog;
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void showLoading() {
        barraCarga.setVisibility(View.VISIBLE);
        contenidoPrincipal.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        barraCarga.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingDialog() {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando...");
        progreso.show();
    }

    @Override
    public void hideLoadingDialog() {
        if (progreso != null && progreso.isShowing())
            progreso.cancel();
    }

    @Override
    public void hideError() {
        layoutNoConexion.setVisibility(View.GONE);
    }

    @Override
    public void showError(String error) {
        layoutNoConexion.setVisibility(View.VISIBLE);
        contenidoPrincipal.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEvento(EventoLimpieza evento) {
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

        presenter.fetchRecomendacionesEvento(this.eventoId);
        contenidoPrincipal.setVisibility(View.VISIBLE);

        String urlFoto = RetrofitClientInstance.getRetrofitInstance().baseUrl() +
                eventoLimpieza.getRutaFotografia();
        Picasso.with(getContext()).load(urlFoto).into(imagenEvento);
    }

    @Override
    public void onParticiparEventoExito() {
        enableDejarParticipar();
        eventoLimpieza.setNumPersonasUnidas(eventoLimpieza.getNumPersonasUnidas() + 1);
        numPersonasUnidas.setText(String.format("%s %s",
                eventoLimpieza.getNumPersonasUnidas(), "personas unidas"));

        getObservable().notificar(eventoLimpieza);
    }

    @Override
    public void onDejarParticiparEventoExito() {
        enableParticipacion();
        eventoLimpieza.setNumPersonasUnidas(eventoLimpieza.getNumPersonasUnidas() - 1);
        numPersonasUnidas.setText(String.format("%s %s",
                eventoLimpieza.getNumPersonasUnidas(), "personas unidas"));

        getObservable().notificar(eventoLimpieza);
    }

    @Override
    public void showRecomendaciones(List<EventoLimpieza> eventos) {
        adapterEventos = new DatosEventoAdapter(getContext(), eventos, new DatosEventoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Integer idEvento = eventos.get(position).getIdEvento();
                BottomSheetDialogFragment fragmentDatosEvento = DatosEventoFragment.newInstance(idEvento);
                fragmentDatosEvento.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                fragmentDatosEvento.show(getFragmentManager(), fragmentDatosEvento.getTag());
            }
        });

        recyclerView.setAdapter(adapterEventos);
        layoutRecyler.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableParticipacion() {
        botonParticipar.setVisibility(View.VISIBLE);
        botonDejarParticipar.setVisibility(View.GONE);
        botonAdministrarEvento.setVisibility(View.GONE);
    }

    @Override
    public void enableDejarParticipar() {
        botonParticipar.setVisibility(View.GONE);
        botonDejarParticipar.setVisibility(View.VISIBLE);
        botonAdministrarEvento.setVisibility(View.GONE);
    }

    @Override
    public void enableAdministrarEvento() {
        botonParticipar.setVisibility(View.GONE);
        botonDejarParticipar.setVisibility(View.GONE);
        botonAdministrarEvento.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAllButtons() {
        botonParticipar.setVisibility(View.GONE);
        botonDejarParticipar.setVisibility(View.GONE);
        botonAdministrarEvento.setVisibility(View.GONE);
    }
}
