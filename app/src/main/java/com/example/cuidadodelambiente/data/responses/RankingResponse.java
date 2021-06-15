package com.example.cuidadodelambiente.data.responses;

import com.example.cuidadodelambiente.data.models.UserRank;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RankingResponse {

    @SerializedName("ranking")
    private List<UserRank> ranking;

    @SerializedName("ranking_usuario")
    private UserRank rankingUsuario;

    @SerializedName("estatus")
    private StatusResponse estatus;

    public RankingResponse(List<UserRank> ranking, UserRank rankingUsuario, StatusResponse estatus) {
        this.ranking = ranking;
        this.rankingUsuario = rankingUsuario;
        this.estatus = estatus;
    }

    public UserRank getRankingUsuario() {
        return rankingUsuario;
    }

    public void setRankingUsuario(UserRank rankingUsuario) {
        this.rankingUsuario = rankingUsuario;
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