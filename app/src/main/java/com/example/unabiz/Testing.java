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


/*import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;*/

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;


public class Testing extends AppCompatActivity {
    ImageView PreviewImageMap;
    String IMAGE_KEY = "image";

    //Buttons on the testing page
    Button button_mapping;
    Button button_testing;
    Button Scan_mode;

    private StringBuilder sbs = new StringBuilder();
    private static final int PICK_IMAGE_REQUEST = 1;
    public Uri mImageUri;
    int count = 0;
    public String imgURL;

    //zooming
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    //mapping grids
    int scrWidth, scrHeight;

    /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    final StorageReference storageReference = FirebaseStorage.getInstance().getReference();*/

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.testing_mode);

        PreviewImageMap = findViewById(R.id.PreviewImage);
        //mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        button_mapping = findViewById(R.id.button_mapping);
        button_testing = findViewById(R.id.button_testing);
        Scan_mode = findViewById(R.id.Scan_mode);


        button_mapping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Mapping_mode.class);
                startActivity(intent);
            }
        });

        Scan_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(activity2Intent);
            }
        });

        //draw circle as location of user
        button_testing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Bitmap bmp = Bitmap.createBitmap(180,180,Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bmp);

                Paint myPaint = new Paint();
                myPaint.setColor(0xffcccccc);
                myPaint.setAntiAlias(true);
                myPaint.setStrokeWidth(10);
                myPaint.setStyle(Paint.Style.STROKE);
                //tempcanvas.drawCircle(10,10,10, myPaint);
                canvas.drawCircle(20,20,1, myPaint);
                PreviewImageMap.setImageBitmap(bmp);*/
            }
        });


        /*GET IMAGE FROM MAPPING INTO TESTING MODE */


        //below is similar to what is in Mapping
        Intent intent = getIntent();
        imgURL = intent.getStringExtra(IMAGE_KEY);
        System.out.println("URL STRING gotten on testing mode side: " + imgURL);
        Testing.LoadImage loadImage = new Testing.LoadImage(PreviewImageMap);
        loadImage.execute(imgURL);


    }
    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            Log.i("IV_dimens", String.valueOf(PreviewImageMap.getWidth()));
            Log.i("IV_dimens", String.valueOf(PreviewImageMap.getHeight()));

            PreviewImageMap.setScaleX(mScaleFactor);
            PreviewImageMap.setScaleY(mScaleFactor);
            return true;
        }
    }*/


    class LoadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        Bitmap tempBitmap;

        public LoadImage(ImageView PreviewImage) {
            this.imageView = PreviewImageMap;
        }

        final FireBaseUtils.listCallbackInterface list_of_wifi_points = new FireBaseUtils.listCallbackInterface() {
            @Override
            public void onCallback(HashMap<String, HashMap<String,Integer>> wifipoints) {

            }
        };
        final FireBaseUtils.AP_coordinatesCallbackInterface coordinatesCallbackInterface = new FireBaseUtils.AP_coordinatesCallbackInterface() {
            @Override
            public void onCallback(HashMap<String, HashMap<String, Integer>> coordinates) {

            }
        };

        @Override
        public Bitmap doInBackground(String... strings) {
            String URLlink = strings[0];
            Bitmap bitmap = null;

            try {
                //this is for URL link
                InputStream inputStream = new java.net.URL(URLlink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                //scrHeight = bitmap.getHeight();
                //scrWidth = bitmap.getWidth();

                tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempcanvas = new Canvas(tempBitmap);

                Paint myPaint = new Paint();
                myPaint.setColor(0xffcccccc);
                myPaint.setAntiAlias(true);
                myPaint.setStrokeWidth(10);
                myPaint.setStyle(Paint.Style.STROKE);

                //Draw the image bitmap into canvas
                tempcanvas.drawBitmap(bitmap, 0, 0, null);

                //retrieve coordinates from firebase
                //FireBaseUtils.retrievekeys(list_of_wifi_points);
                FireBaseUtils.retrieveAP_coordinates(coordinatesCallbackInterface);

                //Feed into Driver to get coordinates

                //final coordinates send to test button for drawing
                //line 161 and l62 return the x, y coordinates in firebaseutils
                tempcanvas.drawCircle(20,20,1, myPaint);




                /*Path myPath = new Path();
                int i, k;
                int division_x = scrWidth / 11;
                int division_y = scrHeight / 11;
                for (i = 0; i <= scrWidth; i = i + division_x) {
                    myPath.moveTo(i, 0);
                    myPath.lineTo(i, scrHeight);
                    Log.i("Draw grid x", Integer.toString(i));
                }
                for (k = 0; k <= scrHeight; k = k + division_y) {
                    myPath.moveTo(0, k);
                    myPath.lineTo(scrWidth, k);
                    Log.i("Draw grid y", Integer.toString(k));
                }
                tempcanvas.drawPath(myPath, myPaint);*/

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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();


        } else {
            Log.i("Failed activity", "Failed activity");
        }
    }
}

