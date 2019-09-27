package com.example.cuidadodelambiente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class DemoCollectionPagerAdapter extends FragmentPagerAdapter {
    public DemoCollectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;

        switch(i)
        {
            case 0:
                return new EventosLimpieza();

            case 1:
                return new RecomendacionEventosFragment();
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
                title = "Todos los eventos";
                break;

            case 1:
                title = "Eventos para ti";
                break;
        }

        return title;
    }
}
