package com.example.cuidadodelambiente.data.responses;

import com.example.cuidadodelambiente.data.models.UserRank;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RankingResponse {

    @SerializedName("ranking")
    private List<UserRank> ranking;

    @SerializedName("estatus")
    private StatusResponse estatus;

    public RankingResponse(List<UserRank> ranking, StatusResponse estatus) {
        this.ranking = ranking;
        this.estatus = estatus;
    }

    public List<UserRank> getRanking() {
        return ranking;
    }

    public void setRanking(List<UserRank> ranking) {
        this.ranking = ranking;
    }

    public StatusResponse getEstatus() {
        return estatus;
    }

    public void setEstatus(StatusResponse estatus) {
        this.estatus = estatus;
    }
}