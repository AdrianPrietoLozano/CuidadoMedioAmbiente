package com.example.cuidadodelambiente.ui.fragments.datos_evento;

import android.util.Log;

import com.example.cuidadodelambiente.AppContext;
import com.example.cuidadodelambiente.Constants;
import com.example.cuidadodelambiente.data.models.EventoLimpieza;
import com.example.cuidadodelambiente.data.models.UserLocalStore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

public class DatosEventoPresenter implements Contract.Presenter {

    private Contract.Model model;
    private Contract.View view;

    public DatosEventoPresenter(Contract.View view) {
        this.view = view;
        this.model = new DatosEventoModel(this);
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void fetchEvento(int idEvento) {
        if (view == null) return;

        view.hideError();
        view.showLoading();
        model.fetchEvento(idEvento);

    }

    @Override
    public void participarEnEvento(int idEvento) {
        if (view == null) return;

        view.showLoadingDialog();
        model.participarEnEvento(idEvento);
    }

    @Override
    public void dejarDeParticiparEnEvento(int idEvento) {
        if (view == null) return;

        view.showLoadingDialog();
        model.dejarDeParticiparEnEvento(idEvento);
    }

    @Override
    public void onEventoFetched(EventoLimpieza eventoLimpieza) {
        if (view == null) return;
        view.hideLoading();

        if (eventoLimpieza.getUsuarioParticipa())
            view.enableDejarParticipar();
        else
            view.enableParticipacion();

        int idUsuario = UserLocalStore.getInstance(AppContext.getContext()).getUsuarioLogueado().getId();
        boolean isVigente = isFechaVigente(eventoLimpieza.getFecha(), eventoLimpieza.getHora());
        if (!isVigente || idUsuario == eventoLimpieza.getCreador().getId())
            view.hideAllButtons();

        if (!isVigente && eventoLimpieza.getCreador().getId() == idUsuario && eventoLimpieza.getAdministrado().equals("0"))
            view.enableAdministrarEvento();

        view.showEvento(eventoLimpieza);
    }

    @Override
    public void onParticiparEnEventoExito() {
        if (view == null) return;
        view.hideLoadingDialog();
        view.showMessage("Éxito");
        view.onParticiparEventoExito();
    }

    @Override
    public void onDejarParticiparEventoExito() {
        if (view == null) return;
        view.hideLoadingDialog();
        view.showMessage("Éxito");
        view.onDejarParticiparEventoExito();
    }

    @Override
    public void onFetchEventoError(String error) {
        if (view == null) return;
        view.hideLoading();
        view.showError(error);
    }

    @Override
    public void onParticiparEnEventoError(String error) {
        if (view == null) return;
        view.hideLoadingDialog();
        view.showMessage(error);
    }

    @Override
    public void onDejarParticiparEventoError(String error) {
        if (view == null) return;
        view.hideLoadingDialog();
        view.showMessage(error);
    }

    @Override
    public void fetchRecomendacionesEvento(int idEvento) {
        if (view == null) return;

        model.fetchRecomendacionesEvento(idEvento);
    }

    @Override
    public void onRecomnedacionesFetched(List<EventoLimpieza> eventos) {
        if (view == null) return;

        view.showRecomendaciones(eventos);
    }

    @Override
    public void onRecomendacionesError(String error) {
        if (view == null) return;

    }

    private boolean isFechaVigente(String fecha, String hora) {
        SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Constants.LOCALE_MX);
        boolean isFechaEventoVigente = true;
        try {
            Date dateEvento = parser.parse(fecha + " " + hora);
            Date now = new Date();
            if (dateEvento.before(now))
                isFechaEventoVigente = false; // la fecha ya pasó
        } catch (Exception e) {
            isFechaEventoVigente = true;
        }

        return isFechaEventoVigente;
    }
}
