package com.example.unabiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import android.widget.Toast;

import com.google.android.gms.common.util.Base64Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Util;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    WifiReceiver wifiReceiver;
    ListAdapter listAdapter;
    ListView wifilist;
    List<ScanResult> mywifilist;
    ToggleButton scanWifi_button;
    Button scan_mode;
    Button mapping_mode;
    Button testing_mode;
    private static final int MY_REQUEST_CODE = 123;
    private static final String LOG_TAG = "Yew Xuan";
    private StringBuilder sb = new StringBuilder();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifilist = (ListView)findViewById(R.id.myListView);
        scanWifi_button = findViewById(R.id.start_scan);
        scan_mode = findViewById(R.id.button_scanmode);
        mapping_mode= findViewById(R.id.button_mappingmode);
        testing_mode = findViewById(R.id.button_testmode);

        wifiManager =(WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();

        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //enable Wifi if not ON
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        scanWifi_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    //toggle enabled
                    scanWifiList();

                } else {
                    //toggle is disabled
                    stopScanWifi();
                }
            }
        });

        mapping_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Mapping_mode.class);
                startActivity(intent);
            }
        });

    }

    private void scanWifiList() {
        // With Android Level >= 23, you have to ask the user
        // for permission to Call.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 23
            int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            // Check for permissions
            if (permission1 != PackageManager.PERMISSION_GRANTED) {

                Log.d(LOG_TAG, "Requesting Permissions");

// Request permissions
                ActivityCompat.requestPermissions(this,
                        new String[] {
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.ACCESS_NETWORK_STATE
                        }, MY_REQUEST_CODE);
                return;
            }
            Log.d(LOG_TAG, "Permissions Already Granted");
        }
        this.doStartScanWifi();
    }

    private void doStartScanWifi()  {
        this.wifiManager.startScan();
        mywifilist =  wifiManager.getScanResults();
        sb.append("\n Number of Wifi Connections: " + " " + mywifilist.size() + "\n\n");
        ;
        for (int i=0; i < mywifilist.size(); i++) {
            String bssid = mywifilist.get(i).BSSID;
            String ssid = mywifilist.get(i).SSID.replace('.', '1'); //replace . with 1
            Integer rssi = mywifilist.get(i).level;
            sb.append(new Integer(i+1).toString() + ".");
            sb.append(String.format("Name: %s,\nBSSID: %s,\nRSSI: %s\n",ssid,bssid,rssi));
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.hasChild(ssid)){databaseReference.child("WIFI").child(ssid).setValue(rssi.toString());}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            //Log.i("WIFI START LIST", mywifilist.get(i).SSID);
            //Log.i("WIFI START LIST", Integer.toString(mywifilist.get(i).level));
        }



        System.out.println(sb);
        setAdapter();
        System.out.println("Scanning starts");
    }

    private void stopScanWifi()  {
        mywifilist = wifiManager.getScanResults();
        setAdapter();
        System.out.println("Scanning stop");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)  {
        Log.d(LOG_TAG, "onRequestPermissionsResult");

        switch (requestCode)  {
            case MY_REQUEST_CODE:  {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)  {
                    // permission was granted
                    Log.d(LOG_TAG, "Permission Granted: " + permissions[0]);

                    // Start Scan Wifi.
                    this.doStartScanWifi();
                }  else   {
                    // Permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(LOG_TAG, "Permission Denied: " + permissions[0]);
                }
                break;
            }
            // Other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void setAdapter() {

        listAdapter = new ListAdapter(getApplicationContext(), mywifilist);
        wifilist.setAdapter(listAdapter);
    }


    class WifiReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }





}