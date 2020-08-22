package com.example.cuidadodelambiente.data.network;


import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.data.models.ResultadoJsonAgregarEvento;
import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.data.models.EventoItem;
import com.example.cuidadodelambiente.data.models.UbicacionReporte;
import com.example.cuidadodelambiente.data.models.User;
import com.google.android.gms.common.data.DataBufferObserver;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @GET("eventos_usuario.php")
    Call<List<EventoLimpieza>> doGetEventosUsuario(@Query("id_ambientalista") Integer ambientalista);

    @GET("reportes_usuario.php")
    Call<List<ReporteContaminacion>> doGetReportesUsuario(@Query("id_ambientalista") Integer ambientalista);

    @GET("datos_reporte.php")
    Call<ReporteContaminacion> doGetReporteContaminacion(@Query("reporte_id") Integer reporte);

    @GET("datos_evento.php")
    Call<EventoLimpieza> doGetEventoLimpieza(
            @Query("evento_id") Integer evento,
            @Query("ambientalista_id") Integer ambientalista_id
    );

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

    @Multipart
    @POST("insertar_reporte.php")
    Call<JsonObject> doAgregarReporte(
            @Part("latitud") RequestBody latitud,
            @Part("longitud") RequestBody longitud,
            @Part("residuos[]") List<RequestBody> residuos,
            @Part("ambientalista_id") RequestBody id_ambientalista,
            @Part("volumen") RequestBody volumen,
            @Part("descripcion") RequestBody descripcion,
            @Part MultipartBody.Part file
    );

    @FormUrlEncoded
    @POST("insertar_unirse_evento.php")
    Call<JsonObject> doUnirseEvento(
            @Field("id_ambientalista") int id_ambientalista,
            @Field("id_evento") int id_evento,
            @Field("fecha_inicio") String fechaInicio,
            @Field("hora_inicio") String horaInicio,
            @Field("fecha_fin") String fechaFin,
            @Field("hora_fin") String horaFin
    );

    @FormUrlEncoded
    @POST("google_id_token.php")
    Call<JsonObject> doVerificarGoogleUser(
            @Field("id_token") String id_token
    );

    @FormUrlEncoded
    @POST("datos_usuario.php")
    Call<JsonObject> doCargarDatosUsuario(
            @Field("id_usuario") int id
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<JsonObject> doLogIn(
            @Field("email") String email,
            @Field("contrasenia") String contrasenia
    );

    @FormUrlEncoded
    @POST("signup.php")
    Call<JsonObject> doSignUp(
            @Field("email") String email,
            @Field("nombre") String nombre,
            @Field("contrasenia") String contrasenia
    );

    /*
    @Multipart
    @POST("image.php")
    Call<JsonObject> uploadImagen(@Part MultipartBody.Part file, @Part("name") RequestBody name);

     */

    @Multipart
    @POST("image.php")
    Call<JsonObject> uploadImage(@Part MultipartBody.Part file,
                                    @Part("file") RequestBody name);

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
