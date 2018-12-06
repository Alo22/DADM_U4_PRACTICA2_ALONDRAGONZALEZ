package com.example.alondra.dadm_u4_practica2_alondragonzalezcruz;

import android.content.DialogInterface;
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
import android.widget.Spinner;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    EditText ident, domicilio, precioventa, preciorenta, fecha;
    Button guardar, eliminar, actualizar, consultar;
    Spinner ids;
    BaseDatos base;
    String[] idp = new String[100000];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ident = findViewById(R.id.ident);
        domicilio = findViewById(R.id.domicilio);
        precioventa = findViewById(R.id.precioventa);
        preciorenta = findViewById(R.id.preciorenta);
        fecha = findViewById(R.id.fecha);
        ids = findViewById(R.id.idp);
        guardar = findViewById(R.id.guardar);
        eliminar = findViewById(R.id.eliminar);
        actualizar = findViewById(R.id.actualizar);
        consultar = findViewById(R.id.consultar);

        base = new BaseDatos(this, "inmobiliaria", null, 1);
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
                if(actualizar.getText().toString().startsWith("CONFIRMAR CAMBIOS")){
                    confirmacionActualizarDatos();
                    return;
                }
                pedirId(2);
            }
        });
    }
    private void confirmacionActualizarDatos() {
        AlertDialog.Builder confir = new AlertDialog.Builder(this);

        confir.setTitle("IMPORTANTE").setMessage("¿Estas seguro que desea aplicar los cambios?")
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
            String [] id=ids.getSelectedItem().toString().split(": ");
            SQLiteDatabase tabla= base.getWritableDatabase();
            String SQL= "UPDATE INMUEBLE SET DOMICILIO='"+domicilio.getText().toString()+"',PRECIOVENTA="+precioventa.getText().toString()+
                    ", PRECIORENTA="+preciorenta.getText().toString()+
                    ", FECHATRAN='"+fecha.getText().toString()+
                    "', IDP="+id[0]+" WHERE ID=" +ident.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "DATOS ACTUALIZADOS CORRECTAMENTE", Toast.LENGTH_LONG).show();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR, NO SE PUDO ACTUALIZAR", Toast.LENGTH_LONG).show();
        }
        habilitarBotonesYLimpiarCampos();
    }


    private void pedirId(final int origen) {
        final EditText pidoID = new EditText(this);
        String mensaje = "ESCRIBA EL ID A BUSCAR";
        String botonAccion = "BUSCAR";
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("VALOR ENTERO MAYOR DE 0");

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        if (origen == 2) {
            mensaje = "ESCRIBA ID A MODIFICAR";
            botonAccion = "ACTUALIZAR";
        }
        if (origen == 3) {
            mensaje = "ESCRIBA ID QUE DESEA ELIMINAR";
            botonAccion = "ELIMINAR";
        }
        alerta.setTitle("ATENCION").setMessage(mensaje)
                .setView(pidoID)
                .setPositiveButton(botonAccion, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (pidoID.getText().toString().isEmpty()) { //si esta vacio
                            Toast.makeText(Main2Activity.this, "ESCRIBIR IB DEL INMUEBLE", Toast.LENGTH_LONG).show();
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
        try {
            SQLiteDatabase tabla = base.getReadableDatabase();
            String SQL = "SELECT * FROM INMUEBLE WHERE ID=" + idBuscar;

            Cursor resultado = tabla.rawQuery(SQL, null); //cursor navega entre los renglones

            if (resultado.moveToFirst()) {
                if (origen == 3) {
                    //borrar inmueble
                    String datos = idBuscar + "&" + resultado.getString(1) + "&" + resultado.getString(2) + "&" + resultado.getString(3) + "&" + resultado.getString(4) + "&" + resultado.getString(5);
                    confirmacionEliminarDatos(datos);
                    return;
                }
                //consultar datos inmueble
                ident.setText(resultado.getString(0));
                domicilio.setText(resultado.getString(1));
                precioventa.setText(resultado.getString(2));
                preciorenta.setText(resultado.getString(3));
                fecha.setText(resultado.getString(4));
                String[] a;
                int y = 0;
                do {
                    a = idp[y].split(": ");
                    y++;
                } while (!a[0].equals(resultado.getString(5)));
                ids.setSelection(y - 1);
                if (origen == 2) {
                    //modificar se habilitan los botones
                    guardar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("CONFIRMAR CAMBIOS");
                    ident.setEnabled(false);
                }

            } else {
                //no hay resultados
                Toast.makeText(this, "NO SE ECONTRARON RESULTADOS!", Toast.LENGTH_LONG).show();
                habilitarBotonesYLimpiarCampos();
            }
            tabla.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, "NO SE PUDO REALIZAR LA BUSQUEDA", Toast.LENGTH_LONG).show();
        }

    }

    private void habilitarBotonesYLimpiarCampos() {
        ident.setText("");
        domicilio.setText("");
        precioventa.setText("");
        preciorenta.setText("");
        fecha.setText("");
        guardar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        ids.setSelected(false);
        ident.setEnabled(true);
        actualizar.setText("ACTUALIZAR");
    }

    private void confirmacionEliminarDatos(String datos) {
        final String[] cadena = datos.split("&");
        AlertDialog.Builder confir = new AlertDialog.Builder(this);

        confir.setTitle("IMPORTANTE").setMessage("¿DESEAS BORRAR EL INMUEBLE QUE ESTA REGISTRADO?: " + cadena[1])
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
        try {
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "DELETE FROM INMUEBLE WHERE ID=" + idEliminar;
            tabla.execSQL(SQL);
            Toast.makeText(this, "SE ELIMINO CORRECTAMENTE ", Toast.LENGTH_LONG).show();
            habilitarBotonesYLimpiarCampos();
            tabla.close();
        } catch (SQLiteException e) {
            Toast.makeText(this, "ERROR, NO SE PUDO ELIMINAR", Toast.LENGTH_LONG).show();
        }
    }


    private void codigoInsertar() {
        try {
            if (ident.getText().toString().isEmpty()) {
                Toast.makeText(this, "AGREGAR ID DEL INMUEBLE", Toast.LENGTH_LONG).show();
                return;
            }
            if (!repetidoId(ident.getText().toString())) {
                String[] f = fecha.getText().toString().split("/");
                if (!(f[0].length() == 4 && f[1].length() <= 2 && f[2].length() <= 2)) {
                    Toast.makeText(this, "INGRESAR FECHA", Toast.LENGTH_LONG).show();
                    fecha.setText("");
                    return;
                }
                String[] id = ids.getSelectedItem().toString().split(": ");
                SQLiteDatabase tabla = base.getWritableDatabase();
                String SQL = "INSERT INTO INMUEBLE VALUES(" + ident.getText().toString()
                        + ",'" + domicilio.getText().toString() + "'," + precioventa.getText().toString()
                        + "," + preciorenta.getText().toString() + ",'" + fecha.getText().toString() + "'," + id[0] + ")";

                tabla.execSQL(SQL);
                tabla.close();
                Toast.makeText(this, "SE GUARDARON LOS DATOS DEL INMUEBLE CORRECTAMENTE", Toast.LENGTH_LONG).show();
                //habilitarBotonesYLimpiarCampos();
            } else {
                Toast.makeText(this, "INO SE PUDO GUARDAR DATOS PORQUE EL ID YA EXISTE", Toast.LENGTH_LONG).show();
                ident.setText("");
            }
        } catch (SQLiteException e) {
            Toast.makeText(this, "ERROR, NO SE PUDO GUARDAR", Toast.LENGTH_LONG).show();
            //habilitarBotonesYLimpiarCampos();
        }
    }

    private boolean repetidoId(String idBuscar) {
        SQLiteDatabase tabla = base.getReadableDatabase();
        String SQL = "SELECT * FROM INMUEBLE WHERE ID=" + idBuscar;
        Cursor resultado = tabla.rawQuery(SQL, null);
        if (resultado.moveToFirst()) {
            return true;
        }
        return false;
    }


}
