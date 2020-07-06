package com.example.cuidadodelambiente;


import androidx.fragment.app.Fragment;

/* Clase para llevar el control del actual fragment en la app */
public class FragmentSingleton {
    private static Fragment currentFragment = null;
    private static Fragment oldFragment = null;

    private FragmentSingleton()
    {
        // constructor vacío
    }

    public static Fragment getCurrentFragment()
    {
        if(currentFragment == null)
            return DeclaracionFragments.eventosLimpiezaFragmentFragement; // eventosLimpiezaFragment es el fragment home
        else
            return currentFragment;
    }

    public static void setCurrentFragment(Fragment fragment)
    {
        currentFragment = fragment;
    }

    public static Fragment getOldFragment() { return oldFragment; }

    public static void setOldFragment(Fragment fragment) { oldFragment = fragment; }

}
