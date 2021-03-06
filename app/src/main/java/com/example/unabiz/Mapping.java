package com.example.unabiz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class Mapping extends AppCompatActivity {
    ImageView PreviewImageMap;
    Bitmap myBitMap = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(myBitMap);
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    String IMAGE_KEY = "image";
    EditText x_entry;
    EditText y_entry;
    Button map_to_database;
    WifiManager wifiManager;
    List<ScanResult> mywifilist2;
    MainActivity.WifiReceiver wifiReceiver;
    private StringBuilder sbs = new StringBuilder();
    private static final int PICK_IMAGE_REQUEST =1;
    public Uri mImageUri;
    int count = 0;

    //mapping grids
    int scrWidth, scrHeight;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

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
        String img = intent.getStringExtra(IMAGE_KEY);
        Log.i("URL STRING", img);
        Log.i("URL STRING", "Image successfully parse");

        Mapping.LoadImage loadImage = new Mapping.LoadImage(PreviewImageMap);
        loadImage.execute(img);
        canvas.drawCircle(50,50,10, paint);
        PreviewImageMap.setImageBitmap(myBitMap);




    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public LoadImage(ImageView PreviewImageMap) {
            this.imageView = PreviewImageMap;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String URLlink = strings[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(URLlink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            PreviewImageMap.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            System.out.println(mImageUri.toString());

            Picasso.get().load(mImageUri).into(PreviewImageMap);
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

    public class MapView extends View {
        MapView(Context context) {
            super(context);
        }

        //called when view is assigned a size

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            //find screen height and screen width
            scrHeight = h - getPaddingBottom() - getPaddingTop();
            scrWidth = w - getPaddingLeft() - getPaddingRight();
        }

        @Override
        protected void onDraw(Canvas mapcanvas) {
            super.onDraw(mapcanvas);

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inMutable = true;
            Bitmap mapBM = BitmapFactory.decodeResource(getResources(), R.id.PreviewImageMap, opt);
            Canvas canv = new Canvas(mapBM); //initialise new canvas to draw onto this image bitmap

            //properties for the path to be drawn
            Paint myPaint = new Paint();
            myPaint.setColor(Color.RED);
            myPaint.setStrokeWidth(15);
            myPaint.setStyle(Paint.Style.STROKE);
            canv.drawCircle(8,8,4,myPaint);
            mapcanvas.drawBitmap(Bitmap.createScaledBitmap(mapBM,scrWidth,scrHeight,false), 0,0,null);



        }
    }


}
