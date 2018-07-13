package com.example.mgutierrezplaza.PictoSec;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Manuel on 18/06/2018.
 */

public class PopUp extends Activity {

    private LinearLayout buttonContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);

        new RetrieveFeedTask().execute();
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;

            getWindow().setLayout((int) (width*.8), (int) (height*.8) );

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.gravity = Gravity.CENTER;
            params.x = 0;
            params.y = -20;

            getWindow().setAttributes(params);

            buttonContainer = findViewById(R.id.buttonContainer);
        }

        protected String doInBackground(Void... urls) {
            //String email = emailText.getText().toString();
            // Do some validation here

            try {
                URL url = new URL("http://arasaac.perentec.com/API/V1/categories");
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
                    final String name = object2.getString("category_name");
                    Button bt = new Button(PopUp.this);
                    bt.setText(name);
                    bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(PopUp.this, Picto.class);
                            i.putExtra("Nombre", name);
                            startActivity(i);
                        }
                    });
                    buttonContainer.addView(bt);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
