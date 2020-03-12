package com.example.cuidadodelambiente.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cuidadodelambiente.R;


public class CrearReporteFragment extends Fragment {

    public CrearReporteFragment() {
        // Required empty public constructor
    }

    public static CrearReporteFragment newInstance(String param1, String param2) {
        CrearReporteFragment fragment = new CrearReporteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);

        return inflater.inflate(R.layout.fragment_crear_reporte, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("CrearReporte", "onDestroy");
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }
}
