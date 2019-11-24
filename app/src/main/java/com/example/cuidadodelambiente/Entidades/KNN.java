package com.example.cuidadodelambiente.Entidades;

import java.util.ArrayList;

public class KNN {
    private ArrayList<RegistroKNN> data;
    private ArrayList<Double> distancias;
    private RegistroKNN datosUsuarioActual;

    public KNN(ArrayList<RegistroKNN> data, RegistroKNN registro)
    {
        this.data = data;
        this.datosUsuarioActual = registro;
        distancias = new ArrayList<>();

        iniciar();
    }

    private double getDistanciaEntre(RegistroKNN r1, RegistroKNN r2)
    {
        int a1 = r1.getEscombro();
        int b1 = r1.getEnvases();
        int c1 = r1.getCarton();
        int d1 = r1.getBolsas();
        int e1 = r1.getElectricos();
        int f1 = r1.getPilas();
        int g1 = r1.getNeumaticos();
        int h1 = r1.getMedicamentos();
        int i1 = r1.getVarios();

        int a2 = r2.getEscombro();
        int b2 = r2.getEnvases();
        int c2 = r2.getCarton();
        int d2 = r2.getBolsas();
        int e2 = r2.getElectricos();
        int f2 = r2.getPilas();
        int g2 = r2.getNeumaticos();
        int h2 = r2.getMedicamentos();
        int i2 = r2.getVarios();

        double cuadrado1 = Math.pow(a1-a2, 2);
        double cuadrado2 = Math.pow(b1-b2, 2);
        double cuadrado3 = Math.pow(c1-c2, 2);
        double cuadrado4 = Math.pow(d1-d2, 2);
        double cuadrado5 = Math.pow(e1-e2, 2);
        double cuadrado6 = Math.pow(f1-f2, 2);
        double cuadrado7 = Math.pow(g1-g2, 2);
        double cuadrado8 = Math.pow(h1-h2, 2);
        double cuadrado9 = Math.pow(i1-i2, 2);

        double sumCuadrados = cuadrado1 + cuadrado2 + cuadrado3 + cuadrado4 + cuadrado5 +
                cuadrado6 + cuadrado7 + cuadrado8 + cuadrado9;

        return Math.sqrt(sumCuadrados);

    }

    private int determinarK()
    {
        double sizeDouble = (double) data.size();
        double root = Math.sqrt(sizeDouble);
        double rawK = root / 2;
        int num = Math.round((float) rawK);

        if(num % 2 == 0) // es par
        {
            num--;
        }

        return num;
    }


    private void iniciar()
    {
        for(RegistroKNN registro : data)
        {
            double distancia = getDistanciaEntre(datosUsuarioActual, registro);
            distancias.add(distancia);
        }

        // falta ordenar distancias
    }

    public void imprimirDistancias()
    {
        for(Double distancia : distancias)
        {
            System.out.println(distancia);
        }

        System.out.printf("K = %d%n", determinarK());
    }
}
