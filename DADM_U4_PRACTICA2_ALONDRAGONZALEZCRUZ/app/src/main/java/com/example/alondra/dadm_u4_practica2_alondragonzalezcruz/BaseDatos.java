package com.example.alondra.dadm_u4_practica2_alondragonzalezcruz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatos   extends SQLiteOpenHelper {
    //context recibe el this(activity que ejecutara esta clase, String nombre de la bd,curso que traia el contenido, version de la BD)
    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE PROPIETARIO(IDP INTEGER PRIMARY KEY NOT NULL,NOMBRE VARCHAR(200),DOMICILIO VARCHAR(500),TELEFONO VARCHAR(50))");
        db.execSQL("CREATE TABLE INMUEBLE(IDM INTEGER PRIMARY KEY NOT NULL,DOMICILIO VARCHAR(200),PRECIOV FLOAT, PRECIORENTA FLOAT, FECHATRAN DATE, IDP INTEGER, FOREIGN KEY (IDP) REFERENCES PROPIETARIO(IDP))");
        }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
