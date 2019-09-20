package com.example.cuidadodelambiente;

import android.provider.BaseColumns;
import android.provider.Telephony;

public final class TablasBD {

    public static final String DB_NAME = "eventos_medio_ambiente";
    public static final int VERSION = 1;

    // constructor
    public TablasBD() {}

    public static abstract class Ambientalista implements BaseColumns {
        public static final String NOMBRE = "ambientalista";
        public static final String COLUMNA_CORREO = "correo";
        public static final String COLUMNA_NOMBRE_USUARIO = "nombre_usuario";
        public static final String COLUMNA_CONTRASENIA = "contrasenia";
    }

    public static abstract class EventoLimpieza implements BaseColumns {
        public static final String NOMBRE = "evento_limpieza";
        public static final String COLUMNA_AMBIENTALISTA_ID = "ambientalista_id";
        public static final String COLUMNA_TITULO = "titulo";
        public static final String COLUMNA_REPORTE_ID = "reporte_id";
        public static final String COLUMNA_FECHA_HORA = "fecha";
        public static final String COLUMNA_DESCRIPCION = "descripcion";
    }

    public static abstract class ReporteContaminacion implements BaseColumns {
        public static final String NOMBRE = "reporte_contaminacion";
        public static final String COLUMNA_UBICACION = "ubicacion";
        public static final String COLUMNA_AMBIENTALISTA_ID = "ambientalista_id";
        public static final String COLUMNA_FOTO = "fotografia";
        public static final String COLUMNA_DESCRIPCION = "descripcion";
        public static final String COLUMNA_FECHA_HORA = "fecha_hora";
        public static final String COLUMNA_VOLUMEN_RESIDUO_ID = "volumen_residuo";
        public static final String COLUMNA_TIPO_RESIDUO_ID = "tipo_residuo";
    }

    public static abstract class VolumenResiduo implements BaseColumns {
        public static final String NOMBRE = "volumen_residuo";
        public static final String COLUMNA_VOLUMEN = "volumen";
    }

    public static abstract class TipoResiduo implements BaseColumns {
        public static final String NOMBRE = "tipo_residuo";
        public static final String COLUMNA_TIPO = "tipo";
    }

    public static abstract class ParticipaEvento implements BaseColumns {
        public static final String NOMBRE = "participa_evento";
        public static final String COLUMNA_AMBIENTALISTA_ID = "ambientalista_id";
        public static final String COLUMNA_EVENTO_ID = "evento_id";
        public static final String COLUMNA_FECHA_HORA_INICIO = "fecha_hora_inicio";
        public static final String COLUMNA_FECHA_HORA_FIN = "fecha_hora_fin";
    }

    public static abstract class RecomendacionEvento implements BaseColumns {
        public static final String NOMBRE = "recomendacion_evento";
        public static final String COLUMNA_AMBIENTALISTA_ID = "ambientalista_id";
        public static final String COLUMNA_EVENTO_ID = "evento_id";
    }

    public static abstract class RecomendacionCrearEvento implements BaseColumns {
        public static final String NOMBRE = "recomendacion_crear_evento";
        public static final String COLUMNA_AMBIENTALISTA_ID = "ambientalista_id";
        public static final String COLUMNA_REPORTE_ID = "reporte_id";
    }

    public static final String SQL_TABLA_AMBIENTALISTA = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "%s	TEXT NOT NULL," +
                    "%s	TEXT NOT NULL," +
                    "%s TEXT NOT NULL );",
            Ambientalista.NOMBRE,
            Ambientalista._ID,
            Ambientalista.COLUMNA_CORREO,
            Ambientalista.COLUMNA_NOMBRE_USUARIO,
            Ambientalista.COLUMNA_CONTRASENIA
    );

    public static final String SQL_TABLA_EVENTO = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "%s	INTEGER NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s INTEGER NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s));",
            EventoLimpieza.NOMBRE,
            EventoLimpieza._ID,
            EventoLimpieza.COLUMNA_AMBIENTALISTA_ID,
            EventoLimpieza.COLUMNA_TITULO,
            EventoLimpieza.COLUMNA_REPORTE_ID,
            EventoLimpieza.COLUMNA_FECHA_HORA,
            EventoLimpieza.COLUMNA_DESCRIPCION,
            EventoLimpieza.COLUMNA_AMBIENTALISTA_ID, Ambientalista.NOMBRE, Ambientalista._ID
    );

    public static final String SQL_TABLA_REPORTE = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s	INTEGER NOT NULL," +
                    "%s	TEXT," +
                    "%s TEXT," +
                    "%s TEXT NOT NULL," +
                    "%s	INTEGER NOT NULL," +
                    "%s	INTEGER NOT NULL," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s)," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s)," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s));",
            ReporteContaminacion.NOMBRE,
            ReporteContaminacion._ID,
            ReporteContaminacion.COLUMNA_UBICACION,
            ReporteContaminacion.COLUMNA_AMBIENTALISTA_ID,
            ReporteContaminacion.COLUMNA_FOTO,
            ReporteContaminacion.COLUMNA_DESCRIPCION,
            ReporteContaminacion.COLUMNA_FECHA_HORA,
            ReporteContaminacion.COLUMNA_VOLUMEN_RESIDUO_ID,
            ReporteContaminacion.COLUMNA_TIPO_RESIDUO_ID,
            ReporteContaminacion.COLUMNA_AMBIENTALISTA_ID, Ambientalista.NOMBRE, Ambientalista._ID,
            ReporteContaminacion.COLUMNA_VOLUMEN_RESIDUO_ID, VolumenResiduo.NOMBRE, VolumenResiduo._ID,
            ReporteContaminacion.COLUMNA_TIPO_RESIDUO_ID, TipoResiduo.NOMBRE, TipoResiduo._ID
    );

    public static final String SQL_TABLA_VOLUMEN = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "%s TEXT NOT NULL);",
            VolumenResiduo.NOMBRE,
            VolumenResiduo._ID,
            VolumenResiduo.COLUMNA_VOLUMEN
    );

    public static final String SQL_TABLA_TIPO = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "%s TEXT NOT NULL);",
            TipoResiduo.NOMBRE,
            TipoResiduo._ID,
            TipoResiduo.COLUMNA_TIPO
    );

    public static final String SQL_TABLA_PARTICIPA = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "%s	INTEGER NOT NULL," +
                    "%s	INTEGER NOT NULL," +
                    "%s	TEXT NOT NULL," +
                    "%s	TEXT NOT NULL," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s)," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s));",
            ParticipaEvento.NOMBRE,
            ParticipaEvento._ID,
            ParticipaEvento.COLUMNA_AMBIENTALISTA_ID,
            ParticipaEvento.COLUMNA_EVENTO_ID,
            ParticipaEvento.COLUMNA_FECHA_HORA_INICIO,
            ParticipaEvento.COLUMNA_FECHA_HORA_FIN,
            ParticipaEvento.COLUMNA_AMBIENTALISTA_ID, Ambientalista.NOMBRE, Ambientalista._ID,
            ParticipaEvento.COLUMNA_EVENTO_ID, EventoLimpieza.NOMBRE, EventoLimpieza._ID
    );

    public static final String SQL_TABLA_RECOMENDACION_EVENTO = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "%s	INTEGER NOT NULL," +
                    "%s	INTEGER NOT NULL," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s)," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s));",
            RecomendacionEvento.NOMBRE,
            RecomendacionEvento._ID,
            RecomendacionEvento.COLUMNA_AMBIENTALISTA_ID,
            RecomendacionEvento.COLUMNA_EVENTO_ID,
            RecomendacionEvento.COLUMNA_AMBIENTALISTA_ID, Ambientalista.NOMBRE, Ambientalista._ID,
            RecomendacionEvento.COLUMNA_EVENTO_ID, EventoLimpieza.NOMBRE, EventoLimpieza._ID
    );


    public static final String SQL_TABLA_RECOMENDACION_CREAR_EVENTO = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "%s	INTEGER NOT NULL," +
                    "%s	INTEGER NOT NULL," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s)," +
                    "FOREIGN KEY(%s) REFERENCES %s(%s));",
            RecomendacionCrearEvento.NOMBRE,
            RecomendacionCrearEvento._ID,
            RecomendacionCrearEvento.COLUMNA_AMBIENTALISTA_ID,
            RecomendacionCrearEvento.COLUMNA_REPORTE_ID,
            RecomendacionCrearEvento.COLUMNA_AMBIENTALISTA_ID, Ambientalista.NOMBRE, Ambientalista._ID,
            RecomendacionCrearEvento.COLUMNA_REPORTE_ID, ReporteContaminacion.NOMBRE, ReporteContaminacion._ID
    );
}
