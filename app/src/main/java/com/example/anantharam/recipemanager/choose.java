package com.example.anantharam.recipemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;
import static com.example.anantharam.recipemanager.Config.img;

public class choose extends AppCompatActivity {
    String ingredients[];
    String ings;
    Button bt7;
    EditText et4;
    boolean flag = false;
    String data[];
    String data1 = "";
    String data2 = "";
    GridView gv;
    Dialog dialog;
    int allasynctasks = 0;
    int no_of_images = 0;
    int k=0;
    public static String recipe1[];
    String images = "";
    String images1[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        bt7 = findViewById(R.id.bt7);
        et4 = findViewById(R.id.et4);
        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ings = et4.getText().toString();
                ingredients = ings.split(",");

                if (Config.isNetworkStatusAvailable(getApplicationContext())) {
                    new save().execute();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(choose.this);
                    builder.setMessage("No Internet Connection").setTitle("Information");
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            }

            class save extends AsyncTask<String, Void, String> {


                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog = new Dialog(choose.this);
                    dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();
                }

                protected String doInBackground(String... arg0) {
                    HttpHandler sh = new HttpHandler();

                    int l = ingredients.length;
                    String ings_final = "";
                    for (int k = 0; k < l; k++) {
                        ings_final += "allowedIngredient[]=" + ingredients[k].trim() + "&";
                    }

                    ings_final = ings_final.substring(0, ings_final.length() - 1);

                    String url = "http://api.yummly.com/v1/api/recipes?_app_id=3fa1b4f4&_app_key=a64779f76ded00c72f8fe1e13f9cbf03&requirePictures=true&" + ings_final;
                    System.out.println(url);
                    String jsonStr = sh.makeServiceCall(url);


                    Log.e(TAG, "Response from url: " + jsonStr);
                    Log.e(TAG, "URL : " + url);
                    Log.e(TAG, "Got Response from url!");

                    if (!jsonStr.equals("Nothing")) {
                        try {
                            JSONObject x = new JSONObject(jsonStr);
                            JSONArray y = x.getJSONArray("matches");
                            for (int i = 0; i < y.length(); i++) {

                                JSONObject z = y.getJSONObject(i);
                                data1 += z.getString("id") + "\n";

                            }
                            data = data1.split("\n");

                            for (String j : data) {
                                j.trim();
                                String url1 = "http://api.yummly.com/v1/api/recipe/" + j + "?_app_id=3fa1b4f4&_app_key=a64779f76ded00c72f8fe1e13f9cbf03";
                                String jsonStr1 = sh.makeServiceCall(url1);

                                Log.e(TAG, "Response from url: " + jsonStr1);
                                Log.e(TAG, "URL : " + url1);
                                Log.e(TAG, "Got Response from url1!");

                                try {
                                    JSONObject x1 = new JSONObject(jsonStr1);
                                    JSONObject y1 = x1.getJSONObject("source");
                                    JSONArray z1 = x1.getJSONArray("images");


                                    data2 += y1.getString("sourceRecipeUrl") + "\n";
                                    JSONObject image = z1.getJSONObject(0);
                                    images += image.getString("hostedLargeUrl") + "\n";

                                } catch (final Exception e) {
                                    //    Log.e(TAG, "Json parsing error: " + e.getMessage());
                                    flag = true;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }

                            images1 = images.split("\n");
                            recipe1 = data2.split("\n");

                            no_of_images = data.length;

                        } catch (final JSONException e) {
                            //    Log.e(TAG, "Json parsing error: " + e.getMessage());
                            flag = true;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        //  Log.v(TAG, "Couldn't get json from server.");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Couldn't get json from server.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    dialog.dismiss();
                    //   tv1.setText(data2, TextView.BufferType.EDITABLE);
                    if (flag == false) {
                        if(no_of_images > 0) {
                            img = new Drawable[no_of_images];
                            for(int i = 0; i < no_of_images; i++)
                                new LoadImages(images1[i], "image"+i+".jpg").execute();
                        }
                    }
                }
            }
        });
    }

    private void goToUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }


    private class LoadImages extends AsyncTask<String, Void, Drawable> {

        private String imageUrl , imageName;

        public LoadImages(String url, String file_name) {
            this.imageUrl = url;
            this.imageName = file_name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Drawable doInBackground(String... urls) {

            try {
                InputStream is = (InputStream) this.fetch(this.imageUrl);
                Drawable d = Drawable.createFromStream(is, this.imageName);
                return d;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        private Object fetch(String address) throws MalformedURLException,IOException {
            URL url = new URL(address);
            Object content = url.getContent();
            return content;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            img[k] = result;
            k++;
            allasynctasks++;
            Log.e(TAG, "allasynctasks : " + allasynctasks);
            if(allasynctasks == no_of_images) {
                gotonextActivity();
            }
        }
    }
    public void gotonextActivity() {
        gv = findViewById(R.id.gv);
        gv.setAdapter(new ImageAdapter(this));

        dialog.dismiss();


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Toast.makeText(MainActivity.this, "" + position,Toast.LENGTH_SHORT).show();
                goToUrl(recipe1[position]);

            }
        });
    }
}