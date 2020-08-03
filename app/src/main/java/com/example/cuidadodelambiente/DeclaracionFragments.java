package com.example.cuidadodelambiente;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.example.cuidadodelambiente.Fragments.CrearEventoFragment;
import com.example.cuidadodelambiente.Fragments.CrearReporteFragment;
import com.example.cuidadodelambiente.ui.activities.ActividadCrearEvento;
import com.example.cuidadodelambiente.ui.activities.ActividadCrearReporte;
import com.example.cuidadodelambiente.ui.fragments.MisEventosYReportesFragment;
import com.example.cuidadodelambiente.ui.fragments.PerfilUsuarioFragment;
import com.example.cuidadodelambiente.ui.fragments.eventos.view.EventosLimpiezaFragment;
import com.example.cuidadodelambiente.ui.fragments.participaciones.view.ParticipaEventosFragment;
import com.example.cuidadodelambiente.ui.fragments.reportes.view.ReportesContaminacionFragment;
import com.example.cuidadodelambiente.ui.fragments.recomendaciones.view.RecomendacionEventosFragment;


/* inicializa los fragments principales que se usan en la aplicacion para solo
* crearlos una vez */
public class DeclaracionFragments {
    public static int actualAmbientalista = 1;

    public static EventosLimpiezaFragment eventosLimpiezaFragmentFragement = new EventosLimpiezaFragment();
    public static final Fragment recomendacionEventosFragment = new RecomendacionEventosFragment();
    public static final Fragment recomendacionCrearEventoFragment = new ReportesContaminacionFragment();
    public static final Fragment participaEventos = new ParticipaEventosFragment();
    public static final Fragment perfilUsuario = new PerfilUsuarioFragment();
    public static final Fragment misEventosYReportes = new MisEventosYReportesFragment();
    public static CrearEventoFragment crearEventoFragment = new CrearEventoFragment();
    public static final Fragment crearReporteFragment = new CrearReporteFragment();
    public static final Activity crearReporteActivity = new ActividadCrearReporte();
    public static final ActividadCrearEvento crearEventoActivity = new ActividadCrearEvento();

    public static Fragment getParticipaEventosFragment()
    {
        return participaEventos;

    }
}
