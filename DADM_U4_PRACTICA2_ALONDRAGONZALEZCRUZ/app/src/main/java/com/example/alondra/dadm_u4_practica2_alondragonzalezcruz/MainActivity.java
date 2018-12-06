package com.example.alondra.dadm_u4_practica2_alondragonzalezcruz;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
Button guardar,eliminar,actualizar,consultar;
EditText identificador, nombre, domicilio, telefono;
BaseDatos base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        identificador=findViewById(R.id.identificador);
        nombre=findViewById(R.id.nombre);
        domicilio=findViewById(R.id.domicilio);
        telefono=findViewById(R.id.telefono);

        guardar=findViewById(R.id.guardar);
        eliminar=findViewById(R.id.eliminar);
        actualizar=findViewById(R.id.actualizar);
        consultar=findViewById(R.id.consultar);
        // Coneccion de la BASE DE DATOS
        base=new BaseDatos(this, "inmobiliaria",null,1);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
      codigoInsertar();
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirId(3);
            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirId(1);
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizar.getText().toString().startsWith("CONFIRMAR CAMBIOS")) {
                    confirmacionActualizarDatos();
                    return;
                }
                pedirId(2);
            }
        });
    }
    private void confirmacionActualizarDatos() {
        AlertDialog.Builder confir = new AlertDialog.Builder(this);

        confir.setTitle("IMPORTANTE").setMessage("¿Estas seguro que quieres realizar  cambios?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actualizarDatos();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }
    private void actualizarDatos() {
        try{
            SQLiteDatabase tabla= base.getWritableDatabase();
            String SQL= "UPDATE PROPIETARIO SET NOMBRE='"+nombre.getText().toString()+"',DOMICILIO='"+domicilio.getText().toString()+"', TELEFONO='"+telefono.getText().toString()+"'WHERE IDP="
                    +identificador.getText().toString();

            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "DATOS ACTUALIZADOS CORRECTAMENTE", Toast.LENGTH_LONG).show();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR, NO SE PUDO ACTUALIZAR", Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }
    private void pedirId(final int origen ) {
        final EditText pidoID= new EditText(this);
        String mensaje=" ID A BUSCAR";
        String botonAccion="BUSCAR";

        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("VALOR ENTERO MAYOR DE 0");


        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        if(origen ==2){
            mensaje="ESCRIBA ID A MODIFICAR";
            botonAccion="ACTUALIZAR";
        }
        if(origen ==3){
            mensaje="ESCRIBA ID QUE DESEA ELIMINAR";
            botonAccion="ELIMINAR";
        }
        alerta.setTitle("ATENCION").setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton(botonAccion, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(pidoID.getText().toString().isEmpty()){
                            Toast.makeText(MainActivity.this, "ESCRIBIR ID", Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pidoID.getText().toString(), origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }
    private void buscarDato(String idBuscar, int origen) {
        try{
            SQLiteDatabase tabla= base.getReadableDatabase();
            String SQL ="SELECT * FROM PROPIETARIO WHERE IDP="+idBuscar;

            Cursor resultado =tabla.rawQuery(SQL, null);
            if(resultado.moveToFirst()){
                if(origen==3){
                    //borrar datos del propietario
                    String datos=idBuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3);
                    confirmacionEliminarDatos(datos);
                    return;
                }
                //Si hay resultado
                identificador.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                domicilio.setText(resultado.getString(2));
                telefono.setText(resultado.getString(3));
                if(origen==2){
                    //siginifica que se consulto para modificar
                   guardar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("CONFIRMAR CAMBIOS");
                    identificador.setEnabled(false);
                }

            }else{
                //no hay resultados
                Toast.makeText(this, "NO SE ENCUENTRAN DATOS CON ESTE ID!", Toast.LENGTH_LONG).show();
            }
            tabla.close();
        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR, NO SE PUEDE BUSCAR", Toast.LENGTH_LONG).show();
        }
    }
    private void confirmacionEliminarDatos(String datos) {
        final String []cadena = datos.split("&");
        AlertDialog.Builder confir = new AlertDialog.Builder(this);

        confir.setTitle("IMPORTANTE").setMessage("¿DESEAS ELIMINAR ESTE ID DE PROPIETARIO?: "+cadena[1])
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarDato(cadena[0]);
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }
    private void eliminarDato(String idEliminar) {
        try{
            SQLiteDatabase tabla= base.getWritableDatabase();

            String SQL ="DELETE FROM PROPIETARIO WHERE IDP="+idEliminar;
            String SQLi ="DELETE FROM INMUEBLE WHERE IDP="+idEliminar;
            tabla.execSQL(SQLi);
            tabla.execSQL(SQL);
            Toast.makeText(this, "SE ELIMINO CORRECTAMENTE", Toast.LENGTH_LONG).show();
            habilitarBotonesYLimpiarCampos();
            tabla.close();
        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR, NO SE PUDO ELIMINAR", Toast.LENGTH_LONG).show();
        }
    }
    private void codigoInsertar() {
        try{
            if(identificador.getText().toString().isEmpty()){
                Toast.makeText(this, "AGREGAR ID DEL PROPIETARIO", Toast.LENGTH_LONG).show();
                return;
            }
            if(!repetidoId(identificador.getText().toString())) {
                SQLiteDatabase tabla = base.getWritableDatabase();
                String SQL = "INSERT INTO PROPIETARIO VALUES(" + identificador.getText().toString()
                        + ",'" + nombre.getText().toString() + "','" + domicilio.getText().toString()
                        + "','" + telefono.getText().toString() + "')";

                tabla.execSQL(SQL);//ejecuta la instruccion SQL IMPORTANTE PONER
                tabla.close(); //cierro la conexion a la bd
                Toast.makeText(this, "SE GUARDO DATOS DE PROPIETARIO CORRECTAMENTE", Toast.LENGTH_LONG).show();
                habilitarBotonesYLimpiarCampos();
            }else {
                Toast.makeText(this, "NO SE PUDO GUARDAR DATOS PORQUE EL ID YA EXISTE", Toast.LENGTH_LONG).show();
                identificador.setText("");
            }
        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR, NO SE PUDO GUARDAR", Toast.LENGTH_LONG).show();
            //habilitarBotonesYLimpiarCampos();
        }
    }
    private boolean repetidoId(String idBuscar){
        SQLiteDatabase tabla= base.getReadableDatabase();
        String SQL ="SELECT * FROM PROPIETARIO WHERE IDP="+idBuscar;
        Cursor resultado =tabla.rawQuery(SQL, null); //cursor permite navegar enre los renglones
        if(resultado.moveToFirst()){
            return true;
        }
        return false;
    }
    private void habilitarBotonesYLimpiarCampos() {
        identificador.setText("");
        nombre.setText("");
        domicilio.setText("");
        telefono.setText("");
        guardar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        identificador.setEnabled(true);
    }

    }
