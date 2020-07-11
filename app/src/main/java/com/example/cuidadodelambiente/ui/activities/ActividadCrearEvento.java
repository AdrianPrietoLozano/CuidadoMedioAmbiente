package com.example.cuidadodelambiente.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.FetchAddressIntentService;
import com.example.cuidadodelambiente.Fragments.CrearEventoFragment;
import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.ParaObservar;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.ResultadoJsonAgregarEvento;
import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActividadCrearEvento extends AppCompatActivity {

    private ActividadCrearEvento.AddressResultReceiver resultReceiver;
    private TextView fechaView;
    private TextView horaView;
    private TextView txtDireccionEvento;
    private EditText tituloEvento;
    private EditText descripcionEvento;
    private Button botonCancelar;
    private Button botonAceptar;
    private int dia, mes, anio, hora, minutos;
    private Calendar calendario;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private ProgressDialog progreso;
    private StringRequest stringRequest;
    private boolean banderaLlenarUbicacion = false; // para saber si se creará un evento desde la pantalla de reportes
    private boolean solicitandoDireccion;
    private int idReporte;
    private LatLng ubicacionReporte;
    CrearEventoFragment.OnEventoCreado onEventoCreado;
    private String addressOutput;
    public ParaObservar observable = new ParaObservar();
    private TextView txtObtenerDireccion;
    private TextView txtLatitudLongitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_crear_evento);


        // Cambia el color del status bar a verde
        Window w = getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        w.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.verde3));


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
            Toast.makeText(getApplicationContext(), "extras == null", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            idReporte = extras.getInt("ID_REPORTE");
            ubicacionReporte = new LatLng(extras.getDouble("LATITUD"),
                    extras.getDouble("LONGITUD"));
            txtLatitudLongitud.setText(String.valueOf(ubicacionReporte.latitude)
                    .concat(", ")
                    .concat(String.valueOf(ubicacionReporte.longitude)));
        }

        resultReceiver = new AddressResultReceiver(new Handler());
        observable.addObserver(DeclaracionFragments.eventosLimpiezaFragmentFragement);

        startIntentService();
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

            mostrarUbicacionEnTextView();
            solicitandoDireccion = false;
            actualizarUI();

        }

    }

    private void mostrarUbicacionEnTextView() {
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


    private void clicBotonCrearEvento()
    {
        if(tituloEvento.getText().toString().equals("") || fechaView.getText().equals("") ||
                horaView.getText().equals("") || txtDireccionEvento.getText().toString().equals("") ||
                descripcionEvento.getText().toString().equals(""))
        {
            // falta hacer mas comprobaciones
            Toast.makeText(getApplicationContext(), "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
            tituloEvento.setError("Campo obligatorio");
        }
        else
        {
            final EventoLimpieza evento = new EventoLimpieza();
            evento.setIdReporte(idReporte);
            //evento.setAmbientalista(//nombre del ambientalista);
            evento.setTitulo(tituloEvento.getText().toString());
            evento.setFecha(fechaView.getText().toString());
            evento.setHora(horaView.getText().toString());
            evento.setDescripcion(descripcionEvento.getText().toString());
            evento.setUbicacion(ubicacionReporte);


            APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
            service.doAgregarEvento(DeclaracionFragments.actualAmbientalista,
                    evento.getIdReporte(),
                    evento.getTitulo(),
                    evento.getFecha(),
                    evento.getHora(),
                    evento.getDescripcion())
                    .enqueue(new Callback<ResultadoJsonAgregarEvento>() {
                        @Override
                        public void onResponse(Call<ResultadoJsonAgregarEvento> call, Response<ResultadoJsonAgregarEvento> response) {
                            ResultadoJsonAgregarEvento json = response.body();
                            evento.setIdEvento(json.getIdEvento());

                            Log.e("RESULTADO", String.valueOf(json.getResultado()));
                            Log.e("MENSAJE", json.getMensaje());
                            Log.e("ID_EVENTO", String.valueOf(json.getIdEvento()));

                            if (json.getResultado() == 1) {
                                Toast.makeText(getApplicationContext(), json.getMensaje(), Toast.LENGTH_SHORT).show();
                                observable.notificar(evento);
                                //((MainActivity) getActivity())
                                        //.cambiarFragment(DeclaracionFragments.eventosLimpiezaFragmentFragement, "EVENTOS");
                            } else {
                                Toast.makeText(getApplicationContext(), json.getMensaje(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultadoJsonAgregarEvento> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    // pasar contexto a Calligraphy
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }
}
