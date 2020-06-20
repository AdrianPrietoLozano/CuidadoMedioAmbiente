package com.example.cuidadodelambiente.data.network;

import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.data.models.Evento;
import com.example.cuidadodelambiente.EventoItem;
import com.example.cuidadodelambiente.ParticipaEventoItem;
import com.example.cuidadodelambiente.data.models.UbicacionReporte;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("ubicaciones_eventos.php")
    Call<List<Evento>> doGetEventos();

    @GET("ubicaciones_reportes.php")
    Call<List<UbicacionReporte>> doGetReportes();

    @GET("recomendaciones_eventos.php")
    Call<List<EventoItem>> doGetEventosRecomendados(@Query("id_ambientalista") Integer ambientalista);

    @GET("datos_participacion_evento.php")
    Call<List<ParticipaEventoItem>> doGetEventosParticipa(@Query("id_ambientalista") Integer ambientalista);

    @GET("datos_reporte.php")
    Call<ReporteContaminacion> doGetReporteContaminacion(@Query("reporte_id") Integer reporte);

}
