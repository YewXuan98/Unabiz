package com.example.unabiz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class Mapping extends AppCompatActivity {
    ImageView PreviewImageMap;
    String IMAGE_KEY = "image";
    EditText x_entry;
    EditText y_entry;
    Button map_to_database;
    WifiManager wifiManager;
    List<ScanResult> mywifilist2;
    MainActivity.WifiReceiver wifiReceiver;
    private StringBuilder sbs = new StringBuilder();
    private static final int PICK_IMAGE_REQUEST = 1;
    public Uri mImageUri;
    int count = 0;
    public String imgURL;



    //mapping grids
    int scrWidth, scrHeight;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    final StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapping_mode_2);

        PreviewImageMap = findViewById(R.id.PreviewImageMap);
        x_entry = findViewById(R.id.x_Entry);
        y_entry = findViewById(R.id.y_Entry);
        map_to_database = findViewById(R.id.map_to_database_button);

        map_to_database.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                    String x_coor = x_entry.getText().toString();
                    String y_coord = y_entry.getText().toString();
                    if(x_coor.isEmpty()|| y_coord.isEmpty()){
                            Toast.makeText(Mapping.this, "Please enter both coordinates", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.i("x_coor", x_coor);
                        Log.i("y_coor", y_coord);
                        doStartScanWifi();
                        count +=1;
                    }
            }
        });

        /*LOAD IMAGE INTO MAPPING MODE */

            Intent intent = getIntent();
            imgURL = intent.getStringExtra(IMAGE_KEY);
            Log.i("URL STRING gotten", imgURL);
            Mapping.LoadImage loadImage = new Mapping.LoadImage(PreviewImageMap);
            loadImage.execute(imgURL);
            System.out.println(imgURL);
            //URL image_url = new URL(imgURL);

            //Bitmap bit_image = BitmapFactory.decodeStream(image_url.openConnection().getInputStream());
            //PreviewImageMap.setImageBitmap(bit_image);

//            Bitmap tempBitmap = Bitmap.createBitmap(bit_image.getWidth(),bit_image.getHeight(), Bitmap.Config.RGB_565);
//            Canvas tempcanvas = new Canvas(tempBitmap);
//
//            Paint myPaint = new Paint();
//            myPaint.setColor(Color.RED);
//            myPaint.setStrokeWidth(30);
//            myPaint.setStyle(Paint.Style.STROKE);
//
//            //Draw the image bitmap into canvas
//            tempcanvas.drawBitmap(bit_image,0,0,null);
//            tempcanvas.drawCircle(10,10,5, myPaint);

            //Create new image bitmap and attach a brand new canvas
            //Log.i("URL gotten", image_url.toString());


//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }
    class LoadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        Bitmap tempBitmap;
        public LoadImage(ImageView PreviewImage) {
            this.imageView = PreviewImageMap;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String URLlink = strings[0];
            Bitmap bitmap = null;


            try {
                InputStream inputStream = new java.net.URL(URLlink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);

                tempBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempcanvas = new Canvas(tempBitmap);

                Paint myPaint = new Paint();
                myPaint.setColor(Color.RED);
                myPaint.setStrokeWidth(30);
                myPaint.setStyle(Paint.Style.STROKE);
                int x1 = 50;
                int y1 = 50;
                int x2 = 50;
                int y2 = 50;

                //Draw the image bitmap into canvas
                tempcanvas.drawBitmap(bitmap,0,0,null);
                tempcanvas.drawCircle(50,50,50, myPaint);
                tempcanvas.drawRoundRect(new RectF(x1,y1,x2,y2), 2, 2, myPaint);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //Attach the canvas to the Image view
            PreviewImageMap.setImageBitmap(tempBitmap);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();


        } else {
            Log.i("Failed activity", "Failed activity");
        }
    }

    private void doStartScanWifi()  {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        final WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mywifilist2 = wifiManager.getScanResults();
        //System.out.println(mywifilist2.size());
        String AP_name = "AP" + count;
        String x_coor = x_entry.getText().toString();
        String y_coord = y_entry.getText().toString();
        for (int i=0; i < mywifilist2.size(); i++) {
            Log.i("AP" , AP_name);
            String bssid = mywifilist2.get(i).BSSID;
            //String ssid = mywifilist2.get(i).SSID.replace('.', '1'); //replace . with 1
            Log.i("AP" , AP_name);
            Integer rssi = mywifilist2.get(i).level;
            //sbs.append(new Integer(i+1).toString() + ".");
            //sbs.append(String.format("Name: %s,\nBSSID: %s,\nRSSI: %s\n",ssid,bssid,rssi));
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(AP_name)){

                        databaseReference.child(AP_name).child("x").setValue(x_coor);
                        databaseReference.child(AP_name).child("y").setValue(y_coord);
                        databaseReference.child(AP_name).child(bssid).setValue(rssi);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        x_entry.setText("");
        y_entry.setText("");

    }





}
