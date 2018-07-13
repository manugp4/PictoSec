package com.example.mgutierrezplaza.PictoSec;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Manuel on 05/06/2018.
 */

public class Picto extends AppCompatActivity {

    private String name;
    public static ArrayList<String> urls;
    public static Bitmap imgApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picto);

        urls = new ArrayList<>();

        imgApi = null;

        Toast.makeText(this, "¡Elige uno de los siguientes pictogramas!", Toast.LENGTH_LONG).show();

        Bundle bActivity = getIntent().getExtras();
        if (bActivity != null) {
            name = bActivity.getString("Nombre");
        }

        new RetrieveFeedTask().execute();

    }

        class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

            protected void onPreExecute() {
            }

            protected String doInBackground(Void... urls) {
                try {
                    URL url = new URL("Http://arasaac.perentec.com/API/V1/pictograms");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            Log.i("linea", line);
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        return stringBuilder.toString();
                    }
                    finally{
                        urlConnection.disconnect();
                    }
                }
                catch(Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    return null;
                }
            }

            protected void onPostExecute(String response) {
                if(response == null) {
                    response = "THERE WAS AN ERROR";
                }

                try {

                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray object1 = object.getJSONArray("Data");

                    for(int i = 0; i < object1.length() ; i++){
                        JSONObject object2 = object1.getJSONObject(i);
                        final String cat = object2.getString("category_name");
                        if(name.equals(cat)){
                            String img = object2.getString("pictogram_img");
                            urls.add(img);
                        }
                    }

                    ImageAdapter adapt = new ImageAdapter(getApplicationContext());

                    final GridView gridview = findViewById(R.id.gridview);
                    gridview.setAdapter(adapt);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return Picto.urls.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(final int position, final View convertView, ViewGroup parent) {
            final ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(140, 140));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(3, 3, 3, 3);
            } else {
                imageView = (ImageView) convertView;
            }

            new DownloadImageTask(imageView).execute("Http://arasaac.perentec.com/resources/"+Picto.urls.get(position));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                imgApi = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
                }
            });

            return imageView;
        }

        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;

            public DownloadImageTask(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;
            }

            protected void onPostExecute(Bitmap result) {
                bmImage.setImageBitmap(result);
            }
        }

    }

}
