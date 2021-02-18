package com.example.cuidadodelambiente.data.network;


import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.ReporteContaminacion;
import com.example.cuidadodelambiente.data.models.UbicacionEvento;
import com.example.cuidadodelambiente.data.models.UbicacionReporte;
import com.example.cuidadodelambiente.data.models.User;
import com.example.cuidadodelambiente.data.responses.CercaMedioLejos;
import com.example.cuidadodelambiente.data.responses.CrearEventoResponse;
import com.example.cuidadodelambiente.data.responses.EventoLimpiezaResponse;
import com.example.cuidadodelambiente.data.responses.ReporteContaminacionResponse;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    /* LOGIN */
    @GET("usuario/{idUsuario}")
    Call<JsonObject> doCargarDatosUsuario(
            @Path("idUsuario") int id
    );

    @FormUrlEncoded
    @POST("usuario/login")
    Call<JsonObject> doLogIn(
            @Field("email") String email,
            @Field("contrasenia") String contrasenia,
            @Field("fcm_token") String fcmToken
    );

    @FormUrlEncoded
    @POST("usuario/google/login")
    Call<JsonObject> doVerificarGoogleUser(
            @Field("id_token") String id_token,
            @Field("fcm_token") String fcmToken
    );

    @FormUrlEncoded
    @POST("usuario/registrar")
    Call<JsonObject> doSignUp(
            @Field("email") String email,
            @Field("nombre") String nombre,
            @Field("contrasenia") String contrasenia,
            @Field("fcm_token") String fcmToken
    );


    /* EVENTOS */

    @GET("eventos")
    Call<List<UbicacionEvento>> doGetEventos();

    @GET("eventos/{idEvento}")
    Call<EventoLimpiezaResponse> doGetEventoLimpieza(
            @Path("idEvento") Integer evento
    );

    @FormUrlEncoded
    @POST("eventos")
    Call<CrearEventoResponse> doAgregarEvento(
            @Field("reporte_id") Integer id_reporte,
            @Field("titulo") String titulo,
            @Field("fecha") String fecha,
            @Field("hora") String hora,
            @Field("descripcion") String descripcion
    );

    @GET("eventos/usuario")
    Call<List<EventoLimpieza>> doGetEventosUsuario();


    /*---------------------- REPORTES -----------------------*/

    @GET("reportes")
    Call<List<UbicacionReporte>> doGetReportes();

    @GET("reportes/{idReporte}")
    Call<ReporteContaminacionResponse> doGetReporteContaminacion(
            @Path("idReporte") Integer reporte
    );

    @GET("reportes/usuario")
    Call<List<ReporteContaminacion>> doGetReportesUsuario();

    @Multipart
    @POST("reportes")
    Call<JsonObject> doAgregarReporte(
            @Part("latitud") RequestBody latitud,
            @Part("longitud") RequestBody longitud,
            @Part("residuos[]") List<RequestBody> residuos,
            @Part("volumen") RequestBody volumen,
            @Part("descripcion") RequestBody descripcion,
            @Part MultipartBody.Part file
    );





    /*------------ PARTICIPACIONES ----------------*/

    @GET("participaciones/usuario")
    Call<List<EventoLimpieza>> doGetEventosParticipa();

    @FormUrlEncoded
    @POST("participaciones/usuario")
    Call<JsonObject> doUnirseEvento(
            @Field("idEvento") Integer id_evento
    );

    @DELETE("participaciones/usuario/{idEvento}")
    Call<JsonObject> doDejarParticiparEvento(
            @Path("idEvento") Integer id_evento
    );

    @GET("participaciones/evento/{idEvento}")
    Call<List<User>> doGetParticipacionesEvento(
            @Path("idEvento") Integer id_evento
    );

    @FormUrlEncoded
    @POST("participaciones/evento/{idEvento}")
    Call<JsonObject> doEnviarIdsParticipantes(
            @Path("idEvento") Integer id_evento,
            @Field("idUsuario") Integer id_usuario,
            @Field("idsParticipantes[]") List<Integer> idsParticipantes
    );





    /* RECOMENDACIONES */

    @GET("recomendaciones/usuario")
    Call<List<EventoLimpieza>> doGetEventosRecomendados();



    /* PRUEBAS DEL SERVICIO SOCIAL */

    @GET("eliminar_tablas.php")
    Call<JsonObject> doEliminarTablas();


    /* BÚSQUEDAS */

    @GET("busqueda/eventos/{query}")
    Call<List<EventoLimpieza>> doBusquedaEventos(@Path("query") String query);


    /* LIMPIEZAS */

    @Multipart
    @POST("insertar_limpieza.php")
    Call<JsonObject> doAgregarLimpieza(
            @Part("reporte_id") RequestBody id_reporte,
            @Part("descripcion") RequestBody descripcion,
            @Part MultipartBody.Part imagen
    );


    /* PRUEBA IMAGEN MAPA */
    @GET("recomendaciones/prueba/{idUsuario}")
    Call<CercaMedioLejos> doGetCercaMedioLejos(@Path("idUsuario") Integer ambientalista);


    /*
    @Multipart
    @POST("image.php")
    Call<JsonObject> uploadImagen(@Part MultipartBody.Part file, @Part("name") RequestBody name);

     */

    @Multipart
    @POST("image.php")
    Call<JsonObject> uploadImage(@Part MultipartBody.Part file,
                                    @Part("file") RequestBody name);


    //////////////////////////////////////////////////////////////////



}

