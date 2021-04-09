package com.example.unabiz;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DataParser {
    HashMap<String, HashMap<String,Integer>> coordinates;
    HashMap<String,HashMap<String,Integer>> mac_rssi;
    ArrayList<String> mac_addresses_list = new ArrayList<>();
    int[] rssiValues;
    String[] location_list;
    int mac_names_total;
    double[][] input_x;
    double[][] input_y;
    double[][] input_x_train;
    double[][] input_y_train;
    double[][] input_x_test;
    double[][] input_y_test;
    double[] output;
    int map_size = 144;
    int training_number = 0;

    public DataParser(HashMap<String, HashMap<String,Integer>> coordinates,
                      HashMap<String,HashMap<String,Integer>> mac_rssi,
                      ArrayList<String> mac_addresses_list){
        this.coordinates = coordinates;
        this.mac_rssi = mac_rssi;
        this.mac_addresses_list = mac_addresses_list;
        location_list = coordinates.keySet().toArray(new String[0]);
        mac_names_total = mac_addresses_list.size();
        setup_input();
    }

    private void setup_input() {
        input_x = new double[location_list.length][mac_names_total];
        input_y = new double[location_list.length][144];
        //input_x_train = new double[location_list.length-training_number][mac_names_total];
        //input_y_train = new double[location_list.length-training_number][144];
        //input_x_test = new double[training_number][mac_names_total];
        //input_y_test = new double[training_number][144];
        Log.i("location_total", String.valueOf(location_list.length));
        Log.i("mac_names_total", String.valueOf(mac_names_total));
    }

    public void parse(){
        int counter = 0;

        for (String ap_name:coordinates.keySet()){
            HashMap<String,Integer> coordinate = coordinates.get(ap_name);
            HashMap<String,Integer> mac_address = mac_rssi.get(ap_name);

            for (int i=0; i<mac_names_total;i++){
                Integer value = mac_address.get(mac_addresses_list.get(i));
                if (value!=null){
                    input_x[counter][i] = (double)value ;
                }

            }

            int coordinate_to_index = coordinate.get("x")*12+coordinate.get("y");
            input_y[counter][coordinate_to_index] = 1;

            counter++;

            //Log.i("key",ap_name);
            //Log.i("coor",coordinate.toString());
            //Log.i("mac_address",mac_address.toString());

        }




        /*for (int i=0;i<location_list.length;i++){
            String row = "";
            for (int j=0;j<144;j++){
                row = row + ", " + String.valueOf(input_y[i][j]);
            }
            Log.i("row",row);
        }

        for (int i=0;i<location_list.length;i++){
            String row = "";
            for (int j=0;j<mac_names_total;j++){
                row = row + ", " + String.valueOf(input_x[i][j]);
            }
            Log.i("row_loc",row);
        }*/
    }

}
