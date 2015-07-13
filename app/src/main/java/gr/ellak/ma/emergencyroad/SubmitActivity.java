package gr.ellak.ma.emergencyroad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.SubMenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Billys on 17/12/2014.
 */
public class SubmitActivity extends ActionBarActivity {
    ImageView btnImg;
    ImageView viewImage;
    JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    TextView locTxt;
    EditText descTxt;
    BootstrapButton mapBtn;
    Activity act = this;
    double lat = 250;
    double longi = 250;
    String path = "";


    @Override
    public void onCreate(Bundle saved){
        super.onCreate(saved);
        setContentView(R.layout.activity_submit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Submit Incident");
        locTxt=(TextView)findViewById(R.id.loc_etxt);
        descTxt=(EditText)findViewById(R.id.desc_etxt);
        mapBtn = (BootstrapButton) findViewById(R.id.openMapBtn);

        viewImage = (ImageView) findViewById(R.id.imgView);
        btnImg = (ImageView) findViewById(R.id.img_btn);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(act, SubmitMapActivity.class);
                startActivityForResult(i, 3);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectImage(View view) {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    Bitmap thumbnail;
                    thumbnail = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    viewImage.setImageBitmap(thumbnail);
                    viewImage.setVisibility(View.VISIBLE);

                    path = android.os.Environment
                            .getExternalStorageDirectory().getPath() + File.separator + "Emeroad";
                    File folder = new File(path);
                    if(!folder.exists())
                        folder.mkdirs();

                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        path = file.getPath();
                        thumbnail.compress(Bitmap.CompressFormat.JPEG, 20, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String imgpath = c.getString(columnIndex);
                c.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap thumbnail = (BitmapFactory.decodeFile(imgpath, options));
                //Log.w("path of image from gallery......******************.........", picturePath + "");
                viewImage.setImageBitmap(thumbnail);
                viewImage.setVisibility(View.VISIBLE);
                path = android.os.Environment
                        .getExternalStorageDirectory().getPath() + File.separator + "Emeroad";
                File folder = new File(path);
                if(!folder.exists())
                    folder.mkdirs();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {
                    outFile = new FileOutputStream(file);
                    path = file.getPath();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 20, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == 3) {
                if(data.getExtras().getBoolean("is_returned")){
                    lat = data.getExtras().getDouble("lat");
                    longi = data.getExtras().getDouble("long");
                    locTxt.setText(data.getExtras().getString("title"));
                    findViewById(R.id.loc_rel).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void send(View view){
        findViewById(R.id.pb).setVisibility(View.VISIBLE);
        new SendInfo().execute();
    }

    public class SendInfo extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> params1 = new ArrayList<>();
                params1.add(new BasicNameValuePair("location", URLEncoder.encode(locTxt.getText().toString(), "UTF-8")));
                params1.add(new BasicNameValuePair("description",URLEncoder.encode(descTxt.getText().toString(), "UTF-8")));
                params1.add(new BasicNameValuePair("long", String.valueOf(longi)));
                params1.add(new BasicNameValuePair("lat", String.valueOf(lat)));

                JSONObject json = jsonParser.makeHttpRequest(act, "http://titansoft.netau.net/create_incident.php",
                        "POST", params1);

                Log.d("Create Response", json.toString());

                int success = json.getInt(TAG_SUCCESS);

                if(!path.equals("")){
                    try {
                        String url = "http://titansoft.netau.net/upload.php?";
                        HttpClient httpclient = EmeRoadApplication.getInstance().getHttpClient();
                        HttpPost httppost = new HttpPost(url);
                        MultipartEntity entity = new MultipartEntity();

                        FileBody filebodyVideo = new FileBody(new File(path));
                        entity.addPart("fileToUpload", filebodyVideo);
                        entity.addPart("userid", new StringBody(String.valueOf(act.getSharedPreferences("EroadPrefs", 0).getInt("user_id", -1)),"text/plain", Charset.forName("UTF-8")));
                        entity.addPart("filename", new StringBody(String.valueOf(json.getLong("date")), "text/plain", Charset.forName("UTF-8")));

                        httppost.setEntity(entity);
                        HttpResponse resp = httpclient.execute(httppost);
                        HttpEntity resEntity = resp.getEntity();
                        String string= EntityUtils.toString(resEntity);
                        System.out.println(string);
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (success == 1) {

                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(act, "Successfully Submitted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    act.finish();
                } else {
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(act, "Error: Unsuccessfully Submitted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        findViewById(R.id.pb).setVisibility(View.GONE);
                    }
                });
                findViewById(R.id.pb).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
