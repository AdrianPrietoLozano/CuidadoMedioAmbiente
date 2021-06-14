package com.example.cuidadodelambiente.data.models;

import com.google.gson.annotations.SerializedName;

public class UserRank {

    @SerializedName("id")
    private Integer id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("puntos")
    private Integer puntos;

    @SerializedName("rank")
    private Integer rank;

    public UserRank(Integer id, String nombre, Integer puntos, Integer rank) {
        this.id = id;
        this.nombre = nombre;
        this.puntos = puntos;
        this.rank = rank;
    }

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

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}