package com.example.cuidadodelambiente;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class RunDatabaseHelper extends SQLiteOpenHelper {

    // el constructor crea la base de datos
    public RunDatabaseHelper(@Nullable Context context) {
        super(context, TablasBD.DB_NAME, null, TablasBD.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TablasBD.SQL_TABLA_AMBIENTALISTA);
        db.execSQL(TablasBD.SQL_TABLA_EVENTO);
        db.execSQL(TablasBD.SQL_TABLA_TIPO);
        db.execSQL(TablasBD.SQL_TABLA_VOLUMEN);
        db.execSQL(TablasBD.SQL_TABLA_REPORTE);
        db.execSQL(TablasBD.SQL_TABLA_RECOMENDACION_EVENTO);
        db.execSQL(TablasBD.SQL_TABLA_RECOMENDACION_CREAR_EVENTO);
        db.execSQL(TablasBD.SQL_TABLA_PARTICIPA);

        System.out.println("BIEN");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
