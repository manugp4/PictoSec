package com.example.mgutierrezplaza.PictoSec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Manuel on 26/03/2018.
 */

public class slider extends AppCompatActivity {

    public static String [] ivSliderPath;
    public static String [] iv1Path;
    public static String [] iv2Path;
    public static String [] iv3Path;
    public static String [] text;
    public static String [] audio;
    public static String [] secuencia;
    public static int contador;
    private String mCPPath, mCPPath1, mCPPath2, mCPPath3;
    public static int selecIV;
    public static String sec;
    ViewPager viewPager;
    slider2 customSwip;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider);

        ivSliderPath = new String[500];
        iv1Path = new String[500];
        iv2Path = new String[500];
        iv3Path = new String[500];
        text = new String[500];
        audio = new String[500];
        secuencia = new String[500];

        sec = "";
        contador=0;

        viewPager = findViewById(R.id.viewPager);
        customSwip = new slider2(this);

        retrieveBBDD();

        if(slider.contador == 0){
            Toast.makeText(this, "No hay ninguna pantalla añadida en esta secuencia, ¡añade una!",Toast.LENGTH_LONG).show();
        }

        viewPager.setAdapter(customSwip);
    }

    @Override
    public void onBackPressed() {
        if(!main.modo){
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(slider.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog, null);
            final EditText edtResul = mView.findViewById(R.id.edtResul);
            edtResul.setInputType(InputType.TYPE_CLASS_NUMBER);
            Button btnSalir = mView.findViewById(R.id.btnSalir);
            Button btnCancelar = mView.findViewById(R.id.btnEntrar);
            TextView tv1 = mView.findViewById(R.id.tv1);
            tv1.setText("Para salir de este modo, debes resolver la operación siguiente :");
            btnCancelar.setText("Cancelar");

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();

            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            btnSalir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(edtResul.getText().toString().equals("14")){
                        finish();
                    } else{
                        Toast.makeText(slider.this, "¡Resultado incorrecto, inténtalo de nuevo!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            super.onBackPressed();
        }
    }

    private void retrieveBBDD(){
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            sec = extras.getString("Nombre secuencia");
        }

        Cursor cursor = main.sqLiteHelper.getData("SELECT secuencia,name,path,path1,path2,path3,audio FROM PRUEBA");

        while (cursor.moveToNext()){
            if(sec.equals(cursor.getString(0))){
                text[contador] = cursor.getString(1);
                ivSliderPath[contador] = cursor.getString(2);
                iv1Path[contador] = cursor.getString(3);
                iv2Path[contador] = cursor.getString(4);
                iv3Path[contador] = cursor.getString(5);
                audio[contador] = cursor.getString(6);
                contador++;
            }
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        String aux;
        Uri tempUri;
        File finalFile;
        Bitmap photo;
        if (resCode == Activity.RESULT_OK){
            switch (reqCode){
                case slider2.CAM_REQ: photo = (Bitmap) data.getExtras().get("data");
                    tempUri = getImageUri(getApplicationContext(), photo);
                    finalFile = new File(getRealPathFromURI(tempUri));
                    aux = finalFile.getAbsolutePath();
                    switch (selecIV){
                        case 0: mCPPath = aux;
                            ivSliderPath[slider2.pos] = mCPPath;
                            //slider2.ivSlider.setImageBitmap(photo);
                            break;
                        case 1: mCPPath1 = aux;
                            iv1Path[slider2.pos] = mCPPath1;
                            //slider2.iv1.setImageBitmap(photo);
                            break;
                        case 2: mCPPath2 = aux;
                            iv2Path[slider2.pos] = mCPPath2;
                            //slider2.iv2.setImageBitmap(photo);
                            break;
                        case 3: mCPPath3 = aux;
                            iv3Path[slider2.pos] = mCPPath3;
                            //slider2.iv3.setImageBitmap(photo);
                            break;
                        default: break;
                    }
                    viewPager.setAdapter(customSwip);
                    galleryAddPic();
                    //showImage();
                    break;

                case slider2.RE_REQ:
                    if (Build.VERSION.SDK_INT < 19) {
                        aux = añadir.RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
                        switch (selecIV){
                            case 0: mCPPath = aux;
                                ivSliderPath[slider2.pos] = mCPPath;
                                break;
                            case 1: mCPPath1 = aux;
                                iv1Path[slider2.pos] = mCPPath1;
                                break;
                            case 2: mCPPath2 = aux;
                                iv2Path[slider2.pos] = mCPPath2;
                                break;
                            case 3: mCPPath3 = aux;
                                iv3Path[slider2.pos] = mCPPath3;
                                break;
                            default: break;
                        }
                    }
                    // SDK > 19 (Android 4.4)
                    else {
                        aux = añadir.RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                        switch (selecIV){
                            case 0: mCPPath = aux;
                                ivSliderPath[slider2.pos] = mCPPath;
                                break;
                            case 1: mCPPath1 = aux;
                                iv1Path[slider2.pos] = mCPPath1;
                                break;
                            case 2: mCPPath2 = aux;
                                iv2Path[slider2.pos] = mCPPath2;
                                break;
                            case 3: mCPPath3 = aux;
                                iv3Path[slider2.pos] = mCPPath3;
                                break;
                            default: break;
                        }
                    }
                    viewPager.setAdapter(customSwip);
                    //showImage();
                    break;
            }
        }else if (resCode == Activity.RESULT_CANCELED){
            if(reqCode == slider2.ARA_REQ) {
                tempUri = getImageUri(getApplicationContext(), Picto.imgApi);
                finalFile = new File(getRealPathFromURI(tempUri));
                aux = finalFile.getAbsolutePath();
                switch (selecIV) {
                    case 0:
                        mCPPath = aux;
                        ivSliderPath[slider2.pos] = mCPPath;
                        break;
                    case 1:
                        mCPPath1 = aux;
                        iv1Path[slider2.pos] = mCPPath1;
                        break;
                    case 2:
                        mCPPath2 = aux;
                        iv2Path[slider2.pos] = mCPPath2;
                        break;
                    case 3:
                        mCPPath3 = aux;
                        iv3Path[slider2.pos] = mCPPath3;
                        break;
                    default:
                        break;
                }
                galleryAddPic();
                //showImage();
                viewPager.setAdapter(customSwip);
                Picto.imgApi = null;
            }
        }

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f;
        switch (selecIV){
            case 0: f = new File(mCPPath);
                break;
            case 1: f = new File(mCPPath1);
                break;
            case 2: f = new File(mCPPath2);
                break;
            case 3: f = new File(mCPPath3);
                break;
            default: f = new File(mCPPath);
                break;
        }
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String ret = cursor.getString(idx);
        cursor.close();
        return ret;
    }

}
