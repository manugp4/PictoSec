package com.example.mgutierrezplaza.PictoSec;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Manuel on 26/03/2018.
 */

public class slider2 extends PagerAdapter {

    private Activity a;
    private boolean playing = true;
    private MediaPlayer mPlayer;
    private String mFile;
    private Button btnPlay, btnRecord;
    static final int CAM_REQ = 11;
    static final int RE_REQ = 21;
    static final int ARA_REQ = 51;
    static int pos;
    private File AudioDir;
    private String fecha;
    private SimpleDateFormat timeStampFormat;
    private boolean mStartRecording = true;
    private MediaRecorder mRecorder = null;

    public slider2(Activity c) {
        a = c;
    }

    @Override
    public int getCount() {
        return slider.contador;
    }

    private void guardarEnBBDD(){
        final ProgressDialog pd;
        pd = ProgressDialog.show(a, "Cargando",
                "Espere mientras se guarda la información", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i < slider.contador;i++){
                    main.sqLiteHelper.insertData(
                            slider.sec, slider.text[i],
                            slider.ivSliderPath[i], slider.iv1Path[i], slider.iv2Path[i], slider.iv3Path[i], slider.audio[i]
                    );
                }
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                });
            }
        }).start();
        Toast.makeText(a, "¡Editado correctamente!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater layoutInflater= (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = layoutInflater.inflate(R.layout.slider2,container,false);
        ImageView ivSlider = itemView.findViewById(R.id.ivSlider);
        ImageView iv1 = itemView.findViewById(R.id.iv1);
        ImageView iv2 = itemView.findViewById(R.id.iv2);
        ImageView iv3 = itemView.findViewById(R.id.iv3);
        final EditText tvName = itemView.findViewById(R.id.tvName);
        btnPlay = itemView.findViewById(R.id.btnPlay);
        btnRecord = itemView.findViewById(R.id.btnRecord);
        Button btnSave = itemView.findViewById(R.id.btnSave);
        Button btnEliminar = itemView.findViewById(R.id.btnEliminar);

        AudioDir = a.getFilesDir();
        mFile= "recording.3gp";

        if(!main.modo){
            btnSave.setVisibility(View.INVISIBLE);
            btnEliminar.setVisibility(View.INVISIBLE);
            btnRecord.setVisibility(View.INVISIBLE);
            tvName.setClickable(false);
            tvName.setFocusable(false);
            tvName.setCursorVisible(false);
            tvName.setFocusableInTouchMode(false);
            if(slider.ivSliderPath[position].equals("")) ivSlider.setVisibility(View.INVISIBLE);
            else{
                ivSlider.setVisibility(View.VISIBLE);
                showImage2(position,slider.ivSliderPath,ivSlider);
            }
            if(slider.iv1Path[position].equals("")) iv1.setVisibility(View.INVISIBLE);
            else{
                iv1.setVisibility(View.VISIBLE);
                showImage2(position,slider.iv1Path,iv1);
            }
            if(slider.iv2Path[position].equals("")) iv2.setVisibility(View.INVISIBLE);
            else{
                iv2.setVisibility(View.VISIBLE);
                showImage2(position,slider.iv2Path,iv2);
            }
            if(slider.iv3Path[position].equals("")) iv3.setVisibility(View.INVISIBLE);
            else{
                iv3.setVisibility(View.VISIBLE);
                showImage2(position,slider.iv3Path,iv3);
            }
        }else{
            showImage2(position,slider.ivSliderPath,ivSlider);
            showImage2(position,slider.iv1Path,iv1);
            showImage2(position,slider.iv2Path,iv2);
            showImage2(position,slider.iv3Path,iv3);
        }

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(a);
                builder.setTitle("¿Seguro que quieres eliminar esta pantalla?");
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        main.sqLiteHelper.deleteData(slider.sec, slider.text[position], slider.ivSliderPath[position], slider.iv1Path[position],
                                slider.iv2Path[position], slider.iv3Path[position], slider.audio[position]);

                        a.onBackPressed();
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
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.sqLiteHelper.queryData("DELETE FROM PRUEBA WHERE secuencia='"+slider.sec+"'");
                slider.text[position] = tvName.getText().toString().trim();
                guardarEnBBDD();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFile = slider.audio[position];
                onPlay(playing);
                if(playing) btnPlay.setBackgroundResource(R.drawable.stopbtn);
                else btnPlay.setBackgroundResource(R.drawable.playbtn);
                playing = !playing;
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                if(mStartRecording) btnRecord.setBackgroundResource(R.drawable.recon);
                else btnRecord.setBackgroundResource(R.drawable.recoff);
                mStartRecording = !mStartRecording;
                slider.audio[position] = mFile;
            }
        });

        ivSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!main.modo){
                    Intent i = new Intent(a, Zoom.class);
                    i.putExtra("Ruta imagen", slider.ivSliderPath[position]);
                    a.startActivity(i);
                } else{
                    pos = position;
                    slider.selecIV = 0;
                    showDialog(a);
                }
            }
        });
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!main.modo){
                    Intent i = new Intent(a, Zoom.class);
                    i.putExtra("Ruta imagen", slider.iv1Path[position]);
                    a.startActivity(i);
                } else{
                    pos = position;
                    slider.selecIV = 1;
                    showDialog(a);
                }
            }
        });
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!main.modo){
                    Intent i = new Intent(a, Zoom.class);
                    i.putExtra("Ruta imagen", slider.iv2Path[position]);
                    a.startActivity(i);
                } else{
                    pos = position;
                    slider.selecIV = 2;
                    showDialog(a);
                }
            }
        });
        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!main.modo){
                    Intent i = new Intent(a, Zoom.class);
                    i.putExtra("Ruta imagen", slider.iv3Path[position]);
                    a.startActivity(i);
                } else{
                    pos = position;
                    slider.selecIV = 3;
                    showDialog(a);
                }
            }
        });

        tvName.setText(slider.text[position]);

        container.addView(itemView);
        return itemView;
    }

    public static void showImage2(int position, String[] path, ImageView iv){
        File imgFile = new File(path[position]);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            iv.setImageBitmap(myBitmap);
        }
    }
    private void onPlay(boolean start){
        if (start) startPlaying();
        else stopPlaying();
    }

    private void startPlaying(){
        mPlayer = new MediaPlayer();
        try{
            mPlayer.setDataSource(mFile);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e){
            Log.e("play", "prepare() failed");
        }
    }

    private void stopPlaying(){
        mPlayer.release();
        mPlayer = null;
    }

    private void onRecord(boolean start){
        if(start) startRecording();
        else stopRecording();
    }

    private void startRecording(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        timeStampFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
        fecha = timeStampFormat.format(new Date());
        mRecorder.setOutputFile(AudioDir+"/"+fecha+mFile);
        //Toast.makeText(getApplicationContext(), AudioDir+"/"+fecha+mFileName, Toast.LENGTH_SHORT).show();
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try{
            mRecorder.prepare();
        } catch(IOException e){
            Log.e("record", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording(){
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
    }

    public void showDialog(final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Elija una de las opciones");

        builder.setPositiveButton("Galeria", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                activity.startActivityForResult(intent, RE_REQ);
            }
        });
        builder.setNegativeButton("Camara", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                activity.startActivityForResult(cameraIntent, CAM_REQ);
            }
        });
        builder.setNeutralButton("Web ARASAAC", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(activity.getApplicationContext(), PopUp.class);
                activity.startActivityForResult(i, ARA_REQ);
            }
        });
        builder.show();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==object);
    }
}

