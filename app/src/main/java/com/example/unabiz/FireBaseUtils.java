package com.example.unabiz;

import android.util.Log;


import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        final HashMap<String, HashMap<String,Integer>> coordinates_2 = new HashMap<>();

        myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot key_snapshot : snapshot.getChildren()) {
                    //Log.i("children",key_snapshot.toString());
                    //if (!(key_snapshot.getKey() == "WIFI")) {
                    //    if (!coordinates_1.isEmpty()){
                    //        coordinates_1.clear();
                    //    Log.i("clear","clear");
                    //    }
                    //}

                    HashMap<String,Integer> coordinates_1 = new HashMap<>();

                    for (DataSnapshot subkey_xy : key_snapshot.getChildren()){

                        //Log.i("subkey_xy",subkey_xy.toString());

                        //Log.i("x_coordinates_barrier1", x_coo.toString());

                        if(subkey_xy.getKey().equals("x")){

                            Integer x_coo = Integer.parseInt(subkey_xy.getValue().toString());
                            //Log.i("x_coordinates_barrier2", x_coo.toString());
                            coordinates_1.put(subkey_xy.getKey(),  x_coo);
                            Log.i("x_coordinates1", coordinates_1.toString());
                            Log.i("x_check",coordinates_2.toString());


                        }

                        if(subkey_xy.getKey().equals("y")){

                            Integer y_coo = Integer.parseInt(subkey_xy.getValue().toString());
                            //Log.i("y_coordinates_barrier2", y_coo.toString());
                            coordinates_1.put(subkey_xy.getKey(),  y_coo);
                            Log.i("y_coordinates1", coordinates_1.toString());
                            Log.i("y_check",coordinates_2.toString());
                        }

                        //Log.i("subkey_xy", subkey_xy.getKey());
                        //Log.i("coordinates_1", coordinates_1.toString());
                        //Log.i("coordinates1", coordinates_1.toString());
                        //Log.i("final_check",coordinates_2.toString());

                    }
                    if(!coordinates_2.containsKey(key_snapshot.getKey())){
                        Log.i("log","key is already inside");
                        Log.i("log_key",key_snapshot.getKey());
                        Log.i("log_xy",coordinates_1.toString());
                        String key = key_snapshot.getKey();
                        Log.i("check",coordinates_2.toString());
                    coordinates_2.put(key, coordinates_1);}

                    Log.i("coordinates_mappings", coordinates_2.toString() + "\n");

                }
                //Log.i("coordinates_mappings", coordinates_2.toString() + "\n");

                callbackAction.onCallback(coordinates_2);

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
        void onCallback(HashMap<String,HashMap<String,Integer>> coordinates);
    }

}

