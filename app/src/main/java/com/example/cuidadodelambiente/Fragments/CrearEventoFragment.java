package com.example.cuidadodelambiente.Fragments;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.Entidades.ReporteContaminacion;
import com.example.cuidadodelambiente.Entidades.VolleySingleton;
import com.example.cuidadodelambiente.FetchAddressIntentService;
import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import android.os.ResultReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrearEventoFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private AddressResultReceiver resultReceiver;
    private TextView fechaView;
    private TextView horaView;
    private EditText ubicacionEvento;
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
    private int idReporte;
    // este atributo se necesitan solo cuando se crea un evento desde un DialogClicReporte
    private LatLng ubicacionReporte;
    OnEventoCreado onEventoCreado;
    String addressOutput;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null || resultCode == Constants.FAILURE_RESULT) {
                addressOutput = String.format("%s, %s", ubicacionReporte.latitude, ubicacionReporte.longitude);
                mostrarUbicacionEnTextView(addressOutput);
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (addressOutput == null) {
                addressOutput = String.format("%s, %s", ubicacionReporte.latitude, ubicacionReporte.longitude);
            }

            mostrarUbicacionEnTextView(addressOutput);

        }

    }

    private void mostrarUbicacionEnTextView(final String direccionCompleta) {

        if(!this.isRemoving()) { // ESTO ESTA MAL

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ubicacionEvento.setText(direccionCompleta);
                }
            });
        }
    }


    public static CrearEventoFragment newInstance(int idReporte, double latitud, double longitud)
    {
        CrearEventoFragment f = new CrearEventoFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putDouble("latitud", latitud);
        args.putDouble("longitud", longitud);
        args.putInt("idReporte", idReporte);
        f.setArguments(args);

        return f;
    }


    public CrearEventoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        // si se pasaron parametros a la vista
        if(arguments != null)
        {
            idReporte = arguments.getInt("idReporte");
            ubicacionReporte = new LatLng(arguments.getDouble("latitud"),
                    arguments.getDouble("longitud"));

            // indica que al crear la vista se debe llenar el campo de ubicación con los datos de ubicacionReporte
            banderaLlenarUbicacion = true;
        }

        resultReceiver = new AddressResultReceiver(null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crear_evento, container, false);

        Log.e("CREAREVENTO", "onCreateView");

        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);

        tituloEvento = v.findViewById(R.id.editTextTitulo);
        ubicacionEvento = v.findViewById(R.id.editTextUbicacion);
        descripcionEvento = v.findViewById(R.id.editTextDescripcion);

        fechaView = v.findViewById(R.id.textViewFecha);
        horaView = v.findViewById(R.id.textViewHora);

        fechaView.setOnClickListener(listenerFecha);
        horaView.setOnClickListener(listenerHora);

        botonAceptar = v.findViewById(R.id.botonAceptar);
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DEBE VERIFICAR QUE TODOS LOS CAMPOS ESTEN LLENOS Y QUE EXISTE EL idReporte
                // Debe verificar que el titulo del evento no este repetido
                clicBotonCrearEvento();
            }
        });

        botonCancelar = v.findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        if(banderaLlenarUbicacion == true) // si se debe llenar el campo de ubicación
        {
            //ubicacionEvento.setText(String.format("%s, %s", ubicacionReporte.latitude,
                    //ubicacionReporte.longitude));
            startIntentService();
        }

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onEventoCreado = (OnEventoCreado) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("Crear", "onDetach");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.e("Crear", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("Crear", "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Crear", "onDestroy");
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {

        super.onHiddenChanged(hidden);

        if(hidden) {
            Log.e("Crear", "hidden true");
        } else {
            Log.e("Crear", "hiden false");
        }
    }

    private View.OnClickListener listenerFecha = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            calendario = Calendar.getInstance();
            dia = calendario.get(calendario.DAY_OF_MONTH);
            mes = calendario.get(calendario.MONTH);
            anio = calendario.get(calendario.YEAR);

            datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener()
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

            timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener()
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

    protected void startIntentService() {
        Intent intent = new Intent(getContext(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(ubicacionReporte.latitude);
        location.setLongitude(ubicacionReporte.longitude);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        getContext().startService(intent);
    }


    private void clicBotonCrearEvento()
    {
        if(tituloEvento.getText().toString().equals("") || fechaView.getText().equals("") ||
            horaView.getText().equals("") || ubicacionEvento.getText().toString().equals("") ||
                descripcionEvento.getText().toString().equals(""))
        {
            Toast.makeText(getContext(), "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String url = getResources().getString(R.string.ip) + "EventosLimpieza/insertar_evento.php?" +
                    "ambientalista_id=" + DeclaracionFragments.actualAmbientalista +
                    "&reporte_id=" + idReporte +
                    "&titulo=" + tituloEvento.getText() +
                    "&fecha=" + fechaView.getText() +
                    "&hora=" + horaView.getText() +
                    "&descripcion=" + descripcionEvento.getText();

            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Cargando...");
            progreso.show();

            stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.equals("1"))
                            {
                                Toast.makeText(getContext(), "Éxito", Toast.LENGTH_SHORT).show();
                                onEventoCreado.onEventoCreado(ubicacionReporte);

                            }
                            else if(response.equals("2"))
                            {
                                Toast.makeText(getContext(), "Error: el evento para ese reporte ya existe",
                                        Toast.LENGTH_SHORT).show();
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

            VolleySingleton.getinstance(getContext()).addToRequestQueue(stringRequest);
        }
    }


    public interface OnEventoCreado
    {
        public void onEventoCreado(LatLng ubicacion);
    }

}
