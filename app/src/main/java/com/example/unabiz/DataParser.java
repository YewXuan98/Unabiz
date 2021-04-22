package com.example.unabiz;

import android.net.wifi.ScanResult;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    HashMap<String, HashMap<String,Integer>> coordinates;
    HashMap<String,HashMap<String,Integer>> mac_rssi;
    ArrayList<String> mac_addresses_list;
    String[] location_list;
    double[][] input_x;
    double[][] input_y;


    double[] output;
    int map_length=12;
    int map_size = 144;

    public DataParser(){

    }
    public DataParser(HashMap<String, HashMap<String,Integer>> coordinates,
                      HashMap<String,HashMap<String,Integer>> mac_rssi,
                      ArrayList<String> mac_addresses_list){
        this.coordinates = coordinates;
        this.mac_rssi = mac_rssi;
        this.mac_addresses_list = mac_addresses_list;
        location_list = coordinates.keySet().toArray(new String[0]);

        input_x = new double[location_list.length][mac_addresses_list.size()];
        input_y = new double[location_list.length][map_size];

        //Log.i("location_total", String.valueOf(location_list.length));
        //Log.i("mac_names_total", String.valueOf(mac_addresses_list.size()));
    }


    public void parse(){
        int counter = 0;

        for (String ap_name:coordinates.keySet()){
            HashMap<String,Integer> xy_coordinate = coordinates.get(ap_name);
            HashMap<String,Integer> mac_address = mac_rssi.get(ap_name);

            for (int i=0; i<mac_addresses_list.size();i++){
                Integer value = mac_address.get(mac_addresses_list.get(i));

                if (value!=null){
                    input_x[counter][i] = (double)value ;
                }
            }

            int coordinate_to_index = xy_coordinate.get("x")*12+xy_coordinate.get("y");
            input_y[counter][xy_coordinate_to_index(xy_coordinate)] = 1;

            counter++;

        }
        //Log.i("DPParse","Parsing is done");
    }

    public double[] parse_test(List<ScanResult> current_wifi_list,ArrayList<String> references) {
        HashMap<String, Integer> wifiHashMap = new HashMap<>();
        double[] input_x_test = new double[references.size()];
        for (int i = 0; i < current_wifi_list.size(); i++) {
            String bssid = current_wifi_list.get(i).BSSID;
            Integer rssi = current_wifi_list.get(i).level;

            wifiHashMap.put(bssid, rssi);
        }

        for (int i = 0; i < references.size(); i++) {
            Integer value = wifiHashMap.get(references.get(i));
            Log.i("Iteration", String.valueOf(i));
            Log.i("Value", String.valueOf(value));
            if (value != null) {
                input_x_test[i] = (double) value;
            }
            else{
                input_x_test[i] = 0;
            }
        }
        return input_x_test;
    }

    //TODO INDEX_TO_POINT
    public int getX(int point){
        return point%map_length;
    }

    //TODO INDEX_TO_POINT
    public int getY(int point){
        return point/map_length;
    }

    public int xy_coordinate_to_index(HashMap<String,Integer> xy_coordinate){
        return xy_coordinate.get("y")*map_length+xy_coordinate.get("x");
    }

    public int array_find_max(List<Double> output){
        double max = Double.NEGATIVE_INFINITY;
        int largestIndex = 0;

        for (int i=0;i<output.size();i++){
            if (output.get(i)>max){
                max = output.get(i);
                largestIndex=i;
            }
        }
        return largestIndex;
    }
}
