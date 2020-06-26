package com.example.cuidadodelambiente;

import java.util.Observable;

public class ParaObservar extends Observable {
    public void notificar(Object o) {
        setChanged();
        notifyObservers(o);
    }
}
