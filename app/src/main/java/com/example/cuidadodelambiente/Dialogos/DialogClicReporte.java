package com.example.cuidadodelambiente.Dialogos;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.cuidadodelambiente.DeclaracionFragments;
import com.example.cuidadodelambiente.Fragments.CrearEventoFragment;
import com.example.cuidadodelambiente.R;
import com.example.cuidadodelambiente.Utilidades;


/* Diálogo que se muestra al hacer clic en un reporte de contaminación */
public class DialogClicReporte extends DialogFragment {

    private Button botonCrearEvento;
    private Button botonCancelar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_clic_reporte, null);
        builder.setView(v);

        botonCrearEvento = v.findViewById(R.id.botonCrearEvento);
        botonCrearEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                        new CrearEventoFragment());
                dismiss();
            }
        });

        botonCancelar = v.findViewById(R.id.botonCancelar);
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


            /*
            // evento clic del botón aceptar
            builder.setPositiveButton("Crear evento", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Utilidades.iniciarFragment(getFragmentManager().beginTransaction(),
                            new CrearEventoFragment());
                }
            })

            // evento clic del botón cancelar
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            */

        return builder.create();
    }
}
