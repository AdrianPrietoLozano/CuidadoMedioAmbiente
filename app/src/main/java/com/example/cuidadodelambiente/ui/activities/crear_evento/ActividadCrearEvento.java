package com.example.cuidadodelambiente.ui.activities.crear_evento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.FetchAddressIntentService;
import com.example.cuidadodelambiente.ParaObservar;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.UserLocalStore;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.example.cuidadodelambiente.data.responses.CrearEventoResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActividadCrearEvento extends AppCompatActivity implements Contract.View {

    private final String TAG = ActividadCrearEvento.class.getSimpleName();

    private TextView fechaView;
    private TextView horaView;
    private TextView txtDireccionEvento;
    private TextInputEditText tituloEvento;
    private TextInputEditText descripcionEvento;
    private Button botonCancelar;
    private Button botonAceptar;
    private int dia, mes, anio, hora, minutos;
    private Calendar calendario;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private ProgressDialog progresoCrearEvento;
    private int idReporte;
    private LatLng ubicacionReporte;
    private static ParaObservar observable = new ParaObservar();
    private TextView txtObtenerDireccion;
    private TextView txtLatitudLongitud;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;
    private Contract.Presenter presenter;

    public static ParaObservar getObservable() {
        return observable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_crear_evento);


        // Cambia el color del status bar a verde
        Utilidades.cambiarColorStatusBar(getWindow(),
                ContextCompat.getColor(getApplicationContext(), R.color.verde3));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Nuevo evento");

        presenter = new CrearEventoPresenter(this);

        tituloEvento = findViewById(R.id.editTextTitulo);
        txtDireccionEvento = findViewById(R.id.textViewDireccion);
        txtObtenerDireccion = findViewById(R.id.txtObtenerDireccion);
        txtObtenerDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getDireccionEvento(getApplicationContext(), ubicacionReporte);
            }
        });
        txtObtenerDireccion.setVisibility(View.INVISIBLE);

        txtLatitudLongitud = findViewById(R.id.txtLatitudLongitud);
        descripcionEvento = findViewById(R.id.editTextDescripcion);

        fechaView = findViewById(R.id.textViewFecha);
        horaView = findViewById(R.id.textViewHora);

        fechaView.setOnClickListener(listenerFecha);
        horaView.setOnClickListener(listenerHora);

        botonAceptar = findViewById(R.id.botonAceptar);
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DEBE VERIFICAR QUE TODOS LOS CAMPOS ESTEN LLENOS Y QUE EXISTE EL idReporte
                // Debe verificar que el titulo del evento no este repetido
                clicBotonCrearEvento();
            }
        });

        botonCancelar = findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();

        if(extras == null) {
            Toast.makeText(this, "extras == null", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            idReporte = extras.getInt("ID_REPORTE");
            ubicacionReporte = new LatLng(extras.getDouble("LATITUD"),
                    extras.getDouble("LONGITUD"));

            txtLatitudLongitud.setText(String.valueOf(ubicacionReporte.latitude)
                    .concat(", ")
                    .concat(String.valueOf(ubicacionReporte.longitude)));
        }

        inicializarProgressDialog();
        presenter.getDireccionEvento(getApplicationContext(), ubicacionReporte);
    }

    private void inicializarProgressDialog() {
        progresoCrearEvento = new ProgressDialog(ActividadCrearEvento.this);
        progresoCrearEvento.setTitle("Creando evento");
        progresoCrearEvento.setMessage("Cargando");
        progresoCrearEvento.setCancelable(false);
        progresoCrearEvento.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.cancelarCrearEvento();
            }
        });
    }

    @Override
    public void showLoading() {
        progresoCrearEvento.show();
    }

    @Override
    public void hideLoading() {
        progresoCrearEvento.dismiss();
    }

    @Override
    public void eventoCreado(EventoLimpieza evento) {
        Toast.makeText(getApplicationContext(), "Evento creado exitosamente", Toast.LENGTH_SHORT).show();
        getObservable().notificar(evento);
    }

    @Override
    public void onEventoCancelado() {
        Toast.makeText(getApplicationContext(), "Evento cancelado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cerrar() {
        Toast.makeText(getApplicationContext(), "Evento creado exitosamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDireccion(String direccion) {
        txtDireccionEvento.setText(direccion);
    }

    @Override
    public void showLoadingDireccion() {
        txtDireccionEvento.setText("Cargando...");
        txtObtenerDireccion.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showErrorDireccion() {
        showDireccion("Ocurrió un error");
        txtObtenerDireccion.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener listenerFecha = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            calendario = Calendar.getInstance();
            dia = calendario.get(calendario.DAY_OF_MONTH);
            mes = calendario.get(calendario.MONTH);
            anio = calendario.get(calendario.YEAR);

            datePickerDialog = new DatePickerDialog(ActividadCrearEvento.this, new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                {
                    fechaView.setText(String.format("%s/%s/%s", dayOfMonth, month+1, year));
                }
            }, anio, mes, dia);

            datePickerDialog.show();

        }
    };

    private View.OnClickListener listenerHora = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            calendario = Calendar.getInstance();
            hora = calendario.get(calendario.HOUR_OF_DAY);
            minutos = calendario.get(calendario.MINUTE);

            timePickerDialog = new TimePickerDialog(ActividadCrearEvento.this, new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                {
                    horaView.setText(hourOfDay + ":" + minute);
                }
            }, hora, minutos, true);

            timePickerDialog.show();
        }
    };

    private EventoLimpieza crearEventoDatosInterfaz() {
        EventoLimpieza evento = new EventoLimpieza();
        evento.setIdReporte(idReporte);
        evento.setTitulo(tituloEvento.getText().toString());
        evento.setFecha(fechaView.getText().toString());
        evento.setHora(horaView.getText().toString());
        evento.setDescripcion(descripcionEvento.getText().toString());
        evento.setUbicacion(ubicacionReporte);

        return evento;
    }

    private boolean validarCampos() {
        if (tituloEvento.getText().toString().equals("")) {
            tituloEvento.setError("Campo obligatorio");
            return false;
        }

        // falta validar la fecha
        if (fechaView.getText().toString().equals("")) { // o la fecha ya pasó
            fechaView.setError("Fecha inválida");
            return false;
        }

        // falta validar la hora
        if (horaView.getText().toString().equals("")) { // o la hora ya pasó
            horaView.setError("Hora inválida");
            return false;
        }

        if (descripcionEvento.getText().toString().equals("")) {
            descripcionEvento.setError("Campo obligatorio");
            return false;
        }

        return true;
    }


    private void clicBotonCrearEvento()
    {
        if (!validarCampos())
            return;

        EventoLimpieza evento = crearEventoDatosInterfaz();
        evento.setIdReporte(this.idReporte);

        presenter.crearEvento(evento);
    }
}
