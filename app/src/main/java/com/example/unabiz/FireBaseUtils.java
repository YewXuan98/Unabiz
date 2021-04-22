package com.example.unabiz;

import android.util.Log;


import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FireBaseUtils {
    static DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();

    public static void retrievekeys(final listCallbackInterface callbackAction) {

        final List<String> ap_mac = new ArrayList<>();



        myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot key_snapshot : snapshot.getChildren()) {

                   ap_mac.add(key_snapshot.getKey());

                }

                int APLatestentry = 0;


                for (int i = 0; i < ap_mac.size() ; i++) {
                    String currentkey = ap_mac.get(i);

                    if(currentkey.substring(0,1) == "AP"){
                        Log.i("AP latest", "Sucess check AP");
                    }

                }




                Log.i("ap_mac", ap_mac.toString());
                //Log.i("key_mappings", key_mapping.toString());

                    callbackAction.onCallback(ap_mac);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void retrieveAP_coordinates(final AP_coordinatesCallbackInterface callbackAction) {

        final HashMap<String, HashMap<String,Integer>> coordinates = new HashMap<>();
        final HashMap<String,HashMap<String,Integer>> mac_rssi = new HashMap<>();
        final ArrayList<String> mac_addresses_list = new ArrayList<>();

        myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.i("inside_main","inside_main");
                for (DataSnapshot key_snapshot : snapshot.getChildren()) {

                    //initialise the ArrayList containing all MAC Addresses
                    if (key_snapshot.getKey().contains("WIFI")) {
                        for (DataSnapshot mac_addr : key_snapshot.getChildren()) {
                            if (mac_addr.getKey().contains(":")){
                                mac_addresses_list.add(mac_addr.getKey());
                            } } } }

                for (DataSnapshot key_snapshot : snapshot.getChildren()) {
                    //Log.i("children",key_snapshot.toString());

                    HashMap<String,Integer> coordinates_inner = new HashMap<>();
                    HashMap<String,Integer> mac_rssi_inner = new HashMap<>();

                    for (DataSnapshot subkey_xy : key_snapshot.getChildren()){

                        //Log.i("subkey_xy",subkey_xy.toString());
                        //Log.i("x_coordinates_barrier1", x_coo.toString());

                        if(subkey_xy.getKey().equals("x")){
                            Integer x_coo = Integer.parseInt(subkey_xy.getValue().toString());
                            coordinates_inner.put(subkey_xy.getKey(),  x_coo);
                        }

                        if(subkey_xy.getKey().equals("y")){
                            Integer y_coo = Integer.parseInt(subkey_xy.getValue().toString());
                            coordinates_inner.put(subkey_xy.getKey(),  y_coo);
                        }

                        if(!(subkey_xy.getKey().equals("x") || subkey_xy.getKey().equals("y"))){
                            Integer rssi_val = Integer.parseInt(subkey_xy.getValue().toString());
                            mac_rssi_inner.put(subkey_xy.getKey(),rssi_val);

                        }
                    }


                    String key = key_snapshot.getKey();
                    if(!(key.contains("WIFI"))){
                        coordinates.put(key, coordinates_inner);
                        mac_rssi.put(key,mac_rssi_inner);
                    }

                    //Log.i("coordinates_mappings", coordinates.toString() + "\n");

                }
                //Log.i("coordinates_mappings", coordinates.toString() + "\n");
                //for (String ap_key:mac_rssi.keySet()){
                //    Log.i(ap_key, mac_rssi.get(ap_key).toString() + "\n");
                //}
                DataParser dp = new DataParser(coordinates, mac_rssi,mac_addresses_list);
                dp.parse();

                int hiddenLayerSize = 100;
                int epoch = 100;

                NeuralNetwork nn = NeuralNetwork.getInstance();
                nn.setParameters(dp.input_x[0].length,hiddenLayerSize,dp.input_y[0].length);
                nn.setupForTest(mac_addresses_list);
                nn.fit(dp.input_x, dp.input_y, epoch);
                Log.i("NN_Setup","Finish NN Training");

                //end of nn training


                callbackAction.onCallback(coordinates,mac_rssi,mac_addresses_list);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    interface listCallbackInterface{
        void onCallback(List<String> wifipoints);
    }

    interface AP_coordinatesCallbackInterface{
        void onCallback(HashMap<String, HashMap<String, Integer>> stringHashMapHashMap, HashMap<String, HashMap<String, Integer>> coordinates, ArrayList<String> mac_addresses_list);
    }


}

