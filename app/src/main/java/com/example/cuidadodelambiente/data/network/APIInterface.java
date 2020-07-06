package com.example.cuidadodelambiente.data.network;

import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.data.models.ResultadoJsonAgregarEvento;
import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.data.models.EventoItem;
import com.example.cuidadodelambiente.data.models.UbicacionReporte;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("ubicaciones_eventos.php")
    Call<List<UbicacionEvento>> doGetEventos();

    @GET("ubicaciones_reportes.php")
    Call<List<UbicacionReporte>> doGetReportes();

    @GET("recomendaciones_eventos.php")
    Call<List<EventoItem>> doGetEventosRecomendados(@Query("id_ambientalista") Integer ambientalista);

    @GET("datos_participacion_evento.php")
    Call<List<EventoLimpieza>> doGetEventosParticipa(@Query("id_ambientalista") Integer ambientalista);

    @GET("datos_reporte.php")
    Call<ReporteContaminacion> doGetReporteContaminacion(@Query("reporte_id") Integer reporte);

    @GET("datos_evento.php")
    Call<EventoLimpieza> doGetEventoLimpieza(@Query("evento_id") Integer evento);

    @FormUrlEncoded
    @POST("insertar_evento.php")
    Call<ResultadoJsonAgregarEvento> doAgregarEvento(
            @Field("ambientalista_id") int id_ambientalista,
            @Field("reporte_id") int id_reporte,
            @Field("titulo") String titulo,
            @Field("fecha") String fecha,
            @Field("hora") String hora,
            @Field("descripcion") String descripcion
    );

}

/*

@GET("insertar_evento.php")
    Call<ResultadoJsonAgregarEvento> doAgregarEvento(
            @Query("ambientalista_id") int id_ambientalista,
            @Query("reporte_id") int id_reporte,
            @Query("titulo") String titulo,
            @Query("fecha") String fecha,
            @Query("hora") String hora,
            @Query("descripcion") String descripcion
    );
 */
