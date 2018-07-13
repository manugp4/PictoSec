package com.example.mgutierrezplaza.PictoSec;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Manuel on 08/02/2018.
 */

public class menu extends AppCompatActivity implements View.OnClickListener {

    private TextView tvOpcion;
    private Button btCrear, btIr, btnAñadir, btEliminar;
    private ArrayList<String> datos;
    private Object clase;
    private EditText edtCreacion;
    public static Spinner opciones;
    private ArrayAdapter<String> adaptador;

    public void onCreate(Bundle bundle) {
        getSupportActionBar().hide();
        super.onCreate(bundle);
        setContentView(R.layout.menu);

        opciones = findViewById(R.id.spinOpciones);
        tvOpcion = findViewById(R.id.tvOpcion);
        edtCreacion = findViewById(R.id.edtCreacion);
        btCrear = findViewById(R.id.btCrear);
        btIr = findViewById(R.id.btIr);
        btnAñadir = findViewById(R.id.btnAñadir);
        btEliminar = findViewById(R.id.btEliminar);

        if (!main.modo) {
            edtCreacion.setVisibility(View.INVISIBLE);
            btCrear.setVisibility(View.INVISIBLE);
            btnAñadir.setVisibility(View.INVISIBLE);
            btEliminar.setVisibility(View.INVISIBLE);
            btIr.setText("¡Empezar!");
            ImageView ivUsuario = findViewById(R.id.ivUsuario);
            ivUsuario.setImageResource(R.drawable.user);
            tvOpcion.setVisibility(View.INVISIBLE);
        }

        btCrear.setOnClickListener(this);
        btIr.setOnClickListener(this);
        btnAñadir.setOnClickListener(this);
        btEliminar.setOnClickListener(this);

        datos = new ArrayList<>();
        datos.add("¿Qué quieres hacer?");

        Cursor cursor = main.sqLiteHelper.getData("SELECT secuencia FROM PRUEBA");
        while (cursor.moveToNext()) {
            if (!datos.contains(cursor.getString(0))) {
                datos.add(cursor.getString(0));
            }
        }
        cursor.close();

        adaptador =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, datos) {
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }
                };

        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        opciones.setAdapter(adaptador);

        opciones.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        clase = parent.getItemAtPosition(position);
                        if (((TextView) parent.getChildAt(0)).getText().toString().equals("¿Qué quieres hacer?")) {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        } else {
                            String s = "Seleccionado : " + clase;
                            tvOpcion.setText(s);
                        }
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        tvOpcion.setText("");
                    }
                });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == btCrear.getId()) {
            if (edtCreacion.getText().length() != 0) {
                if (!datos.contains(edtCreacion.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "¡Secuencia creada correctamente!", Toast.LENGTH_SHORT).show();
                    datos.add(edtCreacion.getText().toString());
                    opciones.setSelection(adaptador.getPosition(edtCreacion.getText().toString()));
                    edtCreacion.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "¡Esa secuencia ya está creada!)", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "¡Debe tener mínimo un carácter!", Toast.LENGTH_SHORT).show();
            }
        } else if (id == btIr.getId()) {
            if(opciones.getSelectedItem().toString().equals("¿Qué quieres hacer?")){
                Toast.makeText(menu.this, "¡Elige una secuencia para editar!", Toast.LENGTH_LONG).show();
            }else{
                Intent i = new Intent(this, slider.class);
                i.putExtra("Nombre secuencia", opciones.getSelectedItem().toString());
                startActivity(i);
            }
        } else if (id == btnAñadir.getId()) {
            if(opciones.getSelectedItem().toString().equals("¿Qué quieres hacer?")){
                Toast.makeText(menu.this, "¡Elige una secuencia antes de añadir una pantalla!", Toast.LENGTH_LONG).show();
            }else{
                Intent i = new Intent(this, añadir.class);
                i.putExtra("Nombre secuencia", opciones.getSelectedItem().toString());
                startActivity(i);
            }
        } else if (id == btEliminar.getId()) {
            if (opciones.getSelectedItem().toString().equals("¿Qué quieres hacer?")) {
                Toast.makeText(menu.this, "¡No hay ninguna secuencia seleccionada!", Toast.LENGTH_SHORT).show();
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("¿Seguro que quieres eliminar esta secuencia?");
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        main.sqLiteHelper.deleteSec(opciones.getSelectedItem().toString());
                        adaptador.remove(opciones.getSelectedItem().toString());
                        tvOpcion.setText("");
                        opciones.setAdapter(adaptador);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        }

    }
}
