package com.example.unabiz;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapping_mode_2);

        PreviewImageMap = findViewById(R.id.PreviewImageMap);
        x_entry = findViewById(R.id.x_Entry);
        y_entry = findViewById(R.id.y_Entry);
        map_to_database = findViewById(R.id.map_to_database_button);

        String x_coor = String.valueOf(x_entry);
        String y_coord = String.valueOf(y_entry);

        map_to_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(x_coor.isEmpty() || y_coord.isEmpty()) {
                        Log.i("x_coor", x_coor);
                        Log.i("y_coor", y_coord);
                        Toast.makeText(Mapping.this, "Please enter both coordinates", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("x_coor", x_coor);
                        Log.i("y_coor", y_coord);
                        doStartScanWifi();
                    }


            }
        });

        /*TO WORK ON PASING THE IMAGE FROM ONW INTENT TO ANOTHER
        Intent intent = getIntent();
        String img = intent.getStringExtra(IMAGE_KEY);
        Uri fileUri = Uri.parse(img);
        Log.i("Yew Xuan", "Image successfully parse");
        Picasso.get().load(fileUri).into(PreviewImageMap);
        */
    }

    private void doStartScanWifi()  {
        this.wifiManager.startScan();
        mywifilist2 =  wifiManager.getScanResults();
        //sbs.append("\n Number of Wifi Connections: " + " " + mywifilist2.size() + "\n\n");
        ;
        for (int i=0; i < mywifilist2.size(); i++) {
            String bssid = mywifilist2.get(i).BSSID;
            String ssid = mywifilist2.get(i).SSID.replace('.', '1'); //replace . with 1
            String AP_name = "AP" + i;
            String x_coor = x_entry.getText().toString();
            String y_coord = y_entry.getText().toString();
            Integer rssi = mywifilist2.get(i).level;
            //sbs.append(new Integer(i+1).toString() + ".");
            //sbs.append(String.format("Name: %s,\nBSSID: %s,\nRSSI: %s\n",ssid,bssid,rssi));
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.hasChild(AP_name)){

                        databaseReference.setValue(x_coor);


//                        databaseReference.child("WIFI_MAP").child(AP_name).child("x").setValue(x_coor);
//                        databaseReference.child("WIFI_MAP").child(AP_name).child("y").setValue(y_coord);
//                        databaseReference.child("WIFI_MAP").child(AP_name).child(ssid).child(bssid);
//                        databaseReference.child("WIFI_MAP").child(AP_name).child(ssid).child(rssi.toString());

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            x_entry.setText("");
            y_entry.setText("");


            //Log.i("WIFI START LIST", mywifilist.get(i).SSID);
            //Log.i("WIFI START LIST", Integer.toString(mywifilist.get(i).level));
        }

        System.out.println(sbs);
        System.out.println("Scanning starts: MAP MODE");
    }
}
