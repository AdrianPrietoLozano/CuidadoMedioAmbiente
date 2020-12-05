package com.example.cuidadodelambiente.prueba;


import android.util.Log;

import com.example.cuidadodelambiente.data.network.APIInterface;
import com.example.cuidadodelambiente.data.network.RetrofitClientInstance;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Programa implements UsuarioPrueba.ClaseObservadora {

    private long tInicio;
    private long tFin;
    private int numUsuarios = 50;
    private int numReportes = numUsuarios * 2;
    private int errores = 0;
    private int bien = 0;
    private int numRecomendaciones = numUsuarios;
    private int numEventos = numUsuarios * 2;
    private int numParticipaciones = numUsuarios * 3;

    private Object lockEventos = new Object();
    private Object lockParticipaciones = new Object();
    private Object lockRecomendaciones = new Object();

    private boolean flag1 = false;
    private boolean flag2 = false;
    private boolean flag3 = false;

    private int[] reportes = new int[numReportes];
    private UsuarioPrueba[] usuarios = new UsuarioPrueba[numUsuarios];
    private ArrayList<Integer> eventos = new ArrayList<>();

    /*
    DELETE FROM `participa_evento`;
    DELETE FROM `KNN`;
    DELETE FROM `evento_limpieza`;
    * */

    public Programa() {
        // llenar arreglo de reportes
        for (int i = 0; i < reportes.length; i++) {
            reportes[i] = i + 1;
        }

        // crear usuarios
        for (int i = 0; i < usuarios.length; i++) {
            usuarios[i] = new UsuarioPrueba(i + 1, this);
        }

    }

    private void inicializarDatos() {
        this.errores = 0;
        this.bien = 0;
    }

    public void iniciar() {

        System.out.println("Inicio");

        iniciarCrearEventos();
        //eliminarTablas();

    }

    private void iniciarCrearEventos() {
        inicializarDatos();
        int numCrearEvento = this.numEventos / this.numUsuarios;
        this.tInicio = System.nanoTime();

        int i = 0;
        for (UsuarioPrueba u : usuarios) {
            for (int j = 0; j < numCrearEvento; j++) {
                u.crearEvento(reportes[i]);
                i++;
            }
        }

    }

    private void iniciarUnirseParticipar() {
        inicializarDatos();
        int numPart = this.numParticipaciones / this.numUsuarios;
        Random rand = new Random();
        this.tInicio = System.nanoTime();

        for (UsuarioPrueba u : usuarios) {
            //Collections.shuffle(this.eventos);
            for (int i = 0; i < numPart; i++) {
                u.participarEnEvento(this.eventos.get(rand.nextInt(this.eventos.size())));
            }
        }

    }

    private void iniciarRecomendaciones() {
        inicializarDatos();
        this.tInicio = System.nanoTime();

        for (UsuarioPrueba u : usuarios)
            u.obtenerRecomendaciones();
    }

    private void imprimirResultados(int total, String nombre) {
        double tiempo = (tFin - tInicio) / 1e6;
        String res = String.format("\nTiempo: %10.3f ms\nCorrectos: %d\nErrores: %d\nPromedio: %10.2f ms",
                tiempo, bien, errores, tiempo / total);
        Log.e(nombre, res);

    }


    @Override
    public void onResponseEvento(boolean exito, Integer idEvento) {
        if (exito) {
            this.bien++;
            this.eventos.add(idEvento);
        }
        else this.errores++;

        synchronized (lockEventos) {
            if (this.bien + this.errores >= this.numEventos && !flag1) {
                this.tFin = System.nanoTime();
                imprimirResultados(this.numEventos, "EVENTOS");
                iniciarUnirseParticipar();
                flag1 = true;
            }
        }
    }

    @Override
    public void onResponseParticipacion(boolean exito) {
        if (exito) this.bien++;
        else this.errores++;

        synchronized (lockParticipaciones) {
            if (this.bien + this.errores >= this.numParticipaciones && !flag2) {
                this.tFin = System.nanoTime();
                imprimirResultados(this.numParticipaciones, "PARTICIPACIONES");
                iniciarRecomendaciones();
                flag2 = true;
            }
        }
    }

    @Override
    public void onResponseRecomendacion(boolean exito) {
        if (exito) this.bien++;
        else this.errores++;

        synchronized (lockRecomendaciones) {
            if (this.bien + this.errores >= this.numRecomendaciones && !flag3) {
                this.tFin = System.nanoTime();
                imprimirResultados(this.numRecomendaciones, "RECOMENDACIONES");
                flag3 = true;
                eliminarTablas();
            }
        }

    }

    private void eliminarTablas() {
        APIInterface service = RetrofitClientInstance.getRetrofitInstance().create(APIInterface.class);
        service.doEliminarTablas().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                System.out.println("ELIMINADOS");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

}
