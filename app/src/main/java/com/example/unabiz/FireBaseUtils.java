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

public class FireBaseUtils {
    static DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();

    public static void retrievekeys(final listCallbackInterface callbackAction) {

        final HashMap<String,Integer> ap_mac = new HashMap<>();
        final HashMap<String, HashMap<String,Integer>> key_mapping = new HashMap<>();

        myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot key_snapshot : snapshot.getChildren()) {
                    //Log.i("key_key" , key_snapshot.getKey());
                    if (!ap_mac.isEmpty()){
                    ap_mac.clear();}
                    for (DataSnapshot subkey : key_snapshot.getChildren()){

                       //Log.i("subkey_key" , subkey.getKey());
                       //Log.i("subkey_" , (subkey.getValue()).toString());

                        ap_mac.put(subkey.getKey(),  Integer.parseInt(subkey.getValue().toString()));
                        ap_mac.remove("x");
                        ap_mac.remove("y");

                    }
                    key_mapping.put(key_snapshot.getKey(),ap_mac);
                    //Log.i("key_mappings", key_mapping.toString() + "\n");

                }
                //Log.i("ap_mac", ap_mac.toString());
                //Log.i("key_mappings", key_mapping.toString());

                    callbackAction.onCallback(key_mapping);

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
        void onCallback(HashMap<String, HashMap<String,Integer>> wifipoints);
    }

    interface AP_coordinatesCallbackInterface{
        void onCallback(HashMap<String, HashMap<String, Integer>> stringHashMapHashMap, HashMap<String, HashMap<String, Integer>> coordinates, ArrayList<String> mac_addresses_list);
    }

}

