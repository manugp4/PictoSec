package com.example.mgutierrezplaza.PictoSec;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Manuel on 27/06/2018.
 */

public class añadir extends AppCompatActivity {

    private Button btnGuardar, btnRecord, btnPlay;
    private String mCurrentPhotoPath, mCurrentPhotoPath1, mCurrentPhotoPath2, mCurrentPhotoPath3;
    private ImageView ivSlider, iv1, iv2, iv3;
    private EditText edtDescrip;
    private ProgressDialog pd;
    private static final int CAMERA_REQUEST = 10;
    private static final int READ_REQUEST = 20;
    private static final int ARASAAC_REQUEST = 50;
    public static int seleccionIV = 0;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private static String mFileName = null;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    File AudioDir;
    String fecha;
    SimpleDateFormat timeStampFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anadir);

        mCurrentPhotoPath="";
        mCurrentPhotoPath1="";
        mCurrentPhotoPath2="";
        mCurrentPhotoPath3="";

        btnRecord = findViewById(R.id.btnRecord);
        btnPlay = findViewById(R.id.btnPlay);
        btnGuardar = findViewById(R.id.btnGuardar);
        ivSlider = findViewById(R.id.ivSlider);
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        iv3 = findViewById(R.id.iv3);
        edtDescrip = findViewById(R.id.edtDescrip);

        AudioDir = getFilesDir();
        mFileName= "recording.3gp";

        edtDescrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtDescrip.length() > 0){
                    TextKeyListener.clear(edtDescrip.getText());
                }
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarEnBBDD();
            }
        });
        ivSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionIV = 0;
                showDialog(añadir.this, "Elija una de las opciones:");
            }
        });
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionIV = 1;
                showDialog(añadir.this, "Elija una de las opciones:");
            }
        });
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionIV = 2;
                showDialog(añadir.this, "Elija una de las opciones:");
            }
        });
        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionIV = 3;
                showDialog(añadir.this, "Elija una de las opciones:");
            }
        });
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                if(mStartRecording) btnRecord.setBackgroundResource(R.drawable.recon);
                else btnRecord.setBackgroundResource(R.drawable.recoff);
                mStartRecording = !mStartRecording;
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if(mStartPlaying) btnPlay.setBackgroundResource(R.drawable.stopbtn);
                else btnPlay.setBackgroundResource(R.drawable.playbtn);
                mStartPlaying = !mStartPlaying;
            }
        });

    }

    private void guardarEnBBDD(){
        if(mCurrentPhotoPath.equals("") || mCurrentPhotoPath1.equals("") || mCurrentPhotoPath2.equals("") ||
                mCurrentPhotoPath3.equals("") || edtDescrip.getText().toString().isEmpty()){
            AlertDialog.Builder b = new AlertDialog.Builder(añadir.this);
            b.setTitle("¿Seguro que quieres guardar la pantalla sin completar?");
            b.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pd = ProgressDialog.show(añadir.this, "Cargando",
                            "Espere mientras se guarda la información", true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            guardado();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();
                                }
                            });
                        }
                    }).start();
                    edtDescrip.setText("");
                    ivSlider.setImageResource(R.drawable.insertarimagen);
                    iv1.setImageResource(R.drawable.insertarimagen);
                    iv2.setImageResource(R.drawable.insertarimagen);
                    iv3.setImageResource(R.drawable.insertarimagen);
                    mCurrentPhotoPath="";
                    mCurrentPhotoPath1="";
                    mCurrentPhotoPath2="";
                    mCurrentPhotoPath3="";
                    mFileName="";
                    Toast.makeText(getApplicationContext(), "¡Guardado!", Toast.LENGTH_SHORT).show();
                }
            });
            b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            b.show();
        }else {
            pd = ProgressDialog.show(añadir.this, "Cargando",
                    "Espere mientras se guarda la información", true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    guardado();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                        }
                    });
                }
            }).start();
            edtDescrip.setText("");
            ivSlider.setImageResource(R.drawable.insertarimagen);
            iv1.setImageResource(R.drawable.insertarimagen);
            iv2.setImageResource(R.drawable.insertarimagen);
            iv3.setImageResource(R.drawable.insertarimagen);
            mCurrentPhotoPath="";
            mCurrentPhotoPath1="";
            mCurrentPhotoPath2="";
            mCurrentPhotoPath3="";
            mFileName="";
            Toast.makeText(getApplicationContext(), "¡Guardado!", Toast.LENGTH_SHORT).show();
        }

    }

    private void guardado(){
        String sec = "";
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            sec = extras.getString("Nombre secuencia");
        }
        main.sqLiteHelper.insertData(
                sec, edtDescrip.getText().toString().trim(),
                mCurrentPhotoPath, mCurrentPhotoPath1, mCurrentPhotoPath2, mCurrentPhotoPath3, AudioDir+"/"+fecha+mFileName
        );
    }

    public void showDialog(Activity activity, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if(title != null) builder.setTitle(title);

        builder.setPositiveButton("Galeria", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, READ_REQUEST);
            }
        });
        builder.setNegativeButton("Camara", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        builder.setNeutralButton("Web ARASAAC", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getApplicationContext(), PopUp.class);
                startActivityForResult(i, ARASAAC_REQUEST);
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        String aux;
        Uri tempUri;
        File finalFile;
        Bitmap photo;
        if (resCode == Activity.RESULT_OK) {
            switch (reqCode) {
                case CAMERA_REQUEST:
                    photo = (Bitmap) data.getExtras().get("data");
                    tempUri = getImageUri(getApplicationContext(), photo);
                    finalFile = new File(getRealPathFromURI(tempUri));
                    aux = finalFile.getAbsolutePath();
                    switch (seleccionIV){
                        case 0: mCurrentPhotoPath = aux;
                            ivSlider.setImageBitmap(photo);
                            break;
                        case 1: mCurrentPhotoPath1 = aux;
                            iv1.setImageBitmap(photo);
                            break;
                        case 2: mCurrentPhotoPath2 = aux;
                            iv2.setImageBitmap(photo);
                            break;
                        case 3: mCurrentPhotoPath3 = aux;
                            iv3.setImageBitmap(photo);
                            break;
                        default: break;
                    }
                    //checkPermission(WRITE_REQUEST);
                    galleryAddPic();
                    showImage();
                    break;
                case READ_REQUEST:
                    if (Build.VERSION.SDK_INT < 19) {
                        aux = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
                        switch (seleccionIV){
                            case 0: mCurrentPhotoPath = aux;
                                break;
                            case 1: mCurrentPhotoPath1 = aux;
                                break;
                            case 2: mCurrentPhotoPath2 = aux;
                                break;
                            case 3: mCurrentPhotoPath3 = aux;
                                break;
                            default: break;
                        }
                    }
                    // SDK > 19 (Android 4.4)
                    else {
                        aux = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                        switch (seleccionIV){
                            case 0: mCurrentPhotoPath = aux;
                                break;
                            case 1: mCurrentPhotoPath1 = aux;
                                break;
                            case 2: mCurrentPhotoPath2 = aux;
                                break;
                            case 3: mCurrentPhotoPath3 = aux;
                                break;
                            default: break;
                        }
                    }
                    showImage();
                    break;
            }
        }
        else if (resCode == Activity.RESULT_CANCELED){
            if(reqCode == ARASAAC_REQUEST && Picto.imgApi != null) {
                tempUri = getImageUri(getApplicationContext(), Picto.imgApi);
                finalFile = new File(getRealPathFromURI(tempUri));
                aux = finalFile.getAbsolutePath();
                switch (seleccionIV) {
                    case 0:
                        mCurrentPhotoPath = aux;
                        break;
                    case 1:
                        mCurrentPhotoPath1 = aux;
                        break;
                    case 2:
                        mCurrentPhotoPath2 = aux;
                        break;
                    case 3:
                        mCurrentPhotoPath3 = aux;
                        break;
                    default:
                        break;
                }
                galleryAddPic();
                showImage();
                Picto.imgApi = null;
            }
        }
    }

    private void showImage(){
        File imgFile;
        Bitmap myBitmap;
            switch (seleccionIV){
                case 0: imgFile = new File(mCurrentPhotoPath);
                    if (imgFile.exists()) {
                        myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ivSlider.setImageBitmap(myBitmap);
                    }
                    break;
                case 1: imgFile = new File(mCurrentPhotoPath1);
                    if (imgFile.exists()) {
                        myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        iv1.setImageBitmap(myBitmap);
                    }
                    break;
                case 2: imgFile = new File(mCurrentPhotoPath2);
                    if (imgFile.exists()) {
                        myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        iv2.setImageBitmap(myBitmap);
                    }
                    break;
                case 3: imgFile = new File(mCurrentPhotoPath3);
                    if (imgFile.exists()) {
                        myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        iv3.setImageBitmap(myBitmap);
                    }
                    break;
                default: break;
            }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f;
        switch (seleccionIV){
            case 0: f = new File(mCurrentPhotoPath);
                break;
            case 1: f = new File(mCurrentPhotoPath1);
                break;
            case 2: f = new File(mCurrentPhotoPath2);
                break;
            case 3: f = new File(mCurrentPhotoPath3);
                break;
            default: f = new File(mCurrentPhotoPath);
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

    public static class RealPathUtil {

        @SuppressLint("NewApi")
        public static String getRealPathFromURI_API19(Context context, Uri uri){
            String filePath = "";
            String wholeID = DocumentsContract.getDocumentId(uri);

            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Images.Media.DATA };

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{ id }, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        }


        @SuppressLint("NewApi")
        public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
            String[] proj = { MediaStore.Images.Media.DATA };
            String result = null;

            CursorLoader cursorLoader = new CursorLoader(
                    context,
                    contentUri, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();

            if(cursor != null){
                int column_index =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
            }
            return result;
        }

    }

    private void onRecord(boolean start){
        if(start) startRecording();
        else stopRecording();
    }

    private void onPlay(boolean start){
        if (start) startPlaying();
        else stopPlaying();
    }

    private void startPlaying(){
        try{
            mPlayer = new MediaPlayer();

            if (mPlayer == null) {
                return;
            }
            else mPlayer.stop();

            //Toast.makeText(getApplicationContext(), AudioDir+"/"+fecha+mFileName, Toast.LENGTH_SHORT).show();
            mPlayer.setDataSource(AudioDir+"/"+fecha+mFileName);
            //mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.prepare();
            /*mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayer.stop();
                }
            });*/
        } catch (Exception e){
            e.printStackTrace();
            Log.e("play", "prepare() failed");
        }
        mPlayer.start();
    }

    private void stopPlaying(){
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        timeStampFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
        fecha = timeStampFormat.format(new Date());
        mRecorder.setOutputFile(AudioDir+"/"+fecha+mFileName);
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

}
