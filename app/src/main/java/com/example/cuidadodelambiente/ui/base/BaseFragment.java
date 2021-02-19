package com.example.cuidadodelambiente.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cuidadodelambiente.R;
import com.google.android.material.appbar.MaterialToolbar;

public abstract class BaseFragment extends Fragment implements MvpView {
    private BaseActivity mActivity;
    private RelativeLayout layoutLoading;
    private RelativeLayout layoutError;
    private TextView mensajeError;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;

    public abstract void initView(View view, Bundle savedInstanceState);
    protected abstract int getLayoutResource();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResource(), container, false);
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbarTitle = rootView.findViewById(R.id.toolbar_title);
        layoutLoading = rootView.findViewById(R.id.pantallaCarga);
        layoutError = rootView.findViewById(R.id.layoutSinConexion);
        mensajeError = rootView.findViewById(R.id.mensajeProblema);
        initView(rootView, savedInstanceState);
        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            this.mActivity = (BaseActivity) context;
        }
    }

    public void initToolbar(String title, int bgColor, int textColor) {
        if (toolbar == null || toolbarTitle == null)
            return;

        toolbar.setBackgroundColor(bgColor);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().onBackPressed();
            }
        });

        toolbarTitle.setText(title);
        toolbarTitle.setTextColor(textColor);
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    @Override
    public boolean isNetworkConnected() {
        if (mActivity != null) {
            return mActivity.isNetworkConnected();
        }
        return false;
    }

    @Override
    public void showMessage(String msg) {
        if (mActivity != null) {
            mActivity.showMessage(msg);
        }
    }

    @Override
    public void showLoading() {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(View.VISIBLE);
            Log.e("LOADING", "SHOWING");
        }
    }

    @Override
    public void hideLoading() {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void showError(String error) {
        if (layoutError != null && mensajeError != null) {
            mensajeError.setText(error);
            layoutError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideError() {
        if (layoutError != null) {
            layoutError.setVisibility(View.GONE);
        }
    }
}
