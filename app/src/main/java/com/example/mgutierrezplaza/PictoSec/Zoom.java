package com.example.mgutierrezplaza.PictoSec;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Manuel on 08/07/2018.
 */

public class Zoom extends AppCompatActivity {

    private ImageView ivZoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);

        ivZoom = findViewById(R.id.ivZoom);
        Bundle extras = getIntent().getExtras();
        String img="";
        if(extras != null){
             img = extras.getString("Ruta imagen");
        }
        showImg(img);
        ivZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void showImg(String img){
        File imgFile = new File(img);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ivZoom.setImageBitmap(myBitmap);
        }
    }
}
