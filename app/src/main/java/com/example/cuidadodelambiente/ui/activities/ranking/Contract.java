package com.example.cuidadodelambiente.ui.activities.ranking;

import com.example.cuidadodelambiente.data.models.UserRank;
import com.example.cuidadodelambiente.ui.BasePresenter;

import java.util.List;

public interface Contract {
    interface View {
        void showLoading();
        void hideLoading();
        void showError(String error);
        void hideError();
        void showRanking(List<UserRank> ranking, UserRank userRank);
    }

    interface Presenter extends BasePresenter {
        void fetchRanking();
        void onfetchRankingError(String error);
        void onfetchRankingExito(List<UserRank> ranking, UserRank userRank);
    }

    interface Model {
        void fetchRanking();
    }
}
