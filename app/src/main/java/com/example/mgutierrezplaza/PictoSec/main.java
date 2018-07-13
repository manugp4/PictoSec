package com.example.mgutierrezplaza.PictoSec;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main extends AppCompatActivity implements View.OnClickListener{

    private Button btnUsuario;
    public static boolean modo;
    public static SQLiteHelper sqLiteHelper;

    public void onCreate(Bundle bundle){
        getSupportActionBar().hide();
        super.onCreate(bundle);
        setContentView(R.layout.main);

        Button btnAdministrador = findViewById(R.id.btnAdmin);

        sqLiteHelper = new SQLiteHelper(this, "Prueba.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS PRUEBA (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "secuencia VARCHAR, name VARCHAR, path VARCHAR, path1 VARCHAR, path2 VARCHAR, path3 VARCHAR, audio VARCHAR)");

        btnUsuario = findViewById(R.id.btnUsuario);

        btnAdministrador.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Intent i = new Intent(main.this, menu.class);
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(main.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog, null);
                final EditText edtResul = mView.findViewById(R.id.edtResul);
                edtResul.setInputType(InputType.TYPE_CLASS_NUMBER);
                Button btnSalir = mView.findViewById(R.id.btnSalir);
                Button btnEntrar = mView.findViewById(R.id.btnEntrar);
                TextView tv1 = mView.findViewById(R.id.tv1);
                tv1.setText("¡Bienvenido al modo administrador! Por favor, resuelve la siguiente operación para continuar:");
                btnEntrar.setText("Entrar");

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btnEntrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(edtResul.getText().toString().equals("14")){
                            modo = true;
                            edtResul.setText("");
                            startActivity(i);
                        } else{
                            Toast.makeText(main.this, "¡Resultado incorrecto, inténtalo de nuevo!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                btnSalir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                return false;
            }
        });
        btnUsuario.setOnClickListener(this);

        checkAndRequestPermissions();


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        final Intent i = new Intent(this, menu.class);
        if(id == btnUsuario.getId()){
            modo = false;
            startActivity(i);
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (internetPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (audioPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                        //Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        //Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                            showDialogOK("Estos permisos son necesarios para la aplicación",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Ve a ajustes y habilita los permisos, ¡gracias!", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new android.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Aceptar", okListener)
                .setNegativeButton("Cancelar", okListener)
                .create()
                .show();
    }

}
