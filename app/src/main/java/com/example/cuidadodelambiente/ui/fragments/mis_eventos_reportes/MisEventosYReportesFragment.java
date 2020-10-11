package com.example.cuidadodelambiente.ui.fragments.mis_eventos_reportes;


import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cuidadodelambiente.MainActivity;
import com.example.cuidadodelambiente.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class MisEventosYReportesFragment extends Fragment {

    private Chip chipEventos;
    private Chip chipReportes;
    private ChipGroup chipGroup;
    private Fragment fragmentEventos = new MisEventosFragment();
    private Fragment fragmentReportes = new MisReportesFragment();
    private Fragment currentFragment;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;

    public MisEventosYReportesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mis_eventos_yreportes, container, false);

        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        toolbarTitle = v.findViewById(R.id.toolbar_title);

        chipEventos = v.findViewById(R.id.chipEventos);
        chipEventos.setChecked(true);
        chipEventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != fragmentEventos) {
                    cambiarFragment(fragmentEventos);
                    currentFragment = fragmentEventos;
                    actualizarUI();
                }
            }
        });

        chipReportes = v.findViewById(R.id.chipReportes);
        chipReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment != fragmentReportes) {
                    cambiarFragment(fragmentReportes);
                    currentFragment = fragmentReportes;
                    actualizarUI();
                }
            }
        });

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragmentEventos)
                .commit();

        currentFragment = fragmentEventos;
        actualizarUI();

        Log.e("ADF", String.valueOf(getId()));

        ((MainActivity) getActivity()).cambiarVisibilidadBottomNavigation(View.GONE);

        return v;

    }

    private void cambiarFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (fragment.isAdded()) { // si ya se mostró antes
            transaction.hide(currentFragment).show(fragment);
        } else { // si es la primera vez en mostrarse
            transaction.hide(currentFragment)
                    .add(R.id.frameLayout, fragment);
        }

        transaction.commit();
    }

    private void actualizarUI() {
        if (currentFragment == fragmentEventos) {
            toolbarTitle.setText("Mis eventos");
        } else {
            toolbarTitle.setText("Mis reportes");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            ((MainActivity) getActivity()).cambiarVisibilidadBottomNavigation(View.VISIBLE);
        } else {
            ((MainActivity) getActivity()).cambiarVisibilidadBottomNavigation(View.GONE);
        }
    }
}
