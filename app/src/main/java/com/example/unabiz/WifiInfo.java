package com.example.unabiz;

public class WifiInfo {

    public String SSID;
    public String BSSID; //MAC_ADDRESS
    public Integer RSSI;

    public WifiInfo() {}

    public WifiInfo(String SSID,String BSSID, Integer RSSI) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.RSSI = RSSI;
    }
}
