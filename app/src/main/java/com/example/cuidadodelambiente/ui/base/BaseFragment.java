package com.example.cuidadodelambiente.ui.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cuidadodelambiente.helpers.HelperCargaError;

public class BaseFragment extends Fragment implements MvpView {
    private BaseActivity mActivity;
    protected HelperCargaError helperCargaError;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
        }
    }

    @Override
    public boolean isNetworkConnected() {
        if (mActivity != null) {
            mActivity.isNetworkConnected();
        }
        return false;
    }

    @Override
    public void showLoading() {
        if (helperCargaError != null)
            helperCargaError.mostrarPantallaCarga();
    }

    @Override
    public void showError() {
        if (helperCargaError != null)
            helperCargaError.mostrarPantallaError();
    }
}
