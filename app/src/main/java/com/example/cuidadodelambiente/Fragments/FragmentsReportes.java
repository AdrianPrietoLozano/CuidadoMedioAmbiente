package com.example.cuidadodelambiente.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cuidadodelambiente.R;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentsReportes extends Fragment {

    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    DemoCollectionPagerAdapterReportes demoCollectionPagerAdapter;
    ViewPager viewPager;


    public FragmentsReportes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragments_reportes, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewPager = view.findViewById(R.id.viewPagerReportes);
        demoCollectionPagerAdapter = new DemoCollectionPagerAdapterReportes(getChildFragmentManager());
        viewPager.setAdapter(demoCollectionPagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout_reportes);
        tabLayout.setupWithViewPager(viewPager);
    }

}


// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
class DemoCollectionPagerAdapterReportes extends FragmentPagerAdapter {

    public DemoCollectionPagerAdapterReportes(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;

        switch(i)
        {
            case 0:
                return new ReportesContaminacionFragment();

            case 1:
                return new RecomendacionCrearEventoFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title = "";

        switch(position)
        {
            case 0:
                title = "Reportes de contaminación";
                break;

            case 1:
                title = "Crea un evento";
                break;
        }

        return title;
    }
}