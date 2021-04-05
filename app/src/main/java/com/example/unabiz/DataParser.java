package com.example.unabiz;

import java.util.HashMap;

public class DataParser {
    HashMap<String, Integer> rawData;
    String[] bssids;
    int[] rssiValues;

    private DataParser(String[] bssids, int[] rssiValues){
        this.bssids = bssids;
        this.rssiValues = rssiValues;
    }

    public int getSize(){
        return this.bssids.length;
    }

}
