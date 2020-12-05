package com.example.cuidadodelambiente.data.network;


import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.data.models.UbicacionReporte;
import com.example.cuidadodelambiente.data.responses.CrearEventoResponse;
import com.example.cuidadodelambiente.data.responses.EventoLimpiezaResponse;
import com.example.cuidadodelambiente.data.responses.ReporteContaminacionResponse;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("eliminar_tablas.php")
    Call<JsonObject> doEliminarTablas();

    @GET("ubicaciones_eventos.php")
    Call<List<UbicacionEvento>> doGetEventos();

    @GET("ubicaciones_reportes.php")
    Call<List<UbicacionReporte>> doGetReportes();

    @GET("recomendaciones_eventos.php")
    Call<List<EventoLimpieza>> doGetEventosRecomendados(@Query("id_ambientalista") Integer ambientalista);

    @GET("datos_participacion_evento.php")
    Call<List<EventoLimpieza>> doGetEventosParticipa(@Query("id_ambientalista") Integer ambientalista);

    @GET("eventos_usuario.php")
    Call<List<EventoLimpieza>> doGetEventosUsuario(@Query("id_ambientalista") Integer ambientalista);

    @GET("reportes_usuario.php")
    Call<List<ReporteContaminacion>> doGetReportesUsuario(@Query("id_ambientalista") Integer ambientalista);

    @GET("datos_reporte.php")
    Call<ReporteContaminacionResponse> doGetReporteContaminacion(@Query("reporte_id") Integer reporte);

    @GET("datos_evento.php")
    Call<EventoLimpiezaResponse> doGetEventoLimpieza(
            @Query("evento_id") Integer evento,
            @Query("ambientalista_id") Integer ambientalista_id
    );

    @FormUrlEncoded
    @POST("insertar_evento.php")
    Call<CrearEventoResponse> doAgregarEvento(
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
            @Field("id_evento") int id_evento
    );

    @Multipart
    @POST("insertar_limpieza.php")
    Call<JsonObject> doAgregarLimpieza(
            @Part("reporte_id") RequestBody id_reporte,
            @Part("ambientalista_id") RequestBody id_ambientalista,
            @Part("descripcion") RequestBody descripcion,
            @Part MultipartBody.Part imagen
    );

    @FormUrlEncoded
    @POST("dejar_participar_evento.php")
    Call<JsonObject> doDejarParticiparEvento(
            @Field("id_ambientalista") int id_ambientalista,
            @Field("id_evento") int id_evento
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
    @POST("login_google.php")
    Call<JsonObject> doVerificarGoogleUser(
            @Field("id_token") String id_token
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

