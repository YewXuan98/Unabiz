package com.example.unabiz;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FireBaseUtils {
    static DatabaseReference myDatabaseRef = FirebaseDatabase.getInstance().getReference();

    public static void retrievekeys(final listCallbackInterface callbackAction) {
        final ArrayList<String> keylist = new ArrayList<>();
        final ArrayList<String> subkeylist = new ArrayList<>();



//        final ArrayList<String> keylist2 = new ArrayList<>();
//        final ArrayList<String> subkeylist2 = new ArrayList<>();

        final HashMap<String, String > ap_mac = new HashMap<>();


        myDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot key_snapshot : snapshot.getChildren()) {

                    String key = key_snapshot.getKey();
                    Log.i("key_snapshot",key_snapshot.toString());

                    String value = String.valueOf(key_snapshot.child(key));
                    keylist.add(key);
                    Log.i("keylist",keylist.toString());

                    subkeylist.add(value);
                    Log.i("subkeylist",subkeylist.toString());

                    ap_mac.put(key,value);


                }



                Log.i("Hashmap",ap_mac.toString());




//                String snap = snapshot.getKey();
//                keylist.add(snap);
//                System.out.println(keylist);
                HashMap<String, ArrayList > ap_mac = new HashMap<>();

//                for (DataSnapshot key_snapshot : snapshot.getChildren()){
//                    String key = key_snapshot.getKey();
//
//                    for(DataSnapshot mac_snapshot: key_snapshot.getChildren() ){
//
//                        String mac = mac_snapshot.getKey();
//                        keylist.add(mac);
//                        //String value = String.valueOf(mac_snapshot.getValue());
//                        //Log.i("Hashmap", mac);
//                        ap_mac.put(key,keylist);
//                    }
//                    //Log.i("Hashmap", ap_mac.toString());
//
//                }
                    callbackAction.onCallback(keylist);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    public static void retrieveAP_coordinates(final AP_coordinatesCallbackInterface callbackAction) {
//        final HashMap<String,HashMap<Integer,Integer>> AP_coordinates_map = new HashMap<>();
//        final HashMap<String,Point> allLocationcoordinates = new HashMap<>();
//
//    }

    interface listCallbackInterface{
        void onCallback(List<String> wifipoints);
    }

//    interface AP_coordinatesCallbackInterface{
//        void onCallback(HashMap<String,HashMap<Integer,Integer>>);
//    }

}

