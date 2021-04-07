package com.example.unabiz;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;

import android.graphics.Paint;
import android.graphics.Path;

import android.net.wifi.ScanResult;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.List;


public class Mapping extends AppCompatActivity {
    ImageView PreviewImageMap;
    String IMAGE_KEY = "image";
    EditText x_entry;
    EditText y_entry;
    Button map_to_database;
    Button map_to_database_2;
    Button gotoTestmode;

    List<ScanResult> mywifilist;
    //private StringBuilder sbs = new StringBuilder();
    private static final int PICK_IMAGE_REQUEST = 1;
    public Uri mImageUri;
    int count_ap = 0;
    int count_dp = 0;
    public String imgURL;

    //zooming

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    //mapping grids
    int scrWidth, scrHeight;

    DatabaseReference databaseReference_una = FirebaseDatabase.getInstance().getReference();


    @SuppressLint("ResourceType")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mapping_mode_2);


        PreviewImageMap = findViewById(R.id.PreviewImageMap);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        x_entry = findViewById(R.id.x_Entry);
        y_entry = findViewById(R.id.y_Entry);
        map_to_database = findViewById(R.id.map_to_database_button);
        map_to_database_2 = findViewById(R.id.map_to_database2_button);
        gotoTestmode = findViewById(R.id.mappingmode_to_testmode);

        final FireBaseUtils.listCallbackInterface list_of_wifi_points = new FireBaseUtils.listCallbackInterface() {
            @Override
            public void onCallback(List<String> wifipoints) {

            }
        };

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
                        count_ap +=1;
                    }

                    FireBaseUtils.retrievekeys(list_of_wifi_points);
            }
        });

        map_to_database_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String x_coor = x_entry.getText().toString();
                String y_coord = y_entry.getText().toString();
                if(x_coor.isEmpty()|| y_coord.isEmpty()){
                    Toast.makeText(Mapping.this, "Please enter both coordinates", Toast.LENGTH_SHORT).show();

                } else {
                    Log.i("x_coor", x_coor);
                    Log.i("y_coor", y_coord);
                    mapToDatabse_DP();
                    count_dp +=1;
                }

            }
        });

        gotoTestmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Testing.class);
                startActivity(intent);
            }
        });

        /*LOAD IMAGE INTO MAPPING MODE */

            Intent intent = getIntent();
            imgURL = intent.getStringExtra(IMAGE_KEY);
            Log.i("URL STRING gotten", imgURL);
            Mapping.LoadImage loadImage = new Mapping.LoadImage(PreviewImageMap);
            loadImage.execute(imgURL);



    }

    private void doStartScanWifi()  {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        final WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mywifilist = wifiManager.getScanResults();
        System.out.println(mywifilist.size());
        String AP_name = "AP" + count_ap;
        String x_coor = x_entry.getText().toString();
        String y_coord = y_entry.getText().toString();
        for (int i=0; i < mywifilist.size(); i++) {
            Log.i("AP" , AP_name);
            String bssid = mywifilist.get(i).BSSID;
            //String ssid = mywifilist.get(i).SSID.replace('.', '1'); //replace . with 1
            Integer rssi = mywifilist.get(i).level;
            //sbs.append(new Integer(i+1).toString() + ".");
            //sbs.append(String.format("Name: %s,\nBSSID: %s,\nRSSI: %s\n",ssid,bssid,rssi));


            databaseReference_una.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {



                    if(!snapshot.child("WIFI").hasChild(bssid)){
                        databaseReference_una.child("WIFI").child(bssid).setValue(rssi);
                    }

                    if (!snapshot.hasChild(AP_name)){
                        Log.i("send coordinates", x_coor + "sent");
                        databaseReference_una.child(AP_name).child("x").setValue(x_coor);
                        databaseReference_una.child(AP_name).child("y").setValue(y_coord);
                        databaseReference_una.child(AP_name).child(bssid).setValue(rssi);

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

    private void mapToDatabse_DP() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        final WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mywifilist = wifiManager.getScanResults();
        //System.out.println(mywifilist.size());
        String DP_name = "DP" + count_dp;
        String x_coor = x_entry.getText().toString();
        String y_coord = y_entry.getText().toString();
        for (int i=0; i < mywifilist.size(); i++) {
            Log.i("DP" , DP_name);
            String bssid = mywifilist.get(i).BSSID;
            //String ssid = mywifilist.get(i).SSID.replace('.', '1'); //replace . with 1
            Integer rssi = mywifilist.get(i).level;

            databaseReference_una.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {



                    if(!snapshot.child("WIFI").hasChild(bssid)){
                        databaseReference_una.child("WIFI").child(bssid).setValue(rssi);
                    }

                    if (!snapshot.hasChild(DP_name)){
                        Log.i("send coordinates", x_coor + "sent");
                        databaseReference_una.child(DP_name).child("x").setValue(x_coor);
                        databaseReference_una.child(DP_name).child("y").setValue(y_coord);
                        databaseReference_una.child(DP_name).child(bssid).setValue(rssi);

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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            Log.i("IV_dimens", String.valueOf(PreviewImageMap.getWidth()));
            Log.i("IV_dimens", String.valueOf(PreviewImageMap.getHeight()));

            PreviewImageMap.setScaleX(mScaleFactor);
            PreviewImageMap.setScaleY(mScaleFactor);
            return true;
        }

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
                scrHeight = bitmap.getHeight();
                scrWidth = bitmap.getWidth();

                tempBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempcanvas = new Canvas(tempBitmap);

                Paint myPaint = new Paint();
                myPaint.setColor(0xffcccccc);
                myPaint.setStrokeWidth(10);
                myPaint.setStyle(Paint.Style.STROKE);


                //Draw the image bitmap into canvas
                tempcanvas.drawBitmap(bitmap,0,0,null);
                //tempcanvas.drawCircle(50,50,50, myPaint);

                Path myPath = new Path();
                int i,k;
                int division_x = scrWidth/11;
                int division_y = scrHeight/11;
                for (i=0; i <= scrWidth; i= i+division_x) {
                        myPath.moveTo(i, 0);
                        myPath.lineTo(i, scrHeight);
                        //Log.i("Draw grid x", Integer.toString(i));
                }
                for (k=0; k <= scrHeight; k= k+division_y) {
                    myPath.moveTo(0, k);
                    myPath.lineTo(scrWidth, k);
                    Log.i("Draw grid y", Integer.toString(k));
                }

                tempcanvas.drawPath(myPath, myPaint);

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




}
