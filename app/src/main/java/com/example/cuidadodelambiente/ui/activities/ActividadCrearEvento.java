package com.example.cuidadodelambiente.ui.activities;

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

public class ActividadCrearEvento extends AppCompatActivity {

    private final String TAG = ActividadCrearEvento.class.getSimpleName();

    private ActividadCrearEvento.AddressResultReceiver resultReceiver;
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
    private StringRequest stringRequest;
    private boolean banderaLlenarUbicacion = false; // para saber si se creará un evento desde la pantalla de reportes
    private boolean solicitandoDireccion;
    private int idReporte;
    private LatLng ubicacionReporte;
    private String addressOutput;
    private static ParaObservar observable = new ParaObservar();
    private TextView txtObtenerDireccion;
    private TextView txtLatitudLongitud;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;


    private Call<CrearEventoResponse> callAgregarEvento;

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

        tituloEvento = findViewById(R.id.editTextTitulo);
        txtDireccionEvento = findViewById(R.id.textViewDireccion);
        txtObtenerDireccion = findViewById(R.id.txtObtenerDireccion);
        txtObtenerDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!solicitandoDireccion) {
                    startIntentService();
                }
            }
        });

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

        resultReceiver = new AddressResultReceiver(new Handler());
        //observable.addObserver(DeclaracionFragments.eventosLimpiezaFragmentFragement);

        startIntentService();
    }

    private void inicializarProgressDialog() {
        progresoCrearEvento = new ProgressDialog(ActividadCrearEvento.this);
        progresoCrearEvento.setTitle("Creando evento");
        progresoCrearEvento.setMessage("Cargando");
        progresoCrearEvento.setCancelable(false);
        progresoCrearEvento.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callAgregarEvento != null) {
                    callAgregarEvento.cancel();
                }
            }
        });
    }

    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null || resultCode == Constants.FAILURE_RESULT) {
                //addressOutput = String.format("%s, %s", ubicacionReporte.latitude, ubicacionReporte.longitude);
                addressOutput = "Ocurrió un error";

            } else {
                // Display the address string
                // or an error message sent from the intent service.
                addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                if (addressOutput == null) {
                    //addressOutput = String.format("%s, %s", ubicacionReporte.latitude, ubicacionReporte.longitude);
                    addressOutput = "Ocurrió un error";
                }
            }

            mostrarDireccionEnTextView();
            solicitandoDireccion = false;
            actualizarUI();

        }

    }

    public static ParaObservar getObservable() {
        return observable;
    }

    private void mostrarDireccionEnTextView() {
        txtDireccionEvento.setText(addressOutput);
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

    private void actualizarUI() {
        if (solicitandoDireccion) {
            txtObtenerDireccion.setVisibility(View.INVISIBLE);
            txtDireccionEvento.setText("Obteniendo dirección ...");
        } else {
            txtObtenerDireccion.setVisibility(View.VISIBLE);
        }
    }

    protected void startIntentService() {
        solicitandoDireccion = true;
        actualizarUI();

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(ubicacionReporte.latitude);
        location.setLongitude(ubicacionReporte.longitude);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

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

    private boolean comprobarCampos() {
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
        if (!comprobarCampos())
            return;

        progresoCrearEvento.show();

        Log.e(TAG, String.valueOf(observable.countObservers()));

        final EventoLimpieza evento = crearEventoDatosInterfaz();

        int idUsuario = UserLocalStore.getInstance(getApplicationContext()).getUsuarioLogueado().getId();
        Log.e(TAG, "Usuario: " + String.valueOf(idUsuario));

        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        callAgregarEvento = service.doAgregarEvento(idUsuario, evento.getIdReporte(), evento.getTitulo(),
                evento.getFecha(), evento.getHora(), evento.getDescripcion());

        callAgregarEvento.enqueue(new Callback<CrearEventoResponse>() {
            @Override
            public void onResponse(Call<CrearEventoResponse> call, Response<CrearEventoResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Ocurrió un error", Toast.LENGTH_SHORT).show();
                    progresoCrearEvento.dismiss();
                }

                if (response.body().getStatus().getResultado() == 1) {
                    evento.setIdEvento(response.body().getIdEvento());
                    Toast.makeText(getApplicationContext(), "Evento creado con éxito", Toast.LENGTH_SHORT).show();
                    getObservable().notificar(evento);
                    //((MainActivity) getActivity())
                            //.cambiarFragment(DeclaracionFragments.eventosLimpiezaFragmentFragement, "EVENTOS");
                } else {
                    Toast.makeText(getApplicationContext(),
                            response.body().getStatus().getMensaje(), Toast.LENGTH_SHORT).show();
                }

                progresoCrearEvento.dismiss();
            }

            @Override
            public void onFailure(Call<CrearEventoResponse> call, Throwable t) {
                if (call.isCanceled()) {
                    Log.e(TAG, "SE canceló la creación del evento");
                }

                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

                progresoCrearEvento.dismiss();
            }
        });

    }
}
