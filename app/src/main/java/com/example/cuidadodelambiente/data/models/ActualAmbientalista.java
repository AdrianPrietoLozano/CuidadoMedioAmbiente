package com.example.cuidadodelambiente.data.models;


public class ActualAmbientalista {

    private static ActualAmbientalista instance;

    private Integer id;
    private String nombre;
    private String email;
    private Integer puntos;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    private ActualAmbientalista() {

    }

    synchronized public static ActualAmbientalista getInstance() {
        if (instance == null) {
            instance = new ActualAmbientalista();
        }

        return instance;
    }

}
