package com.example.cuidadodelambiente.ui.activities.ranking;

import com.example.cuidadodelambiente.data.models.UserRank;

import java.util.List;

public class RankingPresenter implements Contract.Presenter {

    private Contract.View view;
    private Contract.Model model;

    public RankingPresenter(Contract.View view) {
        this.view = view;
        this.model = new RankingModel(this);
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void fetchRanking() {
        if (view == null) return;

        view.hideError();
        view.showLoading();
        model.fetchRanking();
    }

    @Override
    public void onfetchRankingError(String error) {
        if (view == null) return;

        view.hideLoading();
        view.showError(error);

    }

    @Override
    public void onfetchRankingExito(List<UserRank> ranking, UserRank userRank) {
        if (view == null) return;

        view.hideLoading();
        view.hideError();
        view.showRanking(ranking, userRank);

    }
}
