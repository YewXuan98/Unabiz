package com.example.unabiz;

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
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    WifiReceiver wifiReceiver;
    ListAdapter listAdapter;
    ListView wifilist;
    List<ScanResult> mywifilist;
    ToggleButton scanWifi_button;
    private static final int MY_REQUEST_CODE = 123;
    private static final String LOG_TAG = "Yew Xuan";

    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifilist = (ListView)findViewById(R.id.myListView);
        wifiManager =(WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();

        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        scanWifi_button = findViewById(R.id.start_scan);

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
        setAdapter();
        System.out.println("Scanning starts");
    }

    private void stopScanWifi()  {
        mywifilist = Collections.emptyList();
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